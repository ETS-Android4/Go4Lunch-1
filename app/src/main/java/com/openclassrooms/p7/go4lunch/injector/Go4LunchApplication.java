package com.openclassrooms.p7.go4lunch.injector;

import android.app.Application;
import android.content.Context;

import com.openclassrooms.p7.go4lunch.notification.PushNotificationService;

public class Go4LunchApplication extends Application {
    private static boolean isRunningTest;
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        try {
            Class.forName("com.openclassrooms.p7.go4lunch.UserRepositoryAndRestaurantFavoriteTest");
            isRunningTest = true;
        } catch (ClassNotFoundException e) {
            isRunningTest = false;
        }
        PushNotificationService.periodicTimeRequest(getApplicationContext());
    }

    public static boolean isIsRunningTest() {
        return isRunningTest;
    }

    public static Context getContext() {
        return context;
    }
}
