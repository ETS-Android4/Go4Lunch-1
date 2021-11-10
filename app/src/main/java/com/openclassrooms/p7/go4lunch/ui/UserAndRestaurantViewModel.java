package com.openclassrooms.p7.go4lunch.ui;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.firebase.auth.FirebaseUser;
import com.openclassrooms.p7.go4lunch.model.FavoriteOrSelectedRestaurant;
import com.openclassrooms.p7.go4lunch.repository.CurrenUserRepository;
import com.openclassrooms.p7.go4lunch.repository.MapViewRepository;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

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

    // --- GOOGLE MAPS ---
    public String searchPlace(FragmentActivity fragmentActivity) {
        return mapDataSource.searchPlace(fragmentActivity);
    }
    public Place requestForPlaceDetails(String placeId, Context context) {
        return mapDataSource.requestForPlaceDetails(placeId, context);
    }

    public Bitmap requestForPlacePhoto(Place place, Context applicationContext) {
        return mapDataSource.requestForPlacePhoto(place, applicationContext);
    }
}
