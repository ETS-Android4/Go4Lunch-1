package com.openclassrooms.p7.go4lunch.model;

import java.util.Map;

public class RestaurantDataMap {

    private Map<String, UserAndRestaurant> restaurantDataMap;

    public RestaurantDataMap() {
    }

    public RestaurantDataMap(Map<String, UserAndRestaurant> restaurantDataMap) {
        this.restaurantDataMap = restaurantDataMap;
    }

    public Map<String, UserAndRestaurant> getRestaurantDataMap() {
        return restaurantDataMap;
    }
}
