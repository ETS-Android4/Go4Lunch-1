package com.openclassrooms.p7.go4lunch.ui;

import android.content.Context;
import android.location.Location;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QuerySnapshot;
import com.openclassrooms.p7.go4lunch.model.FavoriteOrSelectedRestaurant;
import com.openclassrooms.p7.go4lunch.repository.CurrenUserRepository;
import com.openclassrooms.p7.go4lunch.repository.MapViewRepository;

import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class UserAndRestaurantViewModel extends androidx.lifecycle.ViewModel {

    private final CurrenUserRepository userDataSource = CurrenUserRepository.getInstance();
    private final MapViewRepository mapDataSource = MapViewRepository.getInstance();

    //                   --- FOR USER ---
    // --- CRUD ---
    public void createUser(){
        userDataSource.createUser();
    }
    public FirebaseUser getCurrentUser() { return userDataSource.getCurrentUser(); }
    public void getUsersDataList() { userDataSource.getUsersDataList(); }
    public void updateUser(String currentUserID, Map<String, FavoriteOrSelectedRestaurant> likedOrSelectedRestaurant) { userDataSource.updateUser(currentUserID, likedOrSelectedRestaurant);}
    public Task<Void> deleteUser(Context context) { return userDataSource.deleteUser(context); }

    // --- LOGIN/OUT ---
    public Boolean isCurrentUserLogged() {
        return (this.getCurrentUser() != null);
    }
    public Task<Void> signOut(Context context) {
        return userDataSource.signOut(context);
    }

    // --- FOR MAP ---
    public void getDeviceLocation(boolean locationPermissionGranted, FusedLocationProviderClient fusedLocationProviderClient, FragmentActivity fragmentActivity, GoogleMap map) {
        mapDataSource.getDeviceLocation(locationPermissionGranted, fusedLocationProviderClient, fragmentActivity, map);
    }
}
