package com.openclassrooms.p7.go4lunch.repository;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.openclassrooms.p7.go4lunch.R;
import com.openclassrooms.p7.go4lunch.ui.MainActivity;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class Go4LunchWorker extends Worker {

    public Go4LunchWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public Result doWork() {

        showNotif();

        return Result.success();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void uploadWorkRequest() {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build();

        Duration duration = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            duration = Duration.between(LocalTime.now(), LocalTime.of(12, 0));
        }

        PeriodicWorkRequest uploadWorkRequest = new PeriodicWorkRequest.Builder(Go4LunchWorker.class, 15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .setInitialDelay(30, TimeUnit.SECONDS)
                .addTag("periodic")
                .build();

        WorkManager.getInstance().enqueueUniquePeriodicWork("periodic", ExistingPeriodicWorkPolicy.REPLACE, uploadWorkRequest);
    }

   public void showNotif() {
       Intent intent = new Intent(getApplicationContext(), MainActivity.class);
       intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

       PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);


       NotificationCompat.Builder notificationCompat = new NotificationCompat.Builder(getApplicationContext(), "channel")
               .setSmallIcon(R.drawable.login_meal_icon)
               .setContentTitle("c'ets midi")
               .setContentText("t'as choisis de manger la bas")
               .setContentIntent(pendingIntent)
               .setAutoCancel(true);

       NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
       notificationManagerCompat.notify(4, notificationCompat.build());
   }
}
