package com.openclassrooms.p7.go4lunch.ui;

import android.content.Context;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QuerySnapshot;
import com.openclassrooms.p7.go4lunch.model.FavoriteOrSelectedRestaurant;
import com.openclassrooms.p7.go4lunch.repository.CurrenUserRepository;
import com.openclassrooms.p7.go4lunch.repository.FavoriteOrSelectedRestaurantRepository;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class UserAndRestaurantViewModel extends androidx.lifecycle.ViewModel {

    private final CurrenUserRepository userDataSource = CurrenUserRepository.getInstance();
    private final FavoriteOrSelectedRestaurantRepository favoriteOrSelectedRestaurantDataSource = FavoriteOrSelectedRestaurantRepository.getInstance();
    private final Executor executor = Executors.newSingleThreadExecutor();


    // --- FOR USER ---
    public void createUser(){
        userDataSource.createUser();
    }
    public FirebaseUser getCurrentUser() { return userDataSource.getCurrentUser(); }
    public void getUsersDataList() { userDataSource.getUsersDataList(); }
    public void updateUser(String key, Object object) { userDataSource.updateUser(key, object);}
    public Task<Void> deleteUser(Context context) { return userDataSource.deleteUser(context); }

    public Boolean isCurrentUserLogged() {
        return (this.getCurrentUser() != null);
    }
    public Task<Void> signOut(Context context) {
        return userDataSource.signOut(context);
    }

    // --- FOR RESTAURANT AND USER ---
    public void createFavoriteRestaurant(String userId, FavoriteOrSelectedRestaurant favoriteOrSelectedRestaurant) {
            favoriteOrSelectedRestaurantDataSource.createFavoriteRestaurant(userId, favoriteOrSelectedRestaurant);
    }
    public Task<QuerySnapshot> getFavoriteOrSelectedRestaurant(String uid) {
        return favoriteOrSelectedRestaurantDataSource.getFavoriteOrSelectedRestaurant(uid);
    }

    public void getFavoriteOrSelectedRestaurantList() { favoriteOrSelectedRestaurantDataSource.getFavoriteOrRestaurantRestaurantList(); }

    public void deleteFavoriteRestaurant(String uid) {
            favoriteOrSelectedRestaurantDataSource.deleteFavoriteRestaurant(uid);
    }
    public void updateFavoriteRestaurant(String uid, String restaurantId, boolean isFavorite) {
            favoriteOrSelectedRestaurantDataSource.updateFavoriteRestaurant(uid, restaurantId, isFavorite);
    }
    public void updateSelectedRestaurant(String uid, String restaurantId, boolean isSelected) {
            favoriteOrSelectedRestaurantDataSource.updateSelectedRestaurant(uid, restaurantId, isSelected);
    }
}
