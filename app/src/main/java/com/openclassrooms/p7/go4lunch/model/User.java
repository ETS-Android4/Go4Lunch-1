package com.openclassrooms.p7.go4lunch.model;

import androidx.annotation.Nullable;

import java.util.Map;

public class User {

    private  String uid;
    private  String userName;
    private  String photoUrl;
    private Map<String, UserAndRestaurant> userAndRestaurant;

    // --- CONSTRUCTOR ---
    public User() { }
    public User(String uid, String userName, String photoUrl, Map<String, UserAndRestaurant> userAndRestaurant) {
        this.uid = uid;
        this.userName = userName;
        this.photoUrl = photoUrl;
        this.userAndRestaurant = userAndRestaurant;
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
    public Map<String, UserAndRestaurant> getUserAndRestaurant() {
        return userAndRestaurant;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return super.equals(obj);
    }
}
