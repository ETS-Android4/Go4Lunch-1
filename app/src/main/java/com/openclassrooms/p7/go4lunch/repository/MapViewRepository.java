package com.openclassrooms.p7.go4lunch.repository;

import static android.content.ContentValues.TAG;
import static com.openclassrooms.p7.go4lunch.ui.fragment.map_view.MapViewFragment.CURRENT_LOCATION;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.model.Place;
import com.openclassrooms.p7.go4lunch.injector.DI;
import com.openclassrooms.p7.go4lunch.model.Restaurant;
import com.openclassrooms.p7.go4lunch.model.RestaurantFavorite;
import com.openclassrooms.p7.go4lunch.model.User;
import com.openclassrooms.p7.go4lunch.service.ApiService;

import java.util.ArrayList;
import java.util.List;
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
     * Do a request to search a place with photo if there is in MapViewFragment.
     * @param placeId id of the place.
     * @param context context of the fragment.
     * @param isSearched true if it is a searched place.
     */
    public void requestForPlaceDetails(List<String> placeId, Context context, boolean isSearched) {
        List<Restaurant> restaurantList = new ArrayList<>();
        if (isSearched) {
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
                        Restaurant restaurant = createRestaurant(place, fetchPhotoResponse.getBitmap(), isSearched);
                        restaurantList.add(restaurant);
                        listOfRestaurant.postValue(restaurantList);
                        Log.d(TAG, "requestForPlaceDetails: place found with photo: " + place.getName());
                    }).addOnFailureListener((exception) -> {
                        if (exception instanceof ApiException) {
                            Log.d(TAG, "requestForPlaceDetails: place not found");
                        }
                    });
                } else {
                    Restaurant restaurant = createRestaurant(place, null, isSearched);
                    restaurantList.add(restaurant);
                    listOfRestaurant.postValue(restaurantList);
                    Log.d(TAG, "requestForPlaceDetails: place found without photo");
                }
            });
        }
        isAlreadyNearbySearched.setValue(false);
    }

    /**
     * Used to get the current restaurant.
     * @param restaurantId Current restaurant id.
     * @param restaurantList List of restaurant.
     * @return Restaurant corresponding to restaurant id.
     */
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
     * Call to create a restaurant.
     * @param place Place to retrieve information.
     * @param isSearched true if it is a searched place.
     * @return fetch place task.
     */
    private Restaurant createRestaurant(Place place, Bitmap placeImage, boolean isSearched) {
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
                false,
                isSearched);
    }

    /**
     * Used to set the number of Users interested to the same restaurant.
     * @param userInterestedList List of all interested users.
     * @param restaurants List of all restaurants.
     */
    public void setNumberOfUserInterested(List<User> userInterestedList, List<Restaurant> restaurants) {
        List<User> userList = new ArrayList<>();
        for (Restaurant restaurant : restaurants) {
            for (User user : userInterestedList) {
                if (user.getRestaurantId().equals(restaurant.getId())) {
                    userList.add(user);
                }
            }
            restaurant.setNumberOfUserInterested(userList.size());
            userList.clear();
        }
    }

    /**
     * Used to set restaurants to favorite when they are liked or not favorite.
     * @param restaurantFavoriteList List of restaurant liked.
     * @param restaurants List of restaurant to modify.
     */
    public void setRestaurantFavorite(List<RestaurantFavorite> restaurantFavoriteList, List<Restaurant> restaurants) {
        for (Restaurant restaurant : restaurants) {
            if (restaurant.isFavorite()) {
                restaurant.setFavorite(false);
            }
            for (RestaurantFavorite restaurantFavorite : restaurantFavoriteList) {
                if (restaurantFavorite.getRestaurantId().equals(restaurant.getId())) {
                    restaurant.setFavorite(true);
                }
            }
        }
    }
}
