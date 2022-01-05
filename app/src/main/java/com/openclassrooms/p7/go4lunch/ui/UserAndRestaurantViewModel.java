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
import com.openclassrooms.p7.go4lunch.repository.PlaceTask;
import com.openclassrooms.p7.go4lunch.repository.RestaurantFavoriteRepository;
import com.openclassrooms.p7.go4lunch.repository.UserRepository;

import java.util.List;

public class UserAndRestaurantViewModel extends ViewModel {


    private final UserRepository userDataSource;
    private final RestaurantFavoriteRepository restaurantDataSource;
    private final MapViewRepository mapDataSource;
    private final PlaceTask placeTaskExecutor;

    public UserAndRestaurantViewModel(
            UserRepository userRepository,
            RestaurantFavoriteRepository restaurantFavoriteRepository,
            MapViewRepository mapViewRepository,
            PlaceTask placeTask
    ) {
        userDataSource = userRepository;
        restaurantDataSource = restaurantFavoriteRepository;
        mapDataSource = mapViewRepository;
        placeTaskExecutor = placeTask;
    }

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

    //                  --- GOOGLE MAPS ---
    public void requestForPlaceDetails(List<String> listOfPlaceId, Context context, boolean isSearched) {
        mapDataSource.requestForPlaceDetails(listOfPlaceId, context, isSearched);
    }
    public Restaurant getCurrentRestaurant(String restaurantId, List<Restaurant> restaurantList) { return mapDataSource.getCurrentRestaurant(restaurantId, restaurantList); }
    public LiveData<List<Restaurant>> getAllRestaurants() {
        return mapDataSource.getAllRestaurants();
    }
    public MutableLiveData<Boolean> getIsAlreadyNearbySearched() {
        return mapDataSource.getIsAlreadyNearbySearched();
    }
    public void setNumberOfFriendInterested(List<User> allInterestedUsers, List<Restaurant> restaurants) {
        mapDataSource.setNumberOfFriendInterested(allInterestedUsers, restaurants);
    }

    //                  --- RESTAURANT FAVORITE ---
    public MutableLiveData<RestaurantFavorite> getCurrentRestaurantFavorite(String currentRestaurantId) {
        return restaurantDataSource.getCurrentRestaurantFavorite(currentRestaurantId);
    }
    public void createRestaurantFavorite(RestaurantFavorite restaurantFavorite) {
        restaurantDataSource.createRestaurantFavorite(restaurantFavorite);
    }
    public void deleteRestaurantFavorite(RestaurantFavorite restaurantFavorite) {
        restaurantDataSource.deleteRestaurantFavorite(restaurantFavorite);
    }

    //                  --- PLACE TASK ---
    public void setPlaceTaskExecutor(String url) {
        placeTaskExecutor.execute(url);
    }
    public MutableLiveData<List<String>> getListOfPlaceId() {
        return placeTaskExecutor.getListOfPlaceId();
    }
}
