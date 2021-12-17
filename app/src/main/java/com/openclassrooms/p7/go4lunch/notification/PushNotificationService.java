package com.openclassrooms.p7.go4lunch.notification;

import static com.openclassrooms.p7.go4lunch.notification.Notification.CHANNEL_ID;

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
import com.openclassrooms.p7.go4lunch.model.User;
import com.openclassrooms.p7.go4lunch.repository.FirebaseHelper;
import com.openclassrooms.p7.go4lunch.service.ApiService;
import com.openclassrooms.p7.go4lunch.ui.MainActivity;

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
            return Result.success();
        }
        String userId = Objects.requireNonNull(mFirebaseHelper.getCurrentUser()).getUid();
        Task<QuerySnapshot> task = FirebaseFirestore.getInstance().collection("users").get();
        List<DocumentSnapshot> documentSnapshotList = null;
        try {
            documentSnapshotList = Tasks.await(task).getDocuments();
            if (mApiService.getUsers().isEmpty()) {
                for (DocumentSnapshot document : documentSnapshotList) {
                    User user = document.toObject(User.class);
                    Log.d(TAG, "doWork: User name:" + Objects.requireNonNull(user).getUserName());
                    mApiService.addUser(user);
                }
            }
            createNotification(mApiService.searchUserById(userId));
            return Result.success();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return Result.failure();
        }
    }

    private void createNotification(User user) {

        String userName = mApiService.makeUserFirstName(user.getUserName());
//        UserAndRestaurant userAndRestaurantSelected = mApiService.getCurrentUserSelectedRestaurant(user);
//        List<User> interestedFriends = mApiService.getUsersInterestedAtCurrentRestaurantForNotification(user.getUid(), userAndRestaurantSelected.getRestaurantId());
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
//        Log.d(TAG, "createNotification: friends are coming: " + mApiService.makeInterestedFriendsString(interestedFriends));
        Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.meal_v2_half_size)
                .setContentTitle(String.format("%s %s", mContext.getString(R.string.push_notification_service_alert), userName))
//                .setContentText(String.format("%s %s", mContext.getString(R.string.push_notification_service_restaurant_choice), userAndRestaurantSelected.getRestaurantName()))
                .setStyle(new NotificationCompat.BigTextStyle()
//                        .bigText(String.format("%s %s", mContext.getString(R.string.push_notification_service_list_of_friend_are_come), mApiService.makeInterestedFriendsString(interestedFriends)))
                        .setBigContentTitle(mContext.getString(R.string.push_notification_service_alert)))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_EVENT)
                .setContentIntent(pendingIntent)
                .build();

        notificationManagerCompat.notify(1, notification);
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
