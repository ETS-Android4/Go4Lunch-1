package com.openclassrooms.p7.go4lunch.service;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.openclassrooms.p7.go4lunch.injector.Go4LunchApplication;

public class CustomService extends Service {

    private static String TAG = CustomService.class.getSimpleName();
    private static CustomService INSTANCE;
    private NotificationManagerCompat notificationManagerCompat;

    public static CustomService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CustomService();
        }
        return INSTANCE;
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, CustomService.class);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate()");
        notificationManagerCompat = NotificationManagerCompat.from(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.e(TAG, "onStartCommand()");

        Notification notification = createNotification(this);

        startForeground(Go4LunchApplication.CHANNEL_ID_INT, notification);
        return START_STICKY;
    }

    public Notification createNotification(Context context) {
        Log.e(TAG, "createNotification: ");
        return new NotificationCompat.Builder(context, Go4LunchApplication.CHANNEL_ID_STRING)
                .setOngoing(true)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setContentTitle("c'est la notification")
                .setContentText("j'ai été notifié")
                .build();
    }


}
