package com.openclassrooms.p7.go4lunch.ui;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.openclassrooms.p7.go4lunch.model.Restaurant;
import com.openclassrooms.p7.go4lunch.model.User;
import com.openclassrooms.p7.go4lunch.model.UserAndRestaurant;
import com.openclassrooms.p7.go4lunch.repository.FirebaseHelper;
import com.openclassrooms.p7.go4lunch.repository.MapViewRepository;
import com.openclassrooms.p7.go4lunch.repository.RestaurantDataRepository;
import com.openclassrooms.p7.go4lunch.repository.UserRepository;
import com.openclassrooms.p7.go4lunch.ui.fragment.list_view.ListViewAdapter;

import java.util.List;
import java.util.Map;

public class UserAndRestaurantViewModel extends ViewModel {

    private final UserRepository userDataSource;
    private final RestaurantDataRepository restaurantDataSource;
    private final MapViewRepository mapDataSource;
    private final FirebaseHelper firebaseHelperDataSource;

    public UserAndRestaurantViewModel() {
        userDataSource = UserRepository.getInstance();
        restaurantDataSource = RestaurantDataRepository.getInstance();
        mapDataSource = MapViewRepository.getInstance();
        firebaseHelperDataSource = FirebaseHelper.getInstance();
    }

    public LiveData<List<User>> getAllUsers() {
        MutableLiveData<List<User>> userMutableLiveData = new MutableLiveData<>();
        userMutableLiveData.setValue(userDataSource.getListOfUsers().getValue());
        return userMutableLiveData;
    }

    public LiveData<List<User>> getAllInterestedUsers() {
        MutableLiveData<List<User>> userMutableLiveData = new MutableLiveData<>();
        userMutableLiveData.setValue(userDataSource.getListOfUsersInterested().getValue());
        return userMutableLiveData;
    }

    public User getCurrentFirestoreUser(String userId) { return  userDataSource.getFirestoreUser(userId); }

    public LiveData<List<Restaurant>> getAllRestaurants() {
        MutableLiveData<List<Restaurant>> restaurantMutableLiveData = new MutableLiveData<>();
        restaurantMutableLiveData.setValue(mapDataSource.getAllRestaurantList().getValue());
        return restaurantMutableLiveData;
    }

    public Restaurant getCurrentRestaurant(String restaurantId) { return mapDataSource.getCurrentRestaurant(restaurantId); }


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
    public void updateUser(User user) { userDataSource.updateFirestoreUser(user);}
    public void deleteUserFromFirestore() { userDataSource.deleteFirestoreUser(); }
    //                  --- GOOGLE MAPS ---
    public void requestForPlaceDetails(String placeId, Context context, boolean isSearched) {
        mapDataSource.requestForPlaceDetails(placeId, context, isSearched);
    }
    public void requestForPlaceDetails(String placeId, Context context, RecyclerView mRecyclerView, ListViewAdapter listViewAdapter) {
//        mapDataSource.requestForPlaceDetails(placeId, context, mRecyclerView, listViewAdapter);
    }

    public void createRestaurantData(UserAndRestaurant userAndRestaurant) {
        restaurantDataSource.createRestaurantData(userAndRestaurant);
    }

    public LiveData<Map<String, UserAndRestaurant>> getRestaurantData() {
        MutableLiveData<Map<String, UserAndRestaurant>> restaurantDataMutableLiveData = new MutableLiveData<>();
        restaurantDataMutableLiveData.setValue(restaurantDataSource.getRestaurantData().getValue());
        return restaurantDataMutableLiveData;
    }

    public void updateRestaurantData(UserAndRestaurant userAndRestaurant) {
        restaurantDataSource.updateRestaurantData(userAndRestaurant);
    }

    public UserAndRestaurant getCurrentRestaurantData(String currentRestaurantId) {
        return restaurantDataSource.getCurrentRestaurantData(currentRestaurantId);
    }

    public void initData() {
        restaurantDataSource.getRestaurantData();
        userDataSource.getListOfUsers();
    }

    public void onDataChanged(String userId) {
        userDataSource.onDataChangedToFalse(userId);
        userDataSource.onDataChangedToTrue(userId);
    }
}
