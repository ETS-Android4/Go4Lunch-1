package com.openclassrooms.p7.go4lunch.ui;

import androidx.annotation.NonNull;

import com.openclassrooms.p7.go4lunch.model.User;
import com.openclassrooms.p7.go4lunch.model.UserAndRestaurant;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UserStateItem {

    @NonNull
    private final String uid;
    @NonNull
    private final String userName;
    @NonNull
    private final String photoUrl;
    @NonNull
    private final Map<String, UserAndRestaurant> userAndRestaurantMap;

    public UserStateItem(User user) {
        this.uid = user.getUid();
        this.userName = user.getUserName();
        this.photoUrl = user.getPhotoUrl();
        this.userAndRestaurantMap = new HashMap<>();
    }

    public UserStateItem(@NonNull String uid, @NonNull String userName, @NonNull String photoUrl, @NonNull Map<String, UserAndRestaurant> userAndRestaurantMap) {
        this.uid = uid;
        this.userName = userName;
        this.photoUrl = photoUrl;
        this.userAndRestaurantMap = userAndRestaurantMap;
    }

    // --- GETTERS ---
    @NonNull
    public String getUid() {
        return uid;
    }

    @NonNull
    public String getUserName() {
        return userName;
    }

    @NonNull
    public String getPhotoUrl() {
        return photoUrl;
    }

    @NonNull
    public Map<String, UserAndRestaurant> getUserAndRestaurantMap() {
        return userAndRestaurantMap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserStateItem that = (UserStateItem) o;
        return uid.equals(that.uid) &&
                userName.equals(that.userName) &&
                photoUrl.equals(that.photoUrl) &&
                userAndRestaurantMap.equals(that.userAndRestaurantMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, userName, photoUrl, userAndRestaurantMap);
    }
}
