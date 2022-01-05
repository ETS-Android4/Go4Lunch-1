package com.openclassrooms.p7.go4lunch.repository;

import static android.content.ContentValues.TAG;
import static com.openclassrooms.p7.go4lunch.ui.fragment.map_view.MapViewFragment.CURRENT_LOCATION;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.model.LocalTime;
import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.Period;
import com.google.android.libraries.places.api.model.Place;
import com.openclassrooms.p7.go4lunch.R;
import com.openclassrooms.p7.go4lunch.injector.DI;
import com.openclassrooms.p7.go4lunch.model.Restaurant;
import com.openclassrooms.p7.go4lunch.model.User;
import com.openclassrooms.p7.go4lunch.service.ApiService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MapViewRepository {

    private static volatile MapViewRepository INSTANCE;
    private final GoogleMapsHelper mGoogleMapsHelper;
    private final ApiService mApiService = DI.getRestaurantApiService();
    private final MutableLiveData<List<Restaurant>> listOfRestaurant = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isAlreadyNearbySearched = new MutableLiveData<>(true);

    public MapViewRepository(GoogleMapsHelper googleMapsHelper) {
        mGoogleMapsHelper = googleMapsHelper;
    }

    public static MapViewRepository getInstance() {
        MapViewRepository result = INSTANCE;
        if (result != null) {
            return result;
        }
        synchronized (MapViewRepository.class) {
            if (INSTANCE == null) {
                GoogleMapsHelper googleMapsHelper = GoogleMapsHelper.getInstance();
                INSTANCE = new MapViewRepository(googleMapsHelper);
            }
            return INSTANCE;
        }
    }
    public MutableLiveData<Boolean> getIsAlreadyNearbySearched() {
        return isAlreadyNearbySearched;
    }
    public MutableLiveData<List<Restaurant>> getAllRestaurants() {
        return listOfRestaurant;
    }

    /**
     * Do a request to search a place in MapViewFragment.
     * @param placeId id of the place.
     * @param context context of the fragment.
     * @param isSearched usefull for the marker color.
     */
    public void requestForPlaceDetails(List<String> placeId, Context context, boolean isSearched) {
        List<Restaurant> restaurantList = new ArrayList<>();
        if (placeId.size() == 1) {
            restaurantList.addAll(Objects.requireNonNull(listOfRestaurant.getValue()));
            Restaurant restaurantToRemove = null;
            for (Restaurant restaurant : restaurantList) {
                if (restaurant.isSearched()) {
                    restaurantToRemove = restaurant;
                }
            }
            if (restaurantToRemove != null) {
                restaurantList.remove(restaurantToRemove);
            }
        }
        for (String id : placeId) {
            mGoogleMapsHelper.getPlaceData(context, id).addOnSuccessListener((response) -> {
                Place place = response.getPlace();
                if (mGoogleMapsHelper.getPhotoData(place, context) != null) {
                    mGoogleMapsHelper.getPhotoData(place, context).addOnSuccessListener((fetchPhotoResponse) -> {
                        Restaurant restaurant = createRestaurant(place, fetchPhotoResponse.getBitmap(), context, isSearched);
                        restaurantList.add(restaurant);
                        listOfRestaurant.postValue(restaurantList);
                        Log.e(TAG, "requestForPlaceDetails: PLACE FOUND WITH PHOTO " + place.getName());
                    }).addOnFailureListener((exception) -> {
                        if (exception instanceof ApiException) {
                            Log.e(TAG, "requestForPlaceDetails: PLACE NOT FOUND");
                        }
                    });
                } else {
                    Restaurant restaurant = createRestaurant(place, null, context, isSearched);
                    restaurantList.add(restaurant);
                    listOfRestaurant.postValue(restaurantList);
                    Log.e(TAG, "requestForPlaceDetails: PLACE FOUND WITHOUT PHOTO");
                }
            });
        }
        isAlreadyNearbySearched.setValue(false);
    }

    public Restaurant getCurrentRestaurant(String restaurantId, List<Restaurant> restaurantList) {
        Restaurant currentRestaurant = null;
        for (Restaurant restaurant : Objects.requireNonNull(restaurantList)) {
            if (restaurant.getId().equals(restaurantId)) {
                currentRestaurant = restaurant;
            }
        }
        return currentRestaurant;
    }

    /**
     * Call to get a request for photo.
     * @param place id of the place.
     * @param context context of the fragment.
     * @param isSearched
     * @return fetch place task.
     */
    private Restaurant createRestaurant(Place place, Bitmap placeImage, Context context, boolean isSearched) {
        return new Restaurant(
                place.getId(),
                place.getName(),
                place.getAddress(),
                mApiService.getOpeningHours(place.getOpeningHours()),
                place.getPhoneNumber(),
                mApiService.getWebsiteUri(place.getWebsiteUri()),
                mApiService.getDistance(place.getLatLng(), CURRENT_LOCATION),
                mApiService.getRating(place.getRating()),
                place.getLatLng(),
                placeImage,
                0,
                isSearched);
    }

    public void setNumberOfFriendInterested(List<User> userInterestedList, List<Restaurant> restaurants) {
        List<User> userList = new ArrayList<>();
        for (Restaurant restaurant : restaurants) {
            for (User user : userInterestedList) {
                if (user.getRestaurantId().equals(restaurant.getId())) {
                    userList.add(user);
                }
            }
            restaurant.setNumberOfFriendInterested(userList.size());
            userList.clear();
        }
    }
}
