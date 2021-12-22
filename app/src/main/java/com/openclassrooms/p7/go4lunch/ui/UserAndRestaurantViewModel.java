package com.openclassrooms.p7.go4lunch.ui;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.openclassrooms.p7.go4lunch.model.Restaurant;
import com.openclassrooms.p7.go4lunch.model.RestaurantFavorite;
import com.openclassrooms.p7.go4lunch.model.User;
import com.openclassrooms.p7.go4lunch.repository.FirebaseHelper;
import com.openclassrooms.p7.go4lunch.repository.MapViewRepository;
import com.openclassrooms.p7.go4lunch.repository.RestaurantDataRepository;
import com.openclassrooms.p7.go4lunch.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class UserAndRestaurantViewModel extends ViewModel {

    private final FirebaseHelper mFirebaseHelper;
    private final UserRepository userDataSource;
    private final RestaurantDataRepository restaurantDataSource;
    private final MapViewRepository mapDataSource;

//    private final MutableLiveData<User> currentUser = new MutableLiveData<>();
//    private final MutableLiveData<List<User>> listOfUser = new MutableLiveData<>();
//    private final MutableLiveData<List<User>> listOfUserInterested = new MutableLiveData<>();

    public UserAndRestaurantViewModel() {
        mFirebaseHelper = FirebaseHelper.getInstance();
        userDataSource = UserRepository.getInstance();
        restaurantDataSource = RestaurantDataRepository.getInstance();
        mapDataSource = MapViewRepository.getInstance();
    }
    public void createUser(){
        userDataSource.createFireStoreUser();
    }
    public void updateUser(User user) {
        mFirebaseHelper.getUsersCollection().document(user.getUid()).update(
                "restaurantId", user.getRestaurantId(),
                "restaurantName", user.getRestaurantName(),
                "restaurantSelected", user.isRestaurantSelected()
        );
    }
    public void deleteUserFromFirestore() {
        String uid = Objects.requireNonNull(mFirebaseHelper.getCurrentUser()).getUid();
        mFirebaseHelper.getUsersCollection().document(uid).collection("restaurants").document().delete();
        mFirebaseHelper.getUsersCollection().document(uid).delete();
    }
    public LiveData<List<User>> getAllUsers() {
        return userDataSource.getAllUsers();
    }

    public LiveData<List<User>> getAllInterestedUsers() {
        return userDataSource.getAllInterestedUsers();
    }

    public List<User> getAllInterestedUsersAtCurrentRestaurant(String restaurantId, List<User> users) {
        return userDataSource.getAllInterestedUsersAtCurrentRestaurant(restaurantId, users);
    }

    public MutableLiveData<User> getCurrentFirestoreUser() {
        return userDataSource.getCurrentFirestoreUser();
    }

    public LiveData<List<Restaurant>> getAllRestaurants() {
        return mapDataSource.getAllRestaurantList();
    }

    public void requestForPlaceDetails(List<String> listOfPlaceId, Context context, boolean isSearched) {
        mapDataSource.requestForPlaceDetails(listOfPlaceId, context, isSearched);
    }

    public Restaurant getCurrentRestaurant(String restaurantId) { return mapDataSource.getCurrentRestaurant(restaurantId); }


    //                   --- FOR USER FIREBASE ---
    public FirebaseUser getCurrentUser() {
        return mFirebaseHelper.getCurrentUser();
    }
    public Task<Void> deleteFirebaseUser(Context context) { return mFirebaseHelper.deleteUser(context); }
    public Boolean isCurrentUserLogged() {
        return (this.getCurrentUser() != null);
    }
    public Task<Void> signOut(Context context) {
        return mFirebaseHelper.signOut(context);
    }
    //                   --- FOR USER FIRESTORE---

    //                  --- GOOGLE MAPS ---


    public LiveData<Map<String, RestaurantFavorite>> getRestaurantData() {
        MutableLiveData<Map<String, RestaurantFavorite>> restaurantDataMutableLiveData = new MutableLiveData<>();
        restaurantDataMutableLiveData.setValue(restaurantDataSource.getRestaurantData().getValue());
        return restaurantDataMutableLiveData;
    }

    public RestaurantFavorite getCurrentRestaurantData(String currentRestaurantId) {
        return restaurantDataSource.getCurrentRestaurantData(currentRestaurantId);
    }

//    public void initData() {
//        userDataSource.getListOfUsers();
//        restaurantDataSource.getRestaurantData();
//        userDataSource.getListOfUserInterested();
//    }

    public void createRestaurantFavorite(RestaurantFavorite restaurantFavorite) {
        restaurantDataSource.createRestaurantData(restaurantFavorite);
        restaurantDataSource.getRestaurantData();
    }

    public void deleteRestaurantFavorite(RestaurantFavorite restaurantFavorite) {
        restaurantDataSource.deleteRestaurantData(restaurantFavorite);
        restaurantDataSource.getRestaurantData();
    }

    public void setNumberOfFriendInterested(LiveData<List<User>> allInterestedUsers) {
        mapDataSource.setNumberOfFriendInterested(allInterestedUsers.getValue());
    }
}
