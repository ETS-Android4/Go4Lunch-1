package com.openclassrooms.p7.go4lunch.service;

import com.openclassrooms.p7.go4lunch.model.Restaurant;

import java.util.List;

/**
 * Created by lleotraas on 21.
 */
public interface RestaurantApiService {
    List<Restaurant> getRestaurant();
    void addRestaurant(Restaurant restaurant);
    void removeRestaurant(Restaurant restaurant);
    int setRatingStars(int index, double rating);
}
