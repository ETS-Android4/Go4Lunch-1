package com.openclassrooms.p7.go4lunch.model;

import androidx.annotation.Nullable;

public class User {

    private String uid;
    private String userName;
    private String photoUrl;
    private String restaurantId;
    private boolean restaurantIsSelected;

    // --- CONSTRUCTOR ---
    public User() { }

    public User(String uid, String userName, String photoUrl, String restaurantId, boolean restaurantIsSelected) {
        this.uid = uid;
        this.userName = userName;
        this.photoUrl = photoUrl;
        this.restaurantId = restaurantId;
        this.restaurantIsSelected = restaurantIsSelected;
    }

    // --- GETTERS ---
    public String getUid() {
        return uid;
    }
    public String getUserName() {
        return userName;
    }
    public String getPhotoUrl() {
        return photoUrl;
    }
    public String getRestaurantId() {
        return restaurantId;
    }
    public boolean isRestaurantIsSelected() {
        return restaurantIsSelected;
    }

    // --- SETTERS ---
    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }
    public void setRestaurantIsSelected(boolean restaurantIsSelected) {
        this.restaurantIsSelected = restaurantIsSelected;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return super.equals(obj);
    }
}
