package com.openclassrooms.p7.go4lunch.notification;

import static com.openclassrooms.p7.go4lunch.notification.Notification.CHANNEL_ID;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import androidx.work.Constraints;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.openclassrooms.p7.go4lunch.R;
import com.openclassrooms.p7.go4lunch.injector.DI;
import com.openclassrooms.p7.go4lunch.model.User;
import com.openclassrooms.p7.go4lunch.model.UserAndRestaurant;
import com.openclassrooms.p7.go4lunch.repository.UserRepository;
import com.openclassrooms.p7.go4lunch.service.ApiService;
import com.openclassrooms.p7.go4lunch.ui.MainActivity;

import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;



public class PushNotificationService extends Worker{

    private static final String ONE_TIME_WORK_TAG = "lunch_alert";
    private static final String TAG = PushNotificationService.class.getName();
    private Context mContext;
    private WorkManager mWorkManager;
    private UserRepository mUserDataSource = UserRepository.getInstance();


    public PushNotificationService(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mContext = context;
        mWorkManager = WorkManager.getInstance(context);

    }

    @NonNull
    @Override
    public Result doWork() {
        String userId = Objects.requireNonNull(mUserDataSource.getCurrentUser()).getUid();
//        Task task = mUserDataSource.getUsersCollection().document(Objects.requireNonNull(userId)).get();
//        DocumentSnapshot documentSnapshot = (DocumentSnapshot) task.getResult();
//        User user = documentSnapshot.toObject(User.class);
        mUserDataSource.getUsersDataList();
        createNotification(userId);
        return Result.success();
    }

    private void createNotification(String userId) {
        ApiService apiService = DI.getRestaurantApiService();
        User user = apiService.searchUserById(userId);
        String userName = apiService.makeUserFirstName(user.getUserName());
        UserAndRestaurant userAndRestaurantSelected = apiService.getCurrentuserSelectedRestaurant(user);
        List<User> interestedFriends = apiService.getUsersInterestedAtCurrentRestaurant(user.getUid());
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.meal_v2_half_size)
                .setContentTitle("It's 12am " + userName)
                .setContentText("you have chosen to eat at " + userAndRestaurantSelected.getRestaurantName())
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText( "Friends that come with you:" + apiService.makeInterestedFriendsString(interestedFriends))
                        .setBigContentTitle("It's 12am"))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_EVENT)
                .setContentIntent(pendingIntent)
                .build();

        notificationManagerCompat.notify(1, notification);
    }

    public static void oneTimeRequest(Context context) {
        long timeDiff = setTimeUntilBeginWork();
        OneTimeWorkRequest timeToLunch = new OneTimeWorkRequest.Builder(PushNotificationService.class)
                .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
                .addTag(ONE_TIME_WORK_TAG)
                .build();
        WorkManager.getInstance(context).enqueue(timeToLunch);
        }

    public static long setTimeUntilBeginWork() {
        Calendar calendar = Calendar.getInstance();
        Calendar currentDate = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.before(currentDate)) {
            calendar.add(Calendar.HOUR_OF_DAY,24);
        }
        return calendar.getTimeInMillis() - currentDate.getTimeInMillis();
    }


    public static Constraints setConstrains() {
        Constraints constraints = new Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build();

        return constraints;
    }
}
