package com.openclassrooms.p7.go4lunch.model;

import androidx.annotation.Nullable;

import java.util.Comparator;

public class User {

    private String uid;
    private String userName;
    private String photoUrl;
    private String restaurantName;
    private String restaurantId;
    private boolean restaurantSelected;

    // --- CONSTRUCTOR ---
    public User() { }
    public User(String uid, String userName, String photoUrl, String restaurantName, String restaurantId, boolean restaurantSelected) {
        this.uid = uid;
        this.userName = userName;
        this.photoUrl = photoUrl;
        this.restaurantName = restaurantName;
        this.restaurantId = restaurantId;
        this.restaurantSelected = restaurantSelected;
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
    public String getRestaurantName() {
        return restaurantName;
    }
    public String getRestaurantId() {
        return restaurantId;
    }
    public boolean isRestaurantSelected() {
        return restaurantSelected;
    }

    // --- SETTERS ---
    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }
    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }
    public void setRestaurantSelected(boolean restaurantSelected) {
        this.restaurantSelected = restaurantSelected;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return super.equals(obj);
    }

    public static class UserComparator implements Comparator<User> {
        @Override
        public int compare(User userLeft, User userRight) {
            return userRight.restaurantName.compareTo(userLeft.restaurantName);
        }
    }
}
