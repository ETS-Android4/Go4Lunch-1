package com.openclassrooms.p7.go4lunch.model;

import java.util.Comparator;

public class UserAndRestaurant {

    private String userId;
    private String restaurantId;
    private String restaurantName;
    private boolean isFavorite;
    private Boolean isSelected;

    public UserAndRestaurant() { }

    public UserAndRestaurant(String userId, String restaurantId, String restaurantName, boolean isFavorite, Boolean isSelected) {
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.isFavorite = isFavorite;
        this.isSelected = isSelected;
    }

    // --- GETTERS ---

    public String getUserId() { return userId; }
    public String getRestaurantId() {
        return restaurantId;
    }
    public String getRestaurantName() {
        return restaurantName;
    }
    public boolean isFavorite() {
        return isFavorite;
    }
    public Boolean isSelected() {
        return isSelected;
    }

    // --- SETTERS ---
    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
    public void setSelected(Boolean selected) {
        isSelected = selected;
    }
}
