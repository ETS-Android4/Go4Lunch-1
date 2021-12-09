package com.openclassrooms.p7.go4lunch.model;

import android.app.Application;

public class UserSettings extends Application {

    public static final String PREFERENCES = "preferences";
    public static final String NOTIFICATION = "notification";
    public static final String NOTIFICATION_ENABLED = "notification_enabled";
    public static final String NOTIFICATION_DISABLED = "notification_disabled";
    private String notification;

    // --- GETTERS ---
    public String getNotification() {
        return notification;
    }

    // --- SETTERS ---
    public void setNotification(String notification) {
        this.notification = notification;
    }
}
