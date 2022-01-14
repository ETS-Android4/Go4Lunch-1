package com.openclassrooms.p7.go4lunch.model;

public class RestaurantFavorite {

    private String restaurantId;

    public RestaurantFavorite() { }

    public RestaurantFavorite(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    // --- GETTERS ---
    public String getRestaurantId() {
        return restaurantId;
    }

}
