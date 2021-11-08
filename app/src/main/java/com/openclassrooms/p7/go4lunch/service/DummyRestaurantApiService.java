package com.openclassrooms.p7.go4lunch.service;

import com.openclassrooms.p7.go4lunch.R;
import com.openclassrooms.p7.go4lunch.model.FavoriteOrSelectedRestaurant;
import com.openclassrooms.p7.go4lunch.model.Restaurant;
import com.openclassrooms.p7.go4lunch.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by lleotraas on 21.
 */
public class DummyRestaurantApiService implements RestaurantApiService {

    private final List<Restaurant> mRestaurantList = DummyRestaurant.generateRestaurant();
    private final List<FavoriteOrSelectedRestaurant> mFavoriteOrSelectedRestaurantList = DummyRestaurant.generateFavoriteRestaurant();
    private final List<User> mUserList = DummyRestaurant.generateUsers();

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
    public FavoriteOrSelectedRestaurant searchSelectedRestaurantToDeselect(String currentUserId, String currentRestaurantId) {
        FavoriteOrSelectedRestaurant favoriteOrSelectedRestaurantFound = null;
        for (FavoriteOrSelectedRestaurant favoriteOrSelectedRestaurant : getFavoriteRestaurant()) {
            if (
                    favoriteOrSelectedRestaurant.getUserId().equals(currentUserId) &&
                    !favoriteOrSelectedRestaurant.getRestaurantId().equals(currentRestaurantId))
            {
                favoriteOrSelectedRestaurantFound = favoriteOrSelectedRestaurant;
            }
        }
        return favoriteOrSelectedRestaurantFound;
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
        //TODO pas sûr que ça marche
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
}
