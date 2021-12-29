package com.openclassrooms.p7.go4lunch.repository;

import static android.content.ContentValues.TAG;
import static com.openclassrooms.p7.go4lunch.ui.fragment.map_view.MapViewFragment.currentLocation;

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
    private static int RESTAURANT_SIZE;
    private final MutableLiveData<List<Restaurant>> listOfRestaurant = new MutableLiveData<>();

    public MapViewRepository() {
        mGoogleMapsHelper = GoogleMapsHelper.getInstance();
    }

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
    public void requestForPlaceDetails(List<String> placeId, Context context, boolean isSearched) {
        if (placeId.size() > 1) {
            RESTAURANT_SIZE = placeId.size();
        }
        List<Restaurant> restaurantList = new ArrayList<>();
        if (placeId.size() == 1) {
            restaurantList.addAll(Objects.requireNonNull(listOfRestaurant.getValue()));
            if (restaurantList.size() > RESTAURANT_SIZE) {
                restaurantList.remove(restaurantList.size() - 1);
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
                    }).addOnFailureListener((exception) -> {
                        if (exception instanceof ApiException) {
                            Log.e(TAG, "Place not found: " + exception.getMessage());
                        }
                    });
                } else {
                    Restaurant restaurant = createRestaurant(place, null, context, isSearched);
                    restaurantList.add(restaurant);
                    listOfRestaurant.postValue(restaurantList);
                }
            });
        }

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
                getOpeningHours(place.getOpeningHours(), context),
                place.getPhoneNumber(),
                mApiService.getWebsiteUri(place.getWebsiteUri()),
                mApiService.getDistance(place.getLatLng(), currentLocation),
                mApiService.getRating(place.getRating()),
                place.getLatLng(),
                placeImage,
                0,
                isSearched);
    }

    public void setNumberOfFriendInterested(List<User> userInterestedList) {
        List<User> userList = new ArrayList<>();
        for (Restaurant restaurant : Objects.requireNonNull(listOfRestaurant.getValue())) {
            for (User user : userInterestedList) {
                if (user.getRestaurantId().equals(restaurant.getId())) {
                    userList.add(user);
                }
            }
            restaurant.setNumberOfFriendInterested(userList.size());
            userList.clear();
        }
    }

    public String getJson(String url) throws IOException {
        HttpURLConnection connection = mGoogleMapsHelper.getData(url);
        connection.connect();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }
        reader.close();
        return builder.toString();
    }

    public void handleJson(String url) throws IOException {
        JSONObject jsonObject;
        List<Map<String, String>> mapList = null;
        try {
            jsonObject = new JSONObject(getJson(url));

        } catch (JSONException e) {
            e.printStackTrace();
        }

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
                    return String.format("%s %s:%s%s", context.getResources().getString(R.string.map_view_repository_open_at), openHour, openMinute, context.getResources().getString(R.string.map_view_repository_hour));
                }
                if (openHour > currentHour) {
                    return String.format("%s %s%s", context.getResources().getString(R.string.map_view_repository_open_at), openHour, context.getResources().getString(R.string.map_view_repository_hour));
                }
                if (closeHour > currentHour || closeHour < 3) {
                    return String.format("%s %s%s", context.getResources().getString(R.string.map_view_repository_open_until), Math.abs(closeHour - currentHour), context.getResources().getString(R.string.map_view_repository_hour));
                }
            }
        }
        return context.getResources().getString(R.string.map_view_repository_still_closed);
    }
}
