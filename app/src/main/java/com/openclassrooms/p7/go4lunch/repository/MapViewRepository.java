package com.openclassrooms.p7.go4lunch.repository;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
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
import com.openclassrooms.p7.go4lunch.ui.fragment.list_view.ListViewAdapter;

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

    /**
     * Do a request for nearby search when the app is launched.
     * @param placeId id of the place.
     * @param context activity context.
     * @param map map in MapViewFragment
     */
    public void requestForPlaceDetails(String placeId, Context context, GoogleMap map) {
        getPlaceData(context, placeId).addOnSuccessListener((response) -> {
            requestForPlacePhoto(response.getPlace(), context, map);
        });
    }

    /**
     * Do a request to search a place in ListViewFragment.
     * @param placeId id of the place.
     * @param context context of the fragment.
     * @param mRecyclerView recyclerView of the fragment.
     * @param listViewAdapter adapter of the fragment.
     */
    public void requestForPlaceDetails(String placeId, Context context, RecyclerView mRecyclerView, ListViewAdapter listViewAdapter) {
        getPlaceData(context, placeId).addOnSuccessListener((response) -> {
            requestForPlacePhoto(response.getPlace(), context, mRecyclerView, listViewAdapter);
        });
    }

    /**
     * Do a request to search a place in MapViewFragment.
     * @param placeId id of the place.
     * @param context context of the fragment.
     * @param map map of the fragment.
     * @param isSearched usefull for the marker color.
     */
    public void requestForPlaceDetails(String placeId, Context context,GoogleMap map, boolean isSearched) {
        getPlaceData(context, placeId).addOnSuccessListener((response) -> {
            requestForPlacePhoto(response.getPlace(), context, map, isSearched);
        });
    }

    /**
     * Call to get a request for data
     * @param context context of the fragment.
     * @param placeId id of the place.
     * @return fetch place task.
     */
    public Task<FetchPlaceResponse> getPlaceData(Context context, String placeId) {
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

    /**
     * Do a request to get place photo when the app is launched.
     * @param place the place contains photos.
     * @param context context of the fragment.
     * @param map map of the fragment.
     */
    public void requestForPlacePhoto(Place place, Context context, GoogleMap map) {
        getPhotoData(place, context).addOnSuccessListener((fetchPhotoResponse) -> {
            Restaurant restaurant = mApiService.createRestaurant(place, fetchPhotoResponse.getBitmap());
            mApiService.getRestaurant().add(restaurant);
            mApiService.makeUserAndRestaurantList(restaurant);
            mApiService.setMarkerOnMap(restaurant, map, false);
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                Log.e(TAG, "Place not found: " + exception.getMessage());
            }
        });
    }

    /**
     * Do a request to get place photo for ListViewFragment.
     * @param place the place contains photos.
     * @param context context of the fragment.
     * @param mRecyclerView recyclerView of the fragment.
     * @param listViewAdapter adapter of the fragment.
     */
    public void requestForPlacePhoto(Place place, Context context, RecyclerView mRecyclerView, ListViewAdapter listViewAdapter) {
        getPhotoData(place, context).addOnSuccessListener((fetchPhotoResponse) -> {
            Restaurant restaurant = mApiService.createRestaurant(place, fetchPhotoResponse.getBitmap());
            mApiService.getSearchedRestaurant().clear();
            mApiService.getSearchedRestaurant().add(restaurant);
            listViewAdapter.notifyDataSetChanged();
            mRecyclerView.setAdapter(listViewAdapter);
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                Log.e(TAG, "Place not found: " + exception.getMessage());
            }
        });
    }

    /**
     * Do a request to get place photo for MapViewFragment when user search for a place.
     * @param place the place contains photos.
     * @param context context of the fragment.
     * @param map map of the fragment.
     * @param isSearched usefull for the marker color.
     */
    public void requestForPlacePhoto(Place place, Context context, GoogleMap map, boolean isSearched) {
        getPhotoData(place, context).addOnSuccessListener((fetchPhotoResponse) -> {
            Restaurant restaurant = mApiService.createRestaurant(place, fetchPhotoResponse.getBitmap());
            mApiService.getSearchedRestaurant().clear();
            mApiService.getSearchedRestaurant().add(restaurant);
            mApiService.setMarkerOnMap(restaurant, map, isSearched);
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                Log.e(TAG, "Place not found: " + exception.getMessage());
            }
        });
    }

    /**
     * Call to get a request for photo.
     * @param place id of the place.
     * @param context context of the fragment.
     * @return fetch place task.
     */
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



    //TODO useless for the moment
    public Task<FindAutocompletePredictionsResponse> getPlacePredictionData(Context context, String placeName) {
        PlacesClient placesClient = Places.createClient(context);
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setCountry("FR")
                .setTypeFilter(TypeFilter.ADDRESS)
                .setSessionToken(token)
                .setQuery(placeName)
                .build();
        return  placesClient.findAutocompletePredictions(request);
    }
    public void requestAutocompletePrediction(Context context, String placeName) {
        getPlacePredictionData(context, placeName).addOnSuccessListener((response) -> {
           for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
               getPlaceData(context, prediction.getPlaceId());
           }
        }).addOnFailureListener((exception) -> {
            Log.i(TAG, "AUTO COMPLETE PREDICTION REQUEST FAILURE");
        });
    }
}
