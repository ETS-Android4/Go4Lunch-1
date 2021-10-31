package com.openclassrooms.p7.go4lunch.model;

import java.util.HashMap;

public class User {

    private String uid;
    private String userName;
    private HashMap<String, Boolean> favoriteRestaurantId;
    private String photoUrl;


    public User(String uid, String userName, HashMap<String, Boolean> favoriteRestaurantId, String photoUrl) {
        this.uid = uid;
        this.userName = userName;
        this.favoriteRestaurantId = favoriteRestaurantId;
        this.photoUrl = photoUrl;
    }

    // --- GETTERS ---
    public String getUid() {
        return uid;
    }
    public String getUserName() {
        return userName;
    }
    public HashMap<String, Boolean> getFavoriteRestaurantId() {
        return favoriteRestaurantId;
    }
    public String getPhotoUrl() {
        return photoUrl;
    }

    // --- SETTERS ---
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public void setFavoriteRestaurantId(HashMap<String, Boolean> favoriteRestaurantId) {
        this.favoriteRestaurantId = favoriteRestaurantId;
    }
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
