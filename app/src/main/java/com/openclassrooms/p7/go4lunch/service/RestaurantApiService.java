package com.openclassrooms.p7.go4lunch.service;

import android.view.View;

import com.openclassrooms.p7.go4lunch.model.FavoriteRestaurant;
import com.openclassrooms.p7.go4lunch.model.Restaurant;

import java.util.HashMap;
import java.util.List;

/**
 * Created by lleotraas on 21.
 */
public interface RestaurantApiService {
    List<Restaurant> getRestaurant();
    List<FavoriteRestaurant> getFavoriteRestaurant();
    void addFavoriteRestaurant(FavoriteRestaurant favoriteRestaurant);
    void deleteFavoriteRestaurant(FavoriteRestaurant favoriteRestaurant);
    void addRestaurant(Restaurant restaurant);
    void removeRestaurant(Restaurant restaurant);
    int setRatingStars(int index, double rating);
    int setFavoriteImage(boolean favorite);
    int setSelectedImage(boolean Selected);
}
