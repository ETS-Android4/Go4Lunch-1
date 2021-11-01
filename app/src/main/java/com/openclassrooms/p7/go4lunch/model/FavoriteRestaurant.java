package com.openclassrooms.p7.go4lunch.model;

public class FavoriteRestaurant {

    private final String uid;
    private final String restaurantId;
    private final String restaurantName;
    private boolean isFavorite;
    private boolean isSelected;

    public FavoriteRestaurant(String uid, String restaurantId, String restaurantName, boolean isFavorite, boolean isSelected) {
        this.uid = uid;
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.isFavorite = isFavorite;
        this.isSelected = isSelected;
    }

    // --- GETTERS ---
    public String getUid() {
        return uid;
    }
    public String getRestaurantId() {
        return restaurantId;
    }
    public String getRestaurantName() {
        return restaurantName;
    }
    public boolean isFavorite() {
        return isFavorite;
    }
    public boolean isSelected() {
        return isSelected;
    }

    // --- SETTERS ---
    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
