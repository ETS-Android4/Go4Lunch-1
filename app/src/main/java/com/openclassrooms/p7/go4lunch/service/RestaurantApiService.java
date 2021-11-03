package com.openclassrooms.p7.go4lunch.service;

import android.view.View;

import com.openclassrooms.p7.go4lunch.model.FavoriteRestaurant;
import com.openclassrooms.p7.go4lunch.model.Restaurant;
import com.openclassrooms.p7.go4lunch.model.User;

import java.util.HashMap;
import java.util.List;

/**
 * Created by lleotraas on 21.
 */
public interface RestaurantApiService {
    List<Restaurant> getRestaurant();
    List<FavoriteRestaurant> getFavoriteRestaurant();
    List<User> getUsers();
    void addFavoriteRestaurant(FavoriteRestaurant favoriteRestaurant);
    void deleteFavoriteRestaurant(FavoriteRestaurant favoriteRestaurant);
    void addRestaurant(Restaurant restaurant);
    void removeRestaurant(Restaurant restaurant);
    void addUser(User user);
    void deleteUser(User user);
    Restaurant searchRestaurantById(String id);
    User searchUserById(String uid);
    FavoriteRestaurant searchFavoriteRestaurantById(String favoriteRestaurantId);
    FavoriteRestaurant searchFavoriteRestaurantSelected(String currentUId, String currentRestaurantId);
    int setRatingStars(int index, double rating);
    int setFavoriteImage(boolean favorite);
    int setSelectedImage(boolean Selected);
}
