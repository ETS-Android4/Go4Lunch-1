package com.openclassrooms.p7.go4lunch.ui;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.openclassrooms.p7.go4lunch.model.Restaurant;
import com.openclassrooms.p7.go4lunch.model.RestaurantFavorite;
import com.openclassrooms.p7.go4lunch.model.User;
import com.openclassrooms.p7.go4lunch.repository.FirebaseHelper;
import com.openclassrooms.p7.go4lunch.repository.MapViewRepository;
import com.openclassrooms.p7.go4lunch.repository.RestaurantDataRepository;
import com.openclassrooms.p7.go4lunch.repository.UserRepository;

import java.util.List;
import java.util.Map;

public class UserAndRestaurantViewModel extends ViewModel {

    private final UserRepository userDataSource;
    private final RestaurantDataRepository restaurantDataSource;
    private final MapViewRepository mapDataSource;

    public UserAndRestaurantViewModel() {
        userDataSource = UserRepository.getInstance();
        restaurantDataSource = RestaurantDataRepository.getInstance();
        mapDataSource = MapViewRepository.getInstance();
    }

    public LiveData<List<User>> getAllUsers() {
        MutableLiveData<List<User>> userMutableLiveData = new MutableLiveData<>();
        userMutableLiveData.setValue(userDataSource.getListOfUsers().getValue());
        return userMutableLiveData;
    }

    public LiveData<List<User>> getAllInterestedUsers() {
        MutableLiveData<List<User>> userMutableLiveData = new MutableLiveData<>();
        userMutableLiveData.setValue(userDataSource.getListOfUserInterested().getValue());
        return userMutableLiveData;
    }

    public LiveData<List<User>> getAllInterestedUsersAtCurrentRestaurant(String restaurantId) {
        MutableLiveData<List<User>> userMutableLiveData = new MutableLiveData<>();
        userMutableLiveData.setValue(userDataSource.getListOfUsersInterestedAtCurrentRestaurant(restaurantId).getValue());
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
    public FirebaseUser getCurrentUser() {
        return userDataSource.getCurrentUser();
    }
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
    public void updateUser(User user) {
        userDataSource.updateFirestoreUser(user);
        userDataSource.getListOfUsers();
    }
    public void deleteUserFromFirestore() { userDataSource.deleteFirestoreUser(); }
    //                  --- GOOGLE MAPS ---
    public void requestForPlaceDetails(List<String> listOfPlaceId, Context context, boolean isSearched) {
        mapDataSource.requestForPlaceDetails(listOfPlaceId, context, isSearched);
    }

    public LiveData<Map<String, RestaurantFavorite>> getRestaurantData() {
        MutableLiveData<Map<String, RestaurantFavorite>> restaurantDataMutableLiveData = new MutableLiveData<>();
        restaurantDataMutableLiveData.setValue(restaurantDataSource.getRestaurantData().getValue());
        return restaurantDataMutableLiveData;
    }

    public RestaurantFavorite getCurrentRestaurantData(String currentRestaurantId) {
        return restaurantDataSource.getCurrentRestaurantData(currentRestaurantId);
    }

    public void initData() {
        userDataSource.getListOfUsers();
        restaurantDataSource.getRestaurantData();
        userDataSource.getListOfUserInterested();
    }

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
