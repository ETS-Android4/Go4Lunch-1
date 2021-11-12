package com.openclassrooms.p7.go4lunch.ui;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.openclassrooms.p7.go4lunch.model.UserAndRestaurant;
import com.openclassrooms.p7.go4lunch.repository.CurrenUserRepository;
import com.openclassrooms.p7.go4lunch.repository.MapViewRepository;

import java.util.Map;

public class UserAndRestaurantViewModel extends androidx.lifecycle.ViewModel {

    private final CurrenUserRepository userDataSource = CurrenUserRepository.getInstance();
    private final MapViewRepository mapDataSource = MapViewRepository.getInstance();

    //                   --- FOR USER ---
    public void createUser(){
        userDataSource.createUser();
    }
    public FirebaseUser getCurrentUser() { return userDataSource.getCurrentUser(); }
    public void getUsersDataList() { userDataSource.getUsersDataList(); }
    public void updateUser(String currentUserID, Map<String, UserAndRestaurant> likedOrSelectedRestaurant) { userDataSource.updateUser(currentUserID, likedOrSelectedRestaurant);}
    public Task<Void> deleteUser(Context context) { return userDataSource.deleteUser(context); }
    public Boolean isCurrentUserLogged() {
        return (this.getCurrentUser() != null);
    }
    public Task<Void> signOut(Context context) {
        return userDataSource.signOut(context);
    }

    //                  --- GOOGLE MAPS ---
    public void requestForPlaceDetails(String placeId, Context context, boolean isSearched, GoogleMap map) {
        mapDataSource.requestForPlaceDetails(placeId, context, isSearched, map);
    }
}
