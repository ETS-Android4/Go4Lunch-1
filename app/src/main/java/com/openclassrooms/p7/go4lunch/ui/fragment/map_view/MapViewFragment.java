package com.openclassrooms.p7.go4lunch.ui.fragment.map_view;


import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.openclassrooms.p7.go4lunch.BuildConfig;
import com.openclassrooms.p7.go4lunch.R;
import com.openclassrooms.p7.go4lunch.injector.DI;
import com.openclassrooms.p7.go4lunch.model.Restaurant;
import com.openclassrooms.p7.go4lunch.service.RestaurantApiService;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


/**
 * Created by lleotraas on 14.
 */
public class MapViewFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener {


    private MapView mMapView;
    private GoogleMap mMap = null;
    private Location lastKnownLocation;
    private PlacesClient mPlacesClient;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private final LatLng defaultLocation = new LatLng(43.406656, 3.684383);

    private static final String KEY_LOCATION = "location";
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final int DEFAULT_ZOOM = 16;
    private static final int PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 1;

    private boolean locationPermissionGranted;
    private UserAndRestaurantViewModel mViewModel;
    private RestaurantApiService mApiService;
    private HashMap<String,Restaurant> mRestaurantList;

    public MapViewFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_map_view, container, false);
        mRestaurantList = new HashMap<>();
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }
        this.createMap(savedInstanceState, result);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.requireActivity().getApplicationContext());
        Places.initialize(requireActivity().getApplicationContext(), BuildConfig.GMP_KEY);
        mPlacesClient = Places.createClient(requireActivity().getApplicationContext());
        this.configureViewModelAndService();
        return result;
    }

    private void configureViewModelAndService() {
        mViewModel = new ViewModelProvider(this.requireActivity()).get(UserAndRestaurantViewModel.class);
        mApiService = DI.getRestaurantApiService();
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
        mViewModel.getDeviceLocation(locationPermissionGranted, fusedLocationProviderClient, requireActivity(), mMap);
        googleMap.setOnMarkerClickListener(this);
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

    @Override
    public void onResume() {
        super.onResume();
        for (Restaurant restaurant : mApiService.getRestaurant()){
            LatLng latLng = new LatLng(restaurant.getLatitude(), restaurant.getLongitude());
            setInfoOnMarker(latLng, restaurant.getId());
        }
    }

    public Boolean getLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireActivity(),
                ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
        }
        return locationPermissionGranted;
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
//                mMap.getUiSettings().setMyLocationButtonEnabled(true);
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

    private void createMap(Bundle savedInstanceState, View result) {
        mMapView = result.findViewById(R.id.map);
        mMapView.getMapAsync(this);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        try {
            MapsInitializer.initialize(requireActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Create a restaurant list with the placeId as the key.
    private void createRestaurant(
            String placeId,
            String name,
            String adress,
            double rating,
            String hours,
            float distance,
            String phoneNumber,
            String uriWebsite,
            double latitude,
            double longitude
    ) {
            mRestaurantList.put(placeId, new Restaurant(
                    placeId,
                    name,
                    adress,
                    hours,
                    phoneNumber,
                    uriWebsite,
                    distance,
                    latitude,
                    longitude,
                    0,
                    rating,
                    null
            ));
        mApiService.getRestaurant().add(mRestaurantList.get(placeId));
        mApiService.makeLikedOrSelectedRestaurantList(mRestaurantList.get(placeId));
        LatLng latLng = new LatLng(latitude, longitude);
        setInfoOnMarker(latLng, placeId);
    }



    private void getPlaceDetails(Place place) {
        String openingHours = "no details here";
        double rating = 1.0;
        String uriWebsite = "";
        Log.i(TAG, "Place found: " + place.getName());

        if (place.getOpeningHours() != null) {
            openingHours = makeStringOpeningHours(place.getOpeningHours());
        }

        if (place.getRating() != null) {
            rating = place.getRating();
            Log.i(TAG, "Restaurant name: " + place.getName() + " Rating: " + place.getRating());
        }

        if (place.getWebsiteUri() != null) {
            uriWebsite = String.format("%s",place.getWebsiteUri());
        }
        float[] results = new float[10];
        Location.distanceBetween(
                lastKnownLocation.getLatitude(),
                lastKnownLocation.getLongitude(),
                Objects.requireNonNull(place.getLatLng()).latitude,
                place.getLatLng().longitude,
                results
        );
        createRestaurant(
                place.getId(),
                place.getName(),
                place.getAddress(),
                rating,
                openingHours,
                results[0],
                place.getPhoneNumber(),
                uriWebsite,
                place.getLatLng().latitude,
                place.getLatLng().longitude);
    }

    private void requestForPlacePhoto(String placeId, Place place) {
        if (place.getPhotoMetadatas() != null) {
            PhotoMetadata photoMetadata = place.getPhotoMetadatas().get(0);
            FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                    .build();
            mPlacesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) ->
                    setPlacePhoto(placeId, fetchPhotoResponse.getBitmap())).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    Log.e(TAG, "Place not found: " + exception.getMessage());
                }
            });
        }
    }

    // Add the place image in the hashmap and put the Restaurant with full details needed in the static list.
    private void setPlacePhoto(String placeId, Bitmap placeImage) {
        Objects.requireNonNull(mRestaurantList.get(placeId)).setPictureUrl(placeImage);
    }

    private String makeStringOpeningHours(OpeningHours openingHours) {
        Calendar calendar = Calendar.getInstance(Locale.FRANCE);
        int currentHour = calendar.get(Calendar.HOUR);
        int hours = Objects.requireNonNull(openingHours.getPeriods().get(0).getOpen()).getTime().getHours();
        if (currentHour > hours) {
            return "still closed";
        }else {
            return "open until " + (hours - currentHour) + "pm";
        }
    }

    // Read the url and do a request to find nearby restaurants
    private String downloadUrl (String string) throws IOException {
        URL url = new URL(string);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.connect();
        InputStream stream = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }
        String data = builder.toString();
        reader.close();
        return data;
    }



    private void setInfoOnMarker(LatLng latLng,String placeId) {
        MarkerOptions options = new MarkerOptions();
        options.icon(BitmapDescriptorFactory.fromResource(mApiService.setMarker(placeId)));
        options.position(latLng);
        options.snippet(placeId);
        mMap.addMarker(options);
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        String restaurantId = marker.getSnippet();
        Intent detailIntent = new Intent(requireActivity(), DetailActivity.class);
        detailIntent.putExtra("restaurantId", restaurantId);
        startActivity(detailIntent);
        return false;
    }


}
