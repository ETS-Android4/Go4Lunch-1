package com.openclassrooms.p7.go4lunch.model;

import java.util.List;

public class User {

    private final String uid;
    private final String userName;
    private final String photoUrl;


    public User(String uid, String userName, String photoUrl) {
        this.uid = uid;
        this.userName = userName;
        this.photoUrl = photoUrl;
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
}
