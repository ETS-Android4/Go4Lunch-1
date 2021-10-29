package com.openclassrooms.p7.go4lunch.model;

import android.net.Uri;
import android.widget.TextView;

public class User {

    private String uid;
    private String userName;
    private String email;
    private Uri photoUrl;

    public User(String uid, String userName, String email, Uri photoUrl) {
        this.uid = uid;
        this.userName = userName;
        this.email = email;
        this.photoUrl = photoUrl;
    }

    // --- GETTERS ---
    public String getUid() {
        return uid;
    }
    public String getUserName() {
        return userName;
    }
    public String getEmail() {
        return email;
    }
    public Uri getPhotoUrl() {
        return photoUrl;
    }

    // --- SETTERS ---
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setPhotoUrl(Uri photoUrl) {
        this.photoUrl = photoUrl;
    }
}
