package com.openclassrooms.p7.go4lunch.repository;

import static android.content.ContentValues.TAG;
import static com.openclassrooms.p7.go4lunch.ui.fragment.map_view.MapViewFragment.currentLocation;

import android.content.Context;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPhotoResponse;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.openclassrooms.p7.go4lunch.injector.DI;
import com.openclassrooms.p7.go4lunch.model.Restaurant;
import com.openclassrooms.p7.go4lunch.service.ApiService;

import java.util.Arrays;
import java.util.List;

public class MapViewRepository {

    private static volatile MapViewRepository INSTANCE;
    private ApiService mApiService = DI.getRestaurantApiService();

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

    public Task<FindAutocompletePredictionsResponse> getPlaceData(FragmentActivity activity){
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        PlacesClient placesClient = Places.createClient(activity);
        RectangularBounds rectangularBounds = mApiService.getRectangularBound(currentLocation);
        String query = "restaurant";
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setLocationRestriction(rectangularBounds)
                .setOrigin(new LatLng(currentLocation.latitude, currentLocation.longitude))
                .setCountry("FR")
                .setTypeFilter(TypeFilter.ADDRESS)
                .setSessionToken(token)
                .setQuery(query)
                .build();
        return placesClient.findAutocompletePredictions(request);
    }

    public void requestForPlaceDetails(String placeId, Context context, boolean isSearched, GoogleMap map) {
        getPlaceDetails(context, placeId).addOnSuccessListener((response) -> {
            requestForPlacePhoto(response.getPlace(), context, isSearched, map);

        });
    }

    public Task<FetchPlaceResponse> getPlaceDetails(Context context, String placeId) {
        PlacesClient placesClient = Places.createClient(context);
        List<Place.Field> placeFields = Arrays.asList(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.RATING,
                Place.Field.PHONE_NUMBER,
                Place.Field.WEBSITE_URI,
                Place.Field.OPENING_HOURS,
                Place.Field.LAT_LNG,
                Place.Field.PHOTO_METADATAS
        );
        FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields)
                .build();
        return placesClient.fetchPlace(request);
    }

    public void requestForPlacePhoto(Place place, Context context, boolean isSearched, GoogleMap map) {
        getPhotoData(place, context).addOnSuccessListener((fetchPhotoResponse) -> {
            Restaurant restaurant = mApiService.createRestaurant(place, fetchPhotoResponse.getBitmap());
            mApiService.getRestaurant().add(restaurant);
            mApiService.makeUserAndRestaurantList(restaurant);
            mApiService.setMarkerOnMap(restaurant, isSearched, map);
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                Log.e(TAG, "Place not found: " + exception.getMessage());
            }
        });
    }

    public Task<FetchPhotoResponse> getPhotoData(Place place, Context context) {
        PlacesClient placesClient = null;
        FetchPhotoRequest photoRequest = null;
        if (place.getPhotoMetadatas() != null) {
            PhotoMetadata photoMetadata = place.getPhotoMetadatas().get(0);
            photoRequest = FetchPhotoRequest.builder(photoMetadata)
                    .build();
            placesClient = Places.createClient(context);
        }
        return placesClient.fetchPhoto(photoRequest);
    }


}
