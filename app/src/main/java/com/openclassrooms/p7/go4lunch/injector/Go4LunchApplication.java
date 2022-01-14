package com.openclassrooms.p7.go4lunch.injector;

import static android.content.ContentValues.TAG;
import static com.openclassrooms.p7.go4lunch.notification.PushNotificationService.periodicTimeRequest;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationManagerCompat;

public class Go4LunchApplication extends Application {
    private static boolean isRunningTest;
    private static Context context;
    public static final String CHANNEL_ID_STRING = "go4lunch_channel";

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        try {
            Class.forName("com.openclassrooms.p7.go4lunch.UserRepositoryAndRestaurantFavoriteRepositoryTest");
            isRunningTest = true;
        } catch (ClassNotFoundException e) {
            isRunningTest = false;
        }
//        createNotificationChannel();
//        periodicTimeRequest(this);
    }

    public static boolean isIsRunningTest() {
        return isRunningTest;
    }

    public static Context getContext() {
        return context;
    }


//    public static void periodicTimeRequest(Context context) {
//        long timeDiff = setTimeUntilBeginWork();
//        PeriodicWorkRequest timeToLunch = new PeriodicWorkRequest.Builder(PushNotificationService.class, 24, TimeUnit.HOURS)
//                .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
//                .addTag(PERIODIC_TIME_WORK_TAG)
//                .build();
//        WorkManager.getInstance(context).enqueueUniquePeriodicWork("lunch time", ExistingPeriodicWorkPolicy.REPLACE, timeToLunch);
//        Log.e(TAG, "periodicTimeRequest: PERIODIC WORK ENQUEUED");
//    }
}
