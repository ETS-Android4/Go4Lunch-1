package com.openclassrooms.p7.go4lunch.notification;

import static android.app.Application.getProcessName;
import static android.content.ContentValues.TAG;

import static com.openclassrooms.p7.go4lunch.notification.Notification.CHANNEL_ID;

import android.app.Application;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.openclassrooms.p7.go4lunch.R;
import com.openclassrooms.p7.go4lunch.injector.DI;
import com.openclassrooms.p7.go4lunch.model.User;
import com.openclassrooms.p7.go4lunch.repository.UserRepository;
import com.openclassrooms.p7.go4lunch.service.ApiService;
import com.openclassrooms.p7.go4lunch.ui.MainActivity;
import com.openclassrooms.p7.go4lunch.ui.UserAndRestaurantViewModel;

import java.util.Objects;
import java.util.concurrent.TimeUnit;


public class PushNotificationService extends Worker {

    public PushNotificationService(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Data inputData = getInputData();
        String userId = inputData.getString("user_id");
        UserRepository userDataSource = UserRepository.getInstance();
        userDataSource.getUserData(userId).addOnCompleteListener(task -> {
            DocumentSnapshot documentSnapshot = task.getResult();
            User user = documentSnapshot.toObject(User.class);
            createNotification(Objects.requireNonNull(user));
        });
        return Result.success();
    }

    private void createNotification(User user) {
        ApiService apiService = DI.getRestaurantApiService();
        String userName = apiService.makeUserFirstName(user.getUserName());
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.meal_v2_half_size)
                .setContentTitle("il est midi " + user.getUserName())
                .setContentText("l'index actuel est le: ")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_EVENT)
                .setContentIntent(pendingIntent)
                .build();

        notificationManagerCompat.notify(1, notification);
    }

    public static void periodicRequest(Data data, Context context) {
        PeriodicWorkRequest downloadRequest = new PeriodicWorkRequest.Builder(PushNotificationService.class, 15, TimeUnit.MINUTES)
                .setInputData(data)
//                .setConstraints(setConstraints)
//                .setInitialDelay(10, TimeUnit.SECONDS)
                .addTag("download")
                .build();

        WorkManager.getInstance(context).enqueue(downloadRequest);
    }

    public Constraints setConstrains() {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        return constraints;
    }
}
