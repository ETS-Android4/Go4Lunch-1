package com.openclassrooms.p7.go4lunch.model;

import java.util.HashMap;
import java.util.List;

public class User {

    private String uid;
    private String userName;
    private String photoUrl;


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

    // --- SETTERS ---
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
