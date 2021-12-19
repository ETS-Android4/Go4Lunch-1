package com.openclassrooms.p7.go4lunch.model;

public class RestaurantFavorite {

    private String restaurantId;
    private boolean isFavorite;

    public RestaurantFavorite() { }

    public RestaurantFavorite(String restaurantId, boolean isFavorite) {
        this.restaurantId = restaurantId;
        this.isFavorite = isFavorite;
    }

    // --- GETTERS ---
    public String getRestaurantId() {
        return restaurantId;
    }
    public boolean isFavorite() {
        return isFavorite;
    }

    // --- SETTERS ---
    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

}
