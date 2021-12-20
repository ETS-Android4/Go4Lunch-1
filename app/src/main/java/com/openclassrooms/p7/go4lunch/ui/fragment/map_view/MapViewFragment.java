package com.openclassrooms.p7.go4lunch.ui.fragment.map_view;


import static android.Manifest.permission.ACCESS_FINE_LOCATION;
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
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

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
import com.openclassrooms.p7.go4lunch.injector.DI;
import com.openclassrooms.p7.go4lunch.model.Restaurant;
import com.openclassrooms.p7.go4lunch.model.User;
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
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


/**
 * Created by lleotraas on 14.
 */
public class MapViewFragment extends Fragment implements OnMapReadyCallback,
                                                         GoogleMap.OnMarkerClickListener {


    private GoogleMap mMap = null;
    private Location lastKnownLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private final LatLng defaultLocation = new LatLng(43.406656, 3.684383);
    public static LatLng currentLocation;

    public static final String KEY_LOCATION = "location";
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final int DEFAULT_ZOOM = 16;
    private static final int PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 1;

    private boolean locationPermissionGranted;
    private ApiService mApiService;
    UserAndRestaurantViewModel mViewModel;

    public MapViewFragment(){}

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
        return result;
    }

    private void configureServiceAndViewModel() {
        mViewModel = new ViewModelProvider(this.requireActivity()).get(UserAndRestaurantViewModel.class);
        mApiService = DI.getRestaurantApiService();
    }

    private void configureListener() {
//        ((MainActivity) requireActivity()).setOnDataSelected(place -> {
//            mViewModel.requestForPlaceDetails(place.getId(), requireContext(), true);
            //TODO new marker
//        });
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

    @Override
    public void onResume() {
        super.onResume();
        if (mMap != null) {
            mMap.clear();
            mViewModel.getAllRestaurants().observe(getViewLifecycleOwner(), new Observer<List<Restaurant>>() {
                @Override
                public void onChanged(List<Restaurant> restaurants) {
                    for (Restaurant restaurant : restaurants) {
                        MarkerOptions markerOptions = setMarkerOnMap(restaurant.getId(), restaurant.getPosition().latitude, restaurant.getPosition().longitude);
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(setMarkerIcon(restaurant.getId(), false)));
                        mMap.addMarker(markerOptions);
                    }
                }
            });

        }
        this.configureListener();
    }

    private void getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                @SuppressLint("MissingPermission") Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this.requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        lastKnownLocation = task.getResult();
                        currentLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                        if (lastKnownLocation != null) {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(lastKnownLocation.getLatitude(),
                                            lastKnownLocation.getLongitude()),DEFAULT_ZOOM));
                            nearbySearch();
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
        new PlaceTask().execute(url);
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

    private MarkerOptions setMarkerOnMap(String placeId, double latitude, double longitude) {
        MarkerOptions options = new MarkerOptions();
        options.icon(BitmapDescriptorFactory.fromResource(setMarkerIcon(placeId, false)));
        LatLng latLng = new LatLng(latitude, longitude);
        options.position(latLng);
        options.snippet(placeId);
        return options;
    }

    private int setMarkerIcon(String placeId, Boolean isSearched) {
        List<User> users = new ArrayList<>();
        mViewModel.getAllUsers().observe(getViewLifecycleOwner(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> userList) {
                users.clear();
                users.addAll(userList);
            }
        });
        for (User user : users) {
            if (user.getRestaurantId().equals(placeId)) {
                return R.drawable.baseline_place_cyan;
            }
//            if (isSearched) {
//                return R.drawable.baseline_place_green;
//            }
        }
        return R.drawable.baseline_place_orange;
    }

    private class PlaceTask extends AsyncTask<String, Integer, String> {

        // Get the data from the url
        @Override
        protected String doInBackground(String... strings) {
            String data = null;
            try {
                data = downloadUrl(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }
        // Execute the task to handle the data.
        @Override
        protected void onPostExecute(String s) {
            new ParserTask().execute(s);
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {

        // Return a list contains id, name, adress, latitude and longitude of the restaurants.
        @Override
        protected List<HashMap<String, String>> doInBackground(String... strings) {
            JsonParser jsonParser = new JsonParser();
            List<HashMap<String, String>> mapList = null;
            JSONObject object;
            try {
                object = new JSONObject(strings[0]);
                mapList = jsonParser.parseResult(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return mapList;
        }
        // Put a marker on each restaurant found,
        @Override
        protected void onPostExecute(List<HashMap<String, String>> hashMaps) {
            mMap.clear();

            //TODO just for save some request.
            List<String> listOfPlaceId = new ArrayList<>();
//            for (int i = 0; i < hashMaps.size(); i++) {
            for (int i = 0; i < 3; i++) {
                String placeId = hashMaps.get(i).get("placeId");
                listOfPlaceId.add(placeId);
                double lat = Double.parseDouble(Objects.requireNonNull(hashMaps.get(i).get("latitude")));
                double lng = Double.parseDouble(Objects.requireNonNull(hashMaps.get(i).get("longitude")));
                MarkerOptions markerOptions = setMarkerOnMap(placeId, lat, lng);
                markerOptions.icon(BitmapDescriptorFactory.fromResource(setMarkerIcon(placeId, false)));
                mMap.addMarker(markerOptions);
            }
            mViewModel.requestForPlaceDetails(listOfPlaceId, requireContext(), false);
        }
    }
}