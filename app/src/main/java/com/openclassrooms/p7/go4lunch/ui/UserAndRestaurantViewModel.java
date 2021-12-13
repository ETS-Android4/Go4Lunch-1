package com.openclassrooms.p7.go4lunch.ui;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.openclassrooms.p7.go4lunch.model.UserAndRestaurant;
import com.openclassrooms.p7.go4lunch.repository.UserRepository;
import com.openclassrooms.p7.go4lunch.repository.MapViewRepository;
import com.openclassrooms.p7.go4lunch.ui.fragment.list_view.ListViewAdapter;

import java.util.Map;

public class UserAndRestaurantViewModel extends androidx.lifecycle.ViewModel {

    private final UserRepository userDataSource = UserRepository.getInstance();
    private final MapViewRepository mapDataSource = MapViewRepository.getInstance();

    //                   --- FOR USER FIREBASE ---
    public Task<DocumentSnapshot> getUserData() { return userDataSource.getUserData();}
    public CollectionReference getUserCollection() { return userDataSource.getUsersCollection(); }
    public FirebaseUser getCurrentUser() { return userDataSource.getCurrentUser(); }
    public Task<Void> deleteFirebaseUser(Context context) { return userDataSource.deleteUser(context); }
    public Boolean isCurrentUserLogged() {
        return (this.getCurrentUser() != null);
    }
    public Task<Void> signOut(Context context) {
        return userDataSource.signOut(context);
    }
    //                   --- FOR USER FIRESTORE---
    public void createUser(){
        userDataSource.createFireStoreUser();
    }
    public void getUsersDataList() { userDataSource.getFirestoreUsersDataList(); }
    public void updateUser(String currentUserID, Map<String, UserAndRestaurant> likedOrSelectedRestaurant) { userDataSource.updateFirestoreUser(currentUserID, likedOrSelectedRestaurant);}
    public void deleteUserFromFirestore() { userDataSource.deleteFirestoreUser(); }
    //                  --- GOOGLE MAPS ---
    public void requestForPlaceDetails(String placeId, Context context, GoogleMap map, boolean isSearched) {
        mapDataSource.requestForPlaceDetails(placeId, context, map, isSearched);
    }
    public void requestForPlaceDetails(String placeId, Context context, RecyclerView mRecyclerView, ListViewAdapter listViewAdapter) {
        mapDataSource.requestForPlaceDetails(placeId, context, mRecyclerView, listViewAdapter);
    }
}
