package com.openclassrooms.p7.go4lunch.service;

import com.openclassrooms.p7.go4lunch.model.Restaurant;

import java.util.List;

/**
 * Created by lleotraas on 21.
 */
public class DummyRestaurantApiService implements RestaurantApiService {

    private  List<Restaurant> mRestaurantList = DummyRestaurant.generateRestaurant();

    @Override
    public List<Restaurant> getRestaurant() {
        return mRestaurantList;
    }

    @Override
    public void addRestaurant(Restaurant restaurant) {
        mRestaurantList.add(restaurant);
    }

    @Override
    public void removeRestaurant(Restaurant restaurant) {
        mRestaurantList.remove(restaurant);
    }
}
