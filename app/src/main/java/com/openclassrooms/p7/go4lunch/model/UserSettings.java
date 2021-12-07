package com.openclassrooms.p7.go4lunch.model;

import android.app.Application;

public class UserSettings extends Application {

    public static final String PREFERENCES = "preferences";
    public static final String CUSTOM_THEME = "customTheme";
    public static final String LIGHT_THEME = "lightTheme";
    public static final String DARK_THEME = "darkTheme";
    public static final String NOTIFICATION = "notification";
    public static final String NOTIFICATION_ENABLED = "notification_enabled";
    public static final String NOTIFICATION_DISABLED = "notification_disabled";
    private String customTheme;
    private String notification;

    // --- GETTERS ---
    public String getCustomTheme() {
        return customTheme;
    }
    public String getNotification() {
        return notification;
    }

    // --- SETTERS ---
    public void setCustomTheme(String customTheme) {
        this.customTheme = customTheme;
    }
    public void setNotification(String notification) {
        this.notification = notification;
    }
}
