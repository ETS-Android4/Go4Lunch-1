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
import com.openclassrooms.p7.go4lunch.repository.MapViewRepository;
import com.openclassrooms.p7.go4lunch.repository.RestaurantDataRepository;
import com.openclassrooms.p7.go4lunch.repository.UserRepository;

import java.util.List;
import java.util.Map;

public class UserAndRestaurantViewModel extends ViewModel {


    private final UserRepository userDataSource;
    private final RestaurantDataRepository restaurantDataSource;
    private final MapViewRepository mapDataSource;

    public UserAndRestaurantViewModel(UserRepository userRepository, RestaurantDataRepository restaurantDataRepository, MapViewRepository mapViewRepository) {
        userDataSource = userRepository;
        restaurantDataSource = restaurantDataRepository;
        mapDataSource = mapViewRepository;
    }
    public void createUser(){
        userDataSource.createFireStoreUser();
    }
    public void updateUser(User user) {
        userDataSource.updateUser(user);
    }
    public void deleteUserFromFirestore() {
        userDataSource.deleteUserFromFirestore();
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

    //                  --- GOOGLE MAPS ---


    public RestaurantFavorite getCurrentRestaurantData(String currentRestaurantId) {
        return restaurantDataSource.getCurrentRestaurantData(currentRestaurantId);
    }

    public void createRestaurantFavorite(RestaurantFavorite restaurantFavorite) {
        restaurantDataSource.createRestaurantData(restaurantFavorite);
        restaurantDataSource.getRestaurantData();
    }

    public void deleteRestaurantFavorite(RestaurantFavorite restaurantFavorite) {
        restaurantDataSource.deleteRestaurantData(restaurantFavorite);
        restaurantDataSource.getRestaurantData();
    }

    public void setNumberOfFriendInterested(List<User> allInterestedUsers) {
        mapDataSource.setNumberOfFriendInterested(allInterestedUsers);
    }
}
