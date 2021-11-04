package com.openclassrooms.p7.go4lunch.service;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.openclassrooms.p7.go4lunch.R;
import com.openclassrooms.p7.go4lunch.manager.CurrentUserManager;
import com.openclassrooms.p7.go4lunch.model.FavoriteRestaurant;
import com.openclassrooms.p7.go4lunch.model.Restaurant;
import com.openclassrooms.p7.go4lunch.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lleotraas on 21.
 */
public class DummyRestaurantApiService implements RestaurantApiService {

    private final List<Restaurant> mRestaurantList = DummyRestaurant.generateRestaurant();
    private final List<FavoriteRestaurant> mFavoriteRestaurantList = DummyRestaurant.generateFavoriteRestaurant();
    private final List<User> mUserList = DummyRestaurant.generateUsers();

    @Override
    public List<Restaurant> getRestaurant() {
        return mRestaurantList;
    }

    @Override
    public List<FavoriteRestaurant> getFavoriteRestaurant() {
        return mFavoriteRestaurantList;
    }

    @Override
    public List<User> getUsers() {
        return mUserList;
    }

    @Override
    public void addFavoriteRestaurant(FavoriteRestaurant favoriteRestaurant) {
        mFavoriteRestaurantList.add(favoriteRestaurant);
    }

    @Override
    public void deleteFavoriteRestaurant(FavoriteRestaurant favoriteRestaurant) {
        mFavoriteRestaurantList.remove(favoriteRestaurant);
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
    public FavoriteRestaurant searchFavoriteRestaurantById(String favoriteRestaurantId) {
        FavoriteRestaurant favoriteRestaurantFound = null;
        for (FavoriteRestaurant favoriteRestaurant : getFavoriteRestaurant()) {
            if (favoriteRestaurantId.equals(favoriteRestaurant.getUid() + favoriteRestaurant.getRestaurantId())) {
                favoriteRestaurantFound = favoriteRestaurant;
            }
        }
        return favoriteRestaurantFound;
    }

    @Override
    public FavoriteRestaurant searchFavoriteRestaurantSelected(String currentUid, String currentRestaurantId) {
        FavoriteRestaurant favoriteRestaurantFound = null;
        for (FavoriteRestaurant favoriteRestaurant : getFavoriteRestaurant()) {
            if (favoriteRestaurant.isSelected() && currentUid.equals(favoriteRestaurant.getUid()) && !currentRestaurantId.equals(favoriteRestaurant.getRestaurantId())){
                favoriteRestaurantFound = favoriteRestaurant;
            }
        }
        return favoriteRestaurantFound;
    }

    @Override
    public List<User> getUsersInterestedAtCurrentRestaurant(Restaurant currentRestaurant) {
        List<User> userList = new ArrayList<>();
        CurrentUserManager currentUserManager = CurrentUserManager.getInstance();
        for (FavoriteRestaurant favoriteRestaurants : mFavoriteRestaurantList) {
            if (favoriteRestaurants.isSelected() && !currentUserManager.getCurrentUser().getUid().equals(favoriteRestaurants.getUid()) && currentRestaurant.getId().equals(favoriteRestaurants.getRestaurantId())) {
                User user = searchUserById(favoriteRestaurants.getUid());
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
        for (FavoriteRestaurant favoriteRestaurant : getFavoriteRestaurant()) {
            if (favoriteRestaurant.getRestaurantId().equals(placeId) && favoriteRestaurant.isSelected()) {
                return R.drawable.baseline_place_cyan;
            }
        }
        return R.drawable.baseline_place_orange;
    }
}
