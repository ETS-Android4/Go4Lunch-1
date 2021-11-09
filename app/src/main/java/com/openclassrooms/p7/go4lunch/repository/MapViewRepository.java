package com.openclassrooms.p7.go4lunch.repository;

import static android.content.ContentValues.TAG;

import android.location.Location;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

public class MapViewRepository {

    private static volatile MapViewRepository INSTANCE;

    private MapViewRepository() { }

    public static MapViewRepository getInstance() {
        MapViewRepository result = INSTANCE;
        if (result != null) {
            return result;
        }
        synchronized (MapViewRepository.class) {
            if (INSTANCE == null) {
                INSTANCE = new MapViewRepository();
            }
            return INSTANCE;
        }
    }

    //TODO use it soon
    public void ConfigureSearchPlace(Location lastKnownLocation, FragmentActivity fragmentActivity) {
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        String query = "restaurant";
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setOrigin(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()))
                .setCountry("FR")
                .setTypeFilter(TypeFilter.ADDRESS)
                .setSessionToken(token)
                .setQuery(query)
                .build();
        PlacesClient placesClient = Places.createClient(fragmentActivity);
        placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
            for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                Log.i(TAG, "FOUND THIS: " + prediction.getPlaceId());
            }
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                Log.e(TAG, "NOT FOUND" + apiException.getStatusCode());
            }
        });
    }
}
