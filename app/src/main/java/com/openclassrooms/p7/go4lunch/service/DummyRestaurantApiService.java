package com.openclassrooms.p7.go4lunch.service;

import static android.content.ContentValues.TAG;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.openclassrooms.p7.go4lunch.R;
import com.openclassrooms.p7.go4lunch.model.FavoriteOrSelectedRestaurant;
import com.openclassrooms.p7.go4lunch.model.Restaurant;
import com.openclassrooms.p7.go4lunch.model.User;
import com.openclassrooms.p7.go4lunch.ui.DetailActivity;
import com.openclassrooms.p7.go4lunch.ui.fragment.map_view.MapViewFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * Created by lleotraas on 21.
 */
public class DummyRestaurantApiService implements RestaurantApiService {

    private final List<Restaurant> mRestaurantList = DummyRestaurant.generateRestaurant();
    private final List<FavoriteOrSelectedRestaurant> mFavoriteOrSelectedRestaurantList = DummyRestaurant.generateFavoriteRestaurant();
    private final List<User> mUserList = DummyRestaurant.generateUsers();
    private LatLng mCurrentLocation = MapViewFragment.currentLocation;

    @Override
    public List<Restaurant> getRestaurant() {
        return mRestaurantList;
    }

    @Override
    public List<FavoriteOrSelectedRestaurant> getFavoriteRestaurant() {
        return mFavoriteOrSelectedRestaurantList;
    }

    @Override
    public List<User> getUsers() {
        return mUserList;
    }

    @Override
    public void addFavoriteRestaurant(FavoriteOrSelectedRestaurant favoriteOrSelectedRestaurant) {
        mFavoriteOrSelectedRestaurantList.add(favoriteOrSelectedRestaurant);
    }

    @Override
    public void deleteFavoriteRestaurant(FavoriteOrSelectedRestaurant favoriteOrSelectedRestaurant) {
        mFavoriteOrSelectedRestaurantList.remove(favoriteOrSelectedRestaurant);
    }

    @Override
    public void addRestaurant(Restaurant restaurant) {
        mRestaurantList.add(restaurant);
    }

    @Override
    public void removeRestaurant(Restaurant restaurant) {
        mRestaurantList.remove(restaurant);
    }

    @Override
    public void addUser(User user) {
        mUserList.add(user);
    }

    @Override
    public void deleteUser(User user) {
        mUserList.remove(user);
    }

    @Override
    public Map<String, FavoriteOrSelectedRestaurant> makeLikedOrSelectedRestaurantMap(String currentUserId) {
        Map<String, FavoriteOrSelectedRestaurant> likedOrSelectedRestaurant = new HashMap<>();
        for (FavoriteOrSelectedRestaurant favoriteOrSelectedRestaurant : getFavoriteRestaurant()) {
            if (favoriteOrSelectedRestaurant.getUserId().equals(currentUserId)) {
                likedOrSelectedRestaurant.put(favoriteOrSelectedRestaurant.getRestaurantId(), favoriteOrSelectedRestaurant);
            }
        }
        return likedOrSelectedRestaurant;
    }

    @Override
    public void makeLikedOrSelectedRestaurantList(Restaurant restaurant) {
        FavoriteOrSelectedRestaurant favoriteOrSelectedRestaurant = null;
        for (int index = 0;index < getUsers().size();index++) {
            favoriteOrSelectedRestaurant = getUsers().get(index).getLikedOrSelectedRestaurant().get(restaurant.getId());
            if (favoriteOrSelectedRestaurant != null) {
                getFavoriteRestaurant().add(favoriteOrSelectedRestaurant);
            }
        }
    }

    @Override
    public String makeStringOpeningHours(OpeningHours openingHours) {
        Calendar calendar = Calendar.getInstance(Locale.FRANCE);
        int currentHour = calendar.get(Calendar.HOUR);
        int hours = Objects.requireNonNull(openingHours.getPeriods().get(0).getOpen()).getTime().getHours();
        if (currentHour > hours) {
            return "still closed";
        }else {
            return "open until " + (hours - currentHour) + "pm";
        }
    }

    @Override
    public Restaurant searchRestaurantById(String id) {
        Restaurant restaurantFound = null;
        for (Restaurant restaurant : getRestaurant()) {
            if (id.equals(restaurant.getId())){
                restaurantFound = restaurant;
            }
        }
        return restaurantFound;
    }

    @Override
    public User searchUserById(String uid) {
        User userFound = null;
        for (User user : getUsers()) {
            if (uid.equals(user.getUid())) {
                userFound = user;
            }
        }
        return userFound;
    }

    @Override
    public FavoriteOrSelectedRestaurant searchFavoriteRestaurantById(String currentUserId, String currentRestaurantId) {
        FavoriteOrSelectedRestaurant favoriteOrSelectedRestaurantFound = null;
        for (FavoriteOrSelectedRestaurant favoriteOrSelectedRestaurant : getFavoriteRestaurant()) {
            if (
                    currentRestaurantId.equals(favoriteOrSelectedRestaurant.getRestaurantId()) &&
                    currentUserId.equals(favoriteOrSelectedRestaurant.getUserId())
            ) {
                favoriteOrSelectedRestaurantFound = favoriteOrSelectedRestaurant;
            }
        }
        return favoriteOrSelectedRestaurantFound;
    }

    @Override
    public void searchSelectedRestaurantToDeselect(String currentUserId, String currentRestaurantId) {
        for (int index = 0;index < getFavoriteRestaurant().size();index++) {
            if (
                    getFavoriteRestaurant().get(index).getUserId().equals(currentUserId) &&
                    !getFavoriteRestaurant().get(index).getRestaurantId().equals(currentRestaurantId) &&
                    getFavoriteRestaurant().get(index).isSelected())
            {
                getFavoriteRestaurant().get(index).setSelected(false);
            }
        }
    }

    @Override
    public void selectRestaurant(String currentUserId, String currentRestaurantId, int buttonId) {
        for (int index = 0;index < getFavoriteRestaurant().size();index++) {
            if (
                    currentRestaurantId.equals(getFavoriteRestaurant().get(index).getRestaurantId()) &&
                            currentUserId.equals(getFavoriteRestaurant().get(index).getUserId())
            ) {
                if (buttonId == DetailActivity.LIKE_BTN_TAG) {
                    getFavoriteRestaurant().get(index).setFavorite(!getFavoriteRestaurant().get(index).isFavorite());
                } else {
                    getFavoriteRestaurant().get(index).setSelected(!getFavoriteRestaurant().get(index).isSelected());
                }
            }
        }
    }

    @Override
    public FavoriteOrSelectedRestaurant searchSelectedRestaurant(User user) {
        FavoriteOrSelectedRestaurant favoriteOrSelectedRestaurantFound = null;
        for (FavoriteOrSelectedRestaurant favoriteOrSelectedRestaurant : getFavoriteRestaurant()) {
            if (favoriteOrSelectedRestaurant.isSelected() && user.getUid().equals(favoriteOrSelectedRestaurant.getUserId())) {
                favoriteOrSelectedRestaurantFound = favoriteOrSelectedRestaurant;
            }
        }
        return favoriteOrSelectedRestaurantFound;
    }

    @Override
    public List<User> getUsersInterestedAtCurrentRestaurant(String currentUserId, Restaurant currentRestaurant) {
        List<User> userList = new ArrayList<>();
        for (FavoriteOrSelectedRestaurant favoriteOrSelectedRestaurants : getFavoriteRestaurant()) {
            if (
                    favoriteOrSelectedRestaurants.isSelected() &&
                    !currentUserId.equals(favoriteOrSelectedRestaurants.getUserId()) &&
                    currentRestaurant.getId().equals(favoriteOrSelectedRestaurants.getRestaurantId())
            ) {
                User user = searchUserById(favoriteOrSelectedRestaurants.getUserId());
                userList.add(user);
            }
        }
        return userList;
    }

    @Override
    public int setRatingStars(int index, double rating) {
        int convertedRating = (int) rating;
            if (convertedRating == 2 && index == 1|| convertedRating == 4 && index == 2) {
              return R.drawable.baseline_star_half_black_24;
            }
            if (convertedRating < 4 && index == 2 || convertedRating < 2 && index == 1) {
               return R.drawable.baseline_star_border_black_24;
            }
        return R.drawable.baseline_star_rate_black_24;
    }

    @Override
    public int setFavoriteImage(boolean favoriteOrSelected) {
            if (favoriteOrSelected) {
                return R.drawable.baseline_star_rate_black_36;
            }
            return R.drawable.baseline_star_border_black_36;
    }

    @Override
    public int setSelectedImage(boolean selected) {
        if (selected) {
            return R.drawable.baseline_check_circle_black_24;
        }
        return R.drawable.baseline_check_circle_outline_24;
    }

    @Override
    public int setMarker(String placeId) {
        for (FavoriteOrSelectedRestaurant favoriteOrSelectedRestaurant : getFavoriteRestaurant()) {
            if (favoriteOrSelectedRestaurant.getRestaurantId().equals(placeId) && favoriteOrSelectedRestaurant.isSelected()) {
                return R.drawable.baseline_place_cyan;
            }
        }
        return R.drawable.baseline_place_orange;
    }

    @Override
    public RectangularBounds getRectangularBound(LatLng currentLocation) {
        RectangularBounds rectangularBounds = RectangularBounds.newInstance(
                new LatLng(currentLocation.latitude - 0.001000, currentLocation.longitude + 0.025000),
                new LatLng(currentLocation.latitude + 0.025000, currentLocation.longitude + 0.001000));
                return rectangularBounds;
    }

    @Override
    public Restaurant getPlaceDetails(Place place) {
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
        float[] distance = new float[10];
        Location.distanceBetween(
                mCurrentLocation.latitude,
                mCurrentLocation.longitude,
                Objects.requireNonNull(place.getLatLng()).latitude,
                place.getLatLng().longitude,
                distance
        );
        Restaurant restaurant = new Restaurant(
                place.getId(),
                place.getName(),
                place.getAddress(),
                openingHours,
                place.getPhoneNumber(),
                uriWebsite,
                distance[0],
                place.getLatLng().latitude,
                place.getLatLng().longitude,
                0,
                rating,
                null);
        return restaurant;
    }

    public MarkerOptions setInfoOnMarker(Restaurant restaurantFound) {
        MarkerOptions options = new MarkerOptions();
        options.icon(BitmapDescriptorFactory.fromResource(setMarker(restaurantFound.getId())));
        LatLng latLng = new LatLng(restaurantFound.getLatitude(), restaurantFound.getLongitude());
        options.position(latLng);
        options.snippet(restaurantFound.getId());
        return options;
    }
}
