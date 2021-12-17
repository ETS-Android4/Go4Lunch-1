package com.openclassrooms.p7.go4lunch.model;

public class RestaurantData {

    private String restaurantId;
    private String restaurantName;
    private boolean isFavorite;
    private Boolean isSelected;

    public RestaurantData() { }

    public RestaurantData(String restaurantId, String restaurantName, boolean isFavorite, Boolean isSelected) {
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.isFavorite = isFavorite;
        this.isSelected = isSelected;
    }

    // --- GETTERS ---
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
