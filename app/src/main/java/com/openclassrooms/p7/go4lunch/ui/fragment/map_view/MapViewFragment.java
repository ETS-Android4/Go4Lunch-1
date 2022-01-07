package com.openclassrooms.p7.go4lunch.ui.fragment.map_view;


import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.openclassrooms.p7.go4lunch.BuildConfig;
import com.openclassrooms.p7.go4lunch.R;
import com.openclassrooms.p7.go4lunch.ViewModelFactory;
import com.openclassrooms.p7.go4lunch.injector.DI;
import com.openclassrooms.p7.go4lunch.model.Restaurant;
import com.openclassrooms.p7.go4lunch.model.User;
import com.openclassrooms.p7.go4lunch.repository.PlaceTask;
import com.openclassrooms.p7.go4lunch.service.ApiService;
import com.openclassrooms.p7.go4lunch.ui.DetailActivity;
import com.openclassrooms.p7.go4lunch.ui.UserAndRestaurantViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


/**
 * Created by lleotraas on 14.
 */
public class MapViewFragment extends Fragment implements OnMapReadyCallback,
                                                         GoogleMap.OnMarkerClickListener{


    private static final int AUTOCOMPLETE_REQUEST_CODE = 23;
    private GoogleMap mMap = null;
    private Location lastKnownLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private final LatLng defaultLocation = new LatLng(43.406656, 3.684383);
    public static LatLng CURRENT_LOCATION;

    public static final String KEY_LOCATION = "location";
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final int DEFAULT_ZOOM = 16;
    private static final int PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 1;

    private boolean locationPermissionGranted;
    private UserAndRestaurantViewModel mViewModel;
    private ApiService mApiService;
    private boolean isAlreadyNearbySearched;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_map_view, container, false);
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }
        this.createMap(savedInstanceState, result);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.requireActivity().getApplicationContext());
        Places.initialize(requireActivity().getApplicationContext(), BuildConfig.GMP_KEY);
        this.configureServiceAndViewModel();
        setHasOptionsMenu(true);
        return result;
    }

    private void configureServiceAndViewModel() {
        mViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(UserAndRestaurantViewModel.class);
        mApiService = DI.getRestaurantApiService();
    }

    private void putMarkerOnMap() {
        mViewModel.getIsAlreadyNearbySearched().observe(getViewLifecycleOwner(), aBoolean -> {
            isAlreadyNearbySearched = aBoolean;
        });
        mViewModel.getListOfPlaceId().observe(getViewLifecycleOwner(), strings -> {
            if (isAlreadyNearbySearched) {
                mViewModel.requestForPlaceDetails(strings, requireContext(), false);
            }
        });
        List<User> userInterestedList = new ArrayList<>();
        mViewModel.getAllInterestedUsers().observe(getViewLifecycleOwner(), userInterestedList::addAll);
        mViewModel.getAllRestaurants().observe(getViewLifecycleOwner(), restaurants -> {
            if (mMap != null) {
                mMap.clear();
                if (!userInterestedList.isEmpty()) {
                    mViewModel.setNumberOfFriendInterested(userInterestedList, restaurants);
                }
                for (Restaurant restaurant : restaurants) {
                    MarkerOptions markerOptions = setMarkerOnMap(restaurant);
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(setMarkerIcon(restaurant)));
                    mMap.addMarker(markerOptions);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        List<Place.Field> fields = Arrays.asList(Place.Field.ID);
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                .setTypeFilter(TypeFilter.ESTABLISHMENT)
                .setLocationBias(mApiService.getRectangularBound(MapViewFragment.CURRENT_LOCATION))
                .setCountry("FR")
                .setHint("Search restaurant")
                .build(requireContext());
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(Objects.requireNonNull(data));
                List<String> placesId = new ArrayList<>();
                placesId.add(place.getId());
                mViewModel.requestForPlaceDetails(placesId, requireContext(), true);
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(requireContext(), requireContext().getResources().getString(R.string.map_view_fragment_search_canceled), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, lastKnownLocation);
        }

    }

    @SuppressLint("PotentialBehaviorOverride")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        getLocationPermission();
        updateLocationUI();
        getDeviceLocation();
        googleMap.setOnMarkerClickListener(this);
        this.putMarkerOnMap();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationPermissionGranted = false;
        if (requestCode == PERMISSION_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
            }
        }
        updateLocationUI();
    }

    private void getLocationPermission() {
        Context context = this.requireActivity().getApplicationContext();
        if (context != null) {
            if (ContextCompat.checkSelfPermission(context,
                    ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
            } else {
                ActivityCompat.requestPermissions(this.requireActivity(), new String[]{ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        }catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage() );
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        this.putMarkerOnMap();
    }

    private void getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                 @SuppressLint("MissingPermission") Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this.requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        lastKnownLocation = task.getResult();
                        CURRENT_LOCATION = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                        if (lastKnownLocation != null) {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(lastKnownLocation.getLatitude(),
                                            lastKnownLocation.getLongitude()),DEFAULT_ZOOM));
                            if (isAlreadyNearbySearched) {
                                nearbySearch();
                            }
                        }
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.");
                        Log.e(TAG, "Exception: %s", task.getException());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                        mMap.getUiSettings().setMyLocationButtonEnabled(false);
                    }
                });
            }
        }catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    private void createMap(Bundle savedInstanceState, View result) {
        MapView mMapView = result.findViewById(R.id.map);
        mMapView.getMapAsync(this);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        try {
            MapsInitializer.initialize(requireActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        String restaurantId = marker.getSnippet();
        Intent detailIntent = new Intent(requireActivity(), DetailActivity.class);
        detailIntent.putExtra("restaurantId", restaurantId);
        startActivity(detailIntent);
        return false;
    }

    private void nearbySearch() {
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json"
                + "?keyword=restaurant"
                + "&location=" + lastKnownLocation.getLatitude() + "," + lastKnownLocation.getLongitude()
                + "&radius=1500"
                + "&sensor=true"
                + "&key=" + BuildConfig.GMP_KEY;
        mViewModel.setPlaceTaskExecutor(url);
    }

    private MarkerOptions setMarkerOnMap(Restaurant restaurant) {
        MarkerOptions options = new MarkerOptions();
        LatLng latLng = new LatLng(restaurant.getPosition().latitude, restaurant.getPosition().longitude);
        options.position(latLng);
        options.snippet(restaurant.getId());
        return options;
    }

    private int setMarkerIcon(Restaurant restaurant) {
        if (restaurant.isSearched()) {
            return R.drawable.baseline_place_green;
        }
        if (restaurant.getNumberOfFriendInterested() > 0) {
            return R.drawable.baseline_place_cyan;
        }
            return R.drawable.baseline_place_orange;
    }
}