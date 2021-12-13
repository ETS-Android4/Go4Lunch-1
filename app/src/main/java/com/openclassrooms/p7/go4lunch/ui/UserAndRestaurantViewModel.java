package com.openclassrooms.p7.go4lunch.ui;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.openclassrooms.p7.go4lunch.model.User;
import com.openclassrooms.p7.go4lunch.model.UserAndRestaurant;
import com.openclassrooms.p7.go4lunch.repository.FirebaseHelper;
import com.openclassrooms.p7.go4lunch.repository.MapViewRepository;
import com.openclassrooms.p7.go4lunch.repository.UserRepository;
import com.openclassrooms.p7.go4lunch.ui.fragment.list_view.ListViewAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserAndRestaurantViewModel extends ViewModel {

    private final UserRepository userDataSource;
    private final MapViewRepository mapDataSource;
    private final FirebaseHelper firebaseHelperDataSource;

    public UserAndRestaurantViewModel() {
        userDataSource = UserRepository.getInstance();
        mapDataSource = MapViewRepository.getInstance();
        firebaseHelperDataSource = FirebaseHelper.getInstance();
    }

    private LiveData<List<UserStateItem>> mapDataToViewState(LiveData<List<User>> users) {
        return Transformations.map(users, user -> {
           List<UserStateItem> userStateItems = new ArrayList<>();
           for (User u : user) {
               userStateItems.add(new UserStateItem(u));
           }
           return userStateItems;
        });
    }

    public LiveData<List<UserStateItem>> getAllUsers() {
        return mapDataToViewState(userDataSource.getFirestoreUsersDataList());
    }

    //                   --- FOR USER FIREBASE ---
    public Task<DocumentSnapshot> getUserData() { return firebaseHelperDataSource.getUserData();}
    public CollectionReference getUserCollection() { return firebaseHelperDataSource.getUsersCollection(); }
    public FirebaseUser getCurrentUser() { return firebaseHelperDataSource.getCurrentUser(); }
    public Task<Void> deleteFirebaseUser(Context context) { return firebaseHelperDataSource.deleteUser(context); }
    public Boolean isCurrentUserLogged() {
        return (this.getCurrentUser() != null);
    }
    public Task<Void> signOut(Context context) {
        return firebaseHelperDataSource.signOut(context);
    }
    //                   --- FOR USER FIRESTORE---
    public void createUser(){
        userDataSource.createFireStoreUser();
    }
    public MutableLiveData<List<User>> getUsersDataList() { return userDataSource.getFirestoreUsersDataList(); }
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
