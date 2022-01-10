package com.openclassrooms.p7.go4lunch.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.openclassrooms.p7.go4lunch.R;
import com.openclassrooms.p7.go4lunch.injector.DI;
import com.openclassrooms.p7.go4lunch.injector.Go4LunchApplication;
import com.openclassrooms.p7.go4lunch.model.User;
import com.openclassrooms.p7.go4lunch.repository.FirebaseHelper;
import com.openclassrooms.p7.go4lunch.service.ApiService;
import com.openclassrooms.p7.go4lunch.ui.MainActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


public class PushNotificationService extends Worker {

    private static final String ONE_TIME_WORK_TAG = "lunch_alert";
    private static final String TAG = PushNotificationService.class.getName();
    private final WorkManager mWorkManager;
    private final Context mContext;
    private final FirebaseHelper mFirebaseHelper = FirebaseHelper.getInstance();
    private final ApiService mApiService;


    public PushNotificationService(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mContext = context;
        mApiService = DI.getRestaurantApiService();
        mWorkManager = WorkManager.getInstance(mContext);
    }

    @NonNull
    @Override
    public Result doWork() {
        if (!getCurrentNotification(mContext).equals("notification_enabled")) {
            mWorkManager.cancelUniqueWork("lunch time");
            Log.e(TAG, "doWork: PERIODIC WORK CANCELED");
            return Result.success();
        }
        Task<QuerySnapshot> task = FirebaseFirestore.getInstance().collection("users").get();
        List<DocumentSnapshot> documentSnapshotList = null;
        Log.e(TAG, "doWork: TRY TO DO WORK");
        try {
            documentSnapshotList = Tasks.await(task).getDocuments();
            List<User> userList = new ArrayList<>();
            for (DocumentSnapshot document : documentSnapshotList) {
                User user = document.toObject(User.class);
                assert user != null;
                if (user.isRestaurantSelected()) {
                    Log.e(TAG, "doWork: USER NAME:" + Objects.requireNonNull(user).getUserName());
                    userList.add(user);
                }
            }
            configureNotification(userList);
            return Result.success();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return Result.failure();
        }
    }

    private void configureNotification(List<User> userList) {
        String currentUserId = Objects.requireNonNull(mFirebaseHelper.getCurrentUser()).getUid();
        String whoEatWithUser = mContext.getString(R.string.push_notification_service_list_of_friend_are_come);
        User currentUser = null;
        for (User user : userList) {
            if (user.getUid().equals(currentUserId)) {
                currentUser = user;
            }
        }
        if (currentUser == null) {
            mWorkManager.cancelUniqueWork("lunch time");
        }
        userList.remove(currentUser);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        List<User> interestedFriends = mApiService.getInterestedFriend(userList, Objects.requireNonNull(currentUser).getRestaurantId());
        if (interestedFriends.isEmpty()) {
            whoEatWithUser = mContext.getString(R.string.push_notification_service_nobody_come);
        }
        Log.e(TAG, "createNotification: friends are coming: " + mApiService.formatInterestedFriends(interestedFriends));
        Log.e(TAG, "createNotification: restaurant choice: " + currentUser.getRestaurantName());
        Log.e(TAG, "createNotification: restaurant choice: " + notificationManagerCompat.areNotificationsEnabled());

        Notification notification = this.createNotification(currentUser, whoEatWithUser, interestedFriends, pendingIntent);
        notificationManagerCompat.notify((int) System.currentTimeMillis(), notification);
    }

    private Notification createNotification(User currentUser, String whoEatWithUser, List<User> interestedFriends, PendingIntent pendingIntent) {
        return new NotificationCompat.Builder(getApplicationContext(), Go4LunchApplication.CHANNEL_ID_STRING)
                .setSmallIcon(R.drawable.meal_v2_half_size)
                .setContentTitle(String.format("%s %s", mContext.getString(R.string.push_notification_service_alert), mApiService.formatUserFirstName(currentUser.getUserName())))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(String.format(
                                "%s %s.\n%s %s",
                                mContext.getString(R.string.push_notification_service_restaurant_choice),
                                currentUser.getRestaurantName(),
                                whoEatWithUser,
                                mApiService.formatInterestedFriends(interestedFriends)
                        ))
                        .setBigContentTitle(mContext.getString(R.string.push_notification_service_alert)))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setContentIntent(pendingIntent)
                .build();
    }

    public static void periodicTimeRequest(Context context) {
        long timeDiff = setTimeUntilBeginWork();
        PeriodicWorkRequest timeToLunch = new PeriodicWorkRequest.Builder(PushNotificationService.class, 24, TimeUnit.HOURS)
                .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
                .addTag(ONE_TIME_WORK_TAG)
                .build();
        WorkManager.getInstance(context).enqueueUniquePeriodicWork("lunch time", ExistingPeriodicWorkPolicy.REPLACE, timeToLunch);
    }

    public static long setTimeUntilBeginWork() {
        Calendar calendar = Calendar.getInstance();
        Calendar currentDate = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        if (calendar.before(currentDate)) {
            calendar.add(Calendar.HOUR_OF_DAY, 24);
        }
        return calendar.getTimeInMillis() - currentDate.getTimeInMillis();
    }


    public static Constraints setConstrains() {
        Constraints constraints = new Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build();
        return constraints;
    }

    private String getCurrentNotification(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        return sharedPreferences.getString("notification", "notification_enabled");
    }
}
