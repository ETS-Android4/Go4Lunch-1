package com.openclassrooms.p7.go4lunch.injector;

import android.app.Application;
import android.content.Context;

public class Go4LunchApplication extends Application {
    private static boolean isRunningTest;
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        try {
            Class.forName("androidx.test.runner.AndroidJUnit4");
            isRunningTest = true;
        } catch (ClassNotFoundException e) {
            isRunningTest = false;
        }
    }

    public static boolean isIsRunningTest() {
        return isRunningTest;
    }

    public static Context getContext() {
        return context;
    }
}
