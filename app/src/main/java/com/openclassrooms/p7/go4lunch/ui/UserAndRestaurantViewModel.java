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
import com.openclassrooms.p7.go4lunch.repository.GoogleMapsHelper;
import com.openclassrooms.p7.go4lunch.repository.MapViewRepository;
import com.openclassrooms.p7.go4lunch.repository.RestaurantDataRepository;
import com.openclassrooms.p7.go4lunch.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class UserAndRestaurantViewModel extends ViewModel {

    private final FirebaseHelper mFirebaseHelper;
//    private final UserRepository userDataSource;
    private final RestaurantDataRepository restaurantDataSource;
    private final MapViewRepository mapDataSource;

    private final MutableLiveData<List<User>> listOfUser = new MutableLiveData<>();
    private final MutableLiveData<List<User>> listOfUserInterested = new MutableLiveData<>();

    public UserAndRestaurantViewModel() {
        mFirebaseHelper = FirebaseHelper.getInstance();
//        userDataSource = UserRepository.getInstance();
        restaurantDataSource = RestaurantDataRepository.getInstance();
        mapDataSource = MapViewRepository.getInstance();
    }
    public void createUser(){
        FirebaseUser user = mFirebaseHelper.getCurrentUser();
        User userToCreate = new User(
                Objects.requireNonNull(user).getUid(),
                user.getDisplayName(),
                Objects.requireNonNull(user.getPhotoUrl()).toString(),
                "",
                "",
                false
        );
        mFirebaseHelper.getCurrentFirestoreUser().addOnCompleteListener(documentSnapshot -> {
            if (documentSnapshot.isSuccessful()) {
                if (!documentSnapshot.getResult().exists()) {
                    mFirebaseHelper.getUsersCollection().document(user.getUid()).set(userToCreate);
                } else {
                    Log.d(TAG, "createFireStoreUser: ça existe");
                }
            }
        }).addOnFailureListener(exception -> {

        });
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
    public MutableLiveData<List<User>> getAllUsers() {
        mFirebaseHelper.getAllUsers().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<User> users = new ArrayList<>();
                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                    users.add(documentSnapshot.toObject(User.class));
                }
                listOfUser.postValue(users);
            } else {
                Log.d("Error", "Error getting documents: ", task.getException());
            }
        }).addOnFailureListener(exception -> {

        });
        return listOfUser;
    }

    public LiveData<List<User>> getAllInterestedUsers() {
        mFirebaseHelper.getUsersCollection().whereEqualTo("restaurantSelected", true).addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.w(TAG, "Listen failed.", error);
                return;
            }
            if (value != null) {
                ArrayList<User> users = new ArrayList<>();
                for (DocumentSnapshot document : value.getDocuments()) {
                    users.add(document.toObject(User.class));
                }
                listOfUserInterested.postValue(users);
            } else {
                Log.d(TAG, "Current data: null");
            }
        });
        return listOfUserInterested;
    }

    public LiveData<List<User>> getAllInterestedUsersAtCurrentRestaurant(String restaurantId) {
        MutableLiveData<List<User>> listMutableLiveData = new MutableLiveData<>();
        ArrayList<User> users = new ArrayList<>();
        for (User user : Objects.requireNonNull(listOfUserInterested.getValue())) {
            if (user.getRestaurantId().equals(restaurantId) &&
                    !user.getUid().equals(Objects.requireNonNull(mFirebaseHelper.getCurrentUser()).getUid())) {
                users.add(user);
            }
        }
        listMutableLiveData.setValue(users);
        return listMutableLiveData;
    }

    public User getCurrentFirestoreUser(String userId) {
        User userFound = null;
        for (User user : listOfUser.getValue()) {
            if (user.getUid().equals(userId)) {
                userFound = user;
            }
        }
        return userFound;
    }

    public LiveData<List<Restaurant>> getAllRestaurants() {
        MutableLiveData<List<Restaurant>> restaurantMutableLiveData = new MutableLiveData<>();
        restaurantMutableLiveData.setValue(mapDataSource.getAllRestaurantList().getValue());
        return restaurantMutableLiveData;
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
