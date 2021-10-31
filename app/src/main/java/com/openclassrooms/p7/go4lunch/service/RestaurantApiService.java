package com.openclassrooms.p7.go4lunch.service;

import com.openclassrooms.p7.go4lunch.model.Restaurant;

import java.util.HashMap;
import java.util.List;

/**
 * Created by lleotraas on 21.
 */
public interface RestaurantApiService {
    List<Restaurant> getRestaurant();
    HashMap<String, Boolean> getFavoriteRestaurant();
    void addFavoriteRestaurant(String restaurantId, Boolean isFavorite);
    void deleteFavoriteRestaurant(String restaurantId);
    void addRestaurant(Restaurant restaurant);
    void removeRestaurant(Restaurant restaurant);
    int setRatingStars(int index, double rating);
}
