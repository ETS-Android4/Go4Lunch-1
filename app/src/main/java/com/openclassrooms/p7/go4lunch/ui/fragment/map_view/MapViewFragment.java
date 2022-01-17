package com.openclassrooms.p7.go4lunch.ui.fragment.map_view;


import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.ContentValues.TAG;
import static com.openclassrooms.p7.go4lunch.ui.activity.MainActivity.hideKeyboard;
import static com.openclassrooms.p7.go4lunch.ui.activity.MainActivity.showKeyboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.openclassrooms.p7.go4lunch.BuildConfig;
import com.openclassrooms.p7.go4lunch.R;
import com.openclassrooms.p7.go4lunch.ViewModelFactory;
import com.openclassrooms.p7.go4lunch.databinding.FragmentMapViewBinding;
import com.openclassrooms.p7.go4lunch.model.Restaurant;
import com.openclassrooms.p7.go4lunch.model.User;
import com.openclassrooms.p7.go4lunch.ui.activity.DetailActivity;
import com.openclassrooms.p7.go4lunch.ui.UserAndRestaurantViewModel;
import com.openclassrooms.p7.go4lunch.ui.login.LoginActivity;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by lleotraas on 14.
 */
public class MapViewFragment extends Fragment implements OnMapReadyCallback,
                                                         GoogleMap.OnMarkerClickListener,
                                                         MapViewAutocompleteAdapter.ClickListener{


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
    private boolean isAlreadyNearbySearched;
    private FragmentMapViewBinding mBinding;
    private MapViewAutocompleteAdapter mMapViewAutocompleteAdapter;
    private RecyclerView mRecyclerView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentMapViewBinding.inflate(inflater, container, false);
        View result = mBinding.getRoot();
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }
        this.createMap(savedInstanceState, result);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.requireActivity().getApplicationContext());
        Places.initialize(requireActivity().getApplicationContext(), BuildConfig.GMP_KEY);
        this.configureServiceAndViewModel();
        if (!mViewModel.isCurrentUserLogged()) {
            startActivity(new Intent(requireActivity(), LoginActivity.class));
        }
        mViewModel.getCurrentFirestoreUser().observe(getViewLifecycleOwner(), user -> {
            if (!user.getUid().equals(mViewModel.getCurrentFirebaseUser().getUid())) {
                startActivity(new Intent(requireActivity(), LoginActivity.class));
            }
        });
        this.configureListeners();
        this.createAutocomplete();
        this.setHasOptionsMenu(true);
        return result;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void configureServiceAndViewModel() {
        mViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(UserAndRestaurantViewModel.class);
    }

    private void putMarkerOnMap() {
        mViewModel.getIsAlreadyNearbySearched().observe(getViewLifecycleOwner(), aBoolean -> isAlreadyNearbySearched = aBoolean);
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
                    if (restaurant.isSearched()) {
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(restaurant.getPosition()));
                    }
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.toolbar_search_btn) {
            if (mBinding.placeSearch.getVisibility() == View.GONE) {
                mBinding.placeSearch.setVisibility(View.VISIBLE);
                mBinding.placesRecyclerView.setVisibility(View.VISIBLE);
                mBinding.placeSearch.requestFocus();
                showKeyboard(requireActivity());
            } else {
                mBinding.placeSearch.setVisibility(View.GONE);
                mBinding.placesRecyclerView.setVisibility(View.GONE);
                hideKeyboard(requireActivity(), mBinding.placeSearch);
            }
        }
        return super.onOptionsItemSelected(item);
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
        if (mViewModel.isCurrentUserLogged()) {
            this.putMarkerOnMap();
        }
    }


    @SuppressWarnings("deprecation")
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
        mViewModel.placeTaskExecutor(url);
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

    @SuppressLint("NotifyDataSetChanged")
    private void createAutocomplete() {
        mRecyclerView = mBinding.placesRecyclerView;
        mBinding.placeSearch.addTextChangedListener(filterTextWatcher);
        mMapViewAutocompleteAdapter = new MapViewAutocompleteAdapter(requireContext());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        mMapViewAutocompleteAdapter.setClickListener(this);
        mRecyclerView.setAdapter(mMapViewAutocompleteAdapter);
        mMapViewAutocompleteAdapter.notifyDataSetChanged();
    }

    private final TextWatcher filterTextWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable editable) {
            if (!editable.toString().equals("")) {
                mMapViewAutocompleteAdapter.getFilter().filter(editable.toString());
                if (mRecyclerView.getVisibility() == View.GONE) {mRecyclerView.setVisibility(View.VISIBLE);}
            } else {
                if (mRecyclerView.getVisibility() == View.VISIBLE) {mRecyclerView.setVisibility(View.GONE);}
            }
        }
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }
    };

    @Override
    public void click(List<String> listOfPlaceId) {
        mViewModel.requestForPlaceDetails(listOfPlaceId, requireContext(), true);
        mBinding.placeSearch.setVisibility(View.GONE);
        mBinding.placesRecyclerView.setVisibility(View.GONE);
        hideKeyboard(requireActivity(), mBinding.placeSearch);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void configureListeners() {
        mBinding.placeSearch.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                mBinding.placeSearch.setVisibility(View.GONE);
                mBinding.placesRecyclerView.setVisibility(View.GONE);
                hideKeyboard(requireActivity(), mBinding.placeSearch);
            }
            return false;
        });
    }
}