package com.openclassrooms.p7.go4lunch.repository;

import static android.content.ContentValues.TAG;
import static com.openclassrooms.p7.go4lunch.ui.MainActivity.CURRENT_USER_ID;
import static com.openclassrooms.p7.go4lunch.ui.fragment.map_view.MapViewFragment.currentLocation;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.LocalTime;
import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.Period;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPhotoResponse;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.openclassrooms.p7.go4lunch.R;
import com.openclassrooms.p7.go4lunch.injector.DI;
import com.openclassrooms.p7.go4lunch.model.Restaurant;
import com.openclassrooms.p7.go4lunch.model.UserAndRestaurant;
import com.openclassrooms.p7.go4lunch.service.ApiService;
import com.openclassrooms.p7.go4lunch.ui.fragment.list_view.ListViewAdapter;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class MapViewRepository {

    private static volatile MapViewRepository INSTANCE;
    private final ApiService mApiService = DI.getRestaurantApiService();
    private final MutableLiveData<List<Restaurant>> listOfRestaurant = new MutableLiveData<>();

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

    public MutableLiveData<List<Restaurant>> getAllRestaurantList() {
        return listOfRestaurant;
    }

    /**
     * Do a request to search a place in MapViewFragment.
     * @param placeId id of the place.
     * @param context context of the fragment.
     * @param isSearched usefull for the marker color.
     */
    public void requestForPlaceDetails(String placeId, Context context, boolean isSearched) {
        getPlaceData(context, placeId).addOnSuccessListener((response) -> {
            requestForPlacePhoto(response.getPlace(), context, isSearched);
        });
    }

    /**
     * Call to get a request for data
     * @param context context of the fragment.
     * @param placeId id of the place.
     * @return fetch place task.
     */
    private Task<FetchPlaceResponse> getPlaceData(Context context, String placeId) {
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

    public Restaurant getCurrentRestaurant(String restaurantId) {
        Restaurant currentRestaurant = null;
        for (Restaurant restaurant : Objects.requireNonNull(listOfRestaurant.getValue())) {
            if (restaurant.getId().equals(restaurantId)) {
                currentRestaurant = restaurant;
            }
        }
        return currentRestaurant;
    }

    /**
     * Do a request to get place photo for MapViewFragment when user search for a place.
     * @param place the place contains photos.
     * @param context context of the fragment.
     * @param isSearched usefull for the marker color.
     */
    public void requestForPlacePhoto(Place place, Context context, boolean isSearched) {
        getPhotoData(place, context).addOnSuccessListener((fetchPhotoResponse) -> {
            Restaurant restaurant = createRestaurant(place, fetchPhotoResponse.getBitmap(), context);
            if (isSearched) {
                mApiService.getSearchedRestaurant().clear();
                mApiService.getSearchedRestaurant().add(restaurant);
//                mApiService.setMarkerOnMap(restaurant, map, true);
            } else {
                mApiService.getRestaurant().add(restaurant);
                listOfRestaurant.setValue(mApiService.getRestaurant());
//                mApiService.setMarkerOnMap(restaurant, map, false);
            }
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
        if (placesClient != null) {
            return placesClient.fetchPhoto(photoRequest);
        } else {
            return null;
        }
    }

    private Restaurant createRestaurant(Place place, Bitmap placeImage, Context context) {
        //TODO if photo = null change it by noImage.
        return new Restaurant(
                place.getId(),
                place.getName(),
                place.getAddress(),
                getOpeningHours(place.getOpeningHours(), context),
                place.getPhoneNumber(),
                mApiService.getWebsiteUri(place.getWebsiteUri()),
                mApiService.getDistance(place.getLatLng(), currentLocation),
                mApiService.getRating(place.getRating()),
                place.getLatLng(),
                placeImage,
                0);
    }

    /**
     * Format opening hour for show it.
     *
     * @param openingHours place opening hours.
     * @return opening hours if available.
     */
    public String getOpeningHours(OpeningHours openingHours, Context context) {
        if (openingHours != null) {
            String currentDay = mApiService.getCurrentDay(Calendar.getInstance());
            LocalTime currentTime = LocalTime.newInstance(Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE));
            return makeStringOpeningHours(openingHours, currentDay, currentTime, context);
        }
        return "no details";
    }

    /**
     * Get the current hour and openingHour of the currentDay and calculate the remaining time before Restaurant close.
     *
     * @param openingHours Restaurant openingHours.
     * @param context
     * @return String with remaining time before restaurant close.
     */
    public String makeStringOpeningHours(OpeningHours openingHours, String currentDay, LocalTime currentTime, Context context) {
        int currentHour = currentTime.getHours();
        for (Period openingDay : openingHours.getPeriods()) {
            if (Objects.requireNonNull(openingDay.getOpen()).getDay().toString().equals(currentDay)) {
                int closeHour = Objects.requireNonNull(openingDay.getClose()).getTime().getHours();
                int openHour = Objects.requireNonNull(openingDay.getOpen()).getTime().getHours();
                int openMinute = Objects.requireNonNull(openingDay.getOpen()).getTime().getMinutes();


                if (openMinute > 0 && openHour > currentHour) {
                    return String.format("%s %s:%s", context.getResources().getString(R.string.map_view_repository_open_at), openHour, openMinute);
                }
                if (openHour > currentHour) {
                    return String.format("%s %s", context.getResources().getString(R.string.map_view_repository_open_at), openHour);
                }
                if (closeHour > currentHour || closeHour < 3) {
                    return String.format("%s %s", context.getResources().getString(R.string.map_view_repository_open_until), Math.abs(closeHour - currentHour));
                }
            }
        }
        return context.getResources().getString(R.string.map_view_repository_still_closed);
    }

    /**
     * Do a request to search a place in ListViewFragment.
     * @param placeId id of the place.
     * @param context context of the fragment.
     * @param mRecyclerView recyclerView of the fragment.
     * @param listViewAdapter adapter of the fragment.
     */
//    public void requestForPlaceDetails(String placeId, Context context, RecyclerView mRecyclerView, ListViewAdapter listViewAdapter) {
//        getPlaceData(context, placeId).addOnSuccessListener((response) -> {
//            requestForPlacePhoto(response.getPlace(), context, mRecyclerView, listViewAdapter);
//        });
//    }

    /**
     * Do a request to get place photo for ListViewFragment.
     //     * @param place the place contains photos.
     //     * @param context context of the fragment.
     //     * @param mRecyclerView recyclerView of the fragment.
     //     * @param listViewAdapter adapter of the fragment.
     */
//    public void requestForPlacePhoto(Place place, Context context, RecyclerView mRecyclerView, ListViewAdapter listViewAdapter) {
//        // Place have a photo.
//        if (getPhotoData(place, context) != null) {
//            getPhotoData(place, context).addOnSuccessListener((fetchPhotoResponse) -> {
//                createRestaurantAndShowIt(place, fetchPhotoResponse, context, mRecyclerView, listViewAdapter);
//            }).addOnFailureListener((exception) -> {
//                if (exception instanceof ApiException) {
//                    Log.e(TAG, "Place not found: " + exception.getMessage());
//                }
//            });
//        // Place don't have a photo.
//        } else {
//            createRestaurantAndShowIt(place, null, context, mRecyclerView, listViewAdapter);
//        }
//    }

    //TODO comments
//    private void createRestaurantAndShowIt(Place place, FetchPhotoResponse fetchPhotoResponse, Context context, RecyclerView mRecyclerView, ListViewAdapter listViewAdapter) {
//        Restaurant restaurant = createRestaurant(place, fetchPhotoResponse.getBitmap(), context);
//        mApiService.getSearchedRestaurant().clear();
//        restaurant.setNumberOfFriendInterested(mApiService.getUsersInterestedAtCurrentRestaurants(CURRENT_USER_ID, restaurant).size());
//        mApiService.getSearchedRestaurant().add(restaurant);
//    }
}
