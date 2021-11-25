package com.openclassrooms.p7.go4lunch.repository;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.openclassrooms.p7.go4lunch.R;
import com.openclassrooms.p7.go4lunch.injector.DI;
import com.openclassrooms.p7.go4lunch.model.Restaurant;
import com.openclassrooms.p7.go4lunch.model.User;
import com.openclassrooms.p7.go4lunch.model.UserAndRestaurant;
import com.openclassrooms.p7.go4lunch.service.ApiService;
import com.openclassrooms.p7.go4lunch.ui.MainActivity;
import com.openclassrooms.p7.go4lunch.ui.UserAndRestaurantViewModel;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PushNotificationService extends Application {

    private final int NOTIFICATION_ID = 007;
    private final String NOTIFICATION_TAG = "GO4LUNCH";
    public static final String CHANNEL_ID = "LUNCH_NOTIFICATION";
    private ApiService mApiService;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    NOTIFICATION_TAG,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("il est midi");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        configureServiceAndViewModel();
        String title = Objects.requireNonNull(remoteMessage.getNotification()).getTitle();
        String body = remoteMessage.getNotification().getBody();
        User currentUser = mApiService.searchUserById(MainActivity.CURRENT_USER_ID);
        UserAndRestaurant selectedRestaurant = mApiService.searchSelectedRestaurant(currentUser);
        Restaurant currentRestaurant = mApiService.searchCurrentRestaurantById(selectedRestaurant.getRestaurantId());
        List<User> interestedFriendList = mApiService.getUsersInterestedAtCurrentRestaurant(currentUser.getUid(), currentRestaurant);
        String friendsInterested = mApiService.makeInterestedFriendsString(interestedFriendList);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    NOTIFICATION_TAG,
                    NotificationManager.IMPORTANCE_HIGH
            );
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
            Notification.Builder notification = new Notification.Builder(this, CHANNEL_ID)
                    .setContentTitle(title)
                    .setContentText(body + " Bonjour " + mApiService.makeUserFirstName(currentUser.getUserName()) + " vous avez choisi de manger à " + currentRestaurant.getName() + " avec " + friendsInterested)
                    .setSmallIcon(R.drawable.login_meal_icon)
                    .setAutoCancel(true);
            NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, notification.build());
        }
    }

    private void configureServiceAndViewModel() {
        mApiService = DI.getRestaurantApiService();
    }
}
