package com.openclassrooms.p7.go4lunch.repository;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.openclassrooms.p7.go4lunch.model.User;
import com.openclassrooms.p7.go4lunch.model.UserAndRestaurant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class UserRepository {

    private MutableLiveData<User> currentUser;
    private final MutableLiveData<List<User>> listOfUser = new MutableLiveData<>();
    private final MutableLiveData<List<User>> listOfUserInterested = new MutableLiveData<>();

    private static UserRepository mUserRepository;
    private final FirebaseHelper mFirebaseHelper;

    public static UserRepository getInstance() {
        if (mUserRepository == null) {
            mUserRepository = new UserRepository();
        }
        return mUserRepository;
    }

    public UserRepository() {
        mFirebaseHelper = FirebaseHelper.getInstance();
    }

    /**
     * Create user in Firestore, if user already exist just update it.
     */
    public void createFireStoreUser() {
        FirebaseUser user = mFirebaseHelper.getCurrentUser();
        Map<String, UserAndRestaurant> restaurantDataMap = new HashMap<>();
        User userToCreate = new User(
                Objects.requireNonNull(user).getUid(),
                user.getDisplayName(),
                Objects.requireNonNull(user.getPhotoUrl()).toString(),
                restaurantDataMap
        );
        mFirebaseHelper.getUserData().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.contains("userAndRestaurant")) {
                mFirebaseHelper.getUsersCollection().document().update("userName", userToCreate.getUserName());
                mFirebaseHelper.getUsersCollection().document().update("photoUrl", userToCreate.getPhotoUrl());
            } else {
                mFirebaseHelper.getUsersCollection().document(user.getUid()).set(userToCreate);
            }
        });
    }

    /**
     * Get userList from Firestore and store it in DUMMY_USER.
     */
    public MutableLiveData<List<User>> getListOfUsers() {
        mFirebaseHelper.getUserDataCollection().addOnCompleteListener(task -> {
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

    public MutableLiveData<List<User>> getListOfUsersInterested() {
        mFirebaseHelper.getUsersCollection()

                .whereArrayContains("selected", true)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<User> users = new ArrayList<>();
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            users.add(documentSnapshot.toObject(User.class));
                        }
                        listOfUserInterested.postValue(users);
                    } else {
                        Log.e("Error","Error getting documents: ", task.getException());
                    }
        }).addOnFailureListener(exception -> {

        });
        return listOfUserInterested;
    }

    public User getFirestoreUser(String userId) {
        User userFound = null;
        for (User user : Objects.requireNonNull(listOfUser.getValue())) {
            if (user.getUid().equals(userId)) {
                userFound = user;
            }
        }
        return userFound;
    }

    public MutableLiveData<User> getCurrentUser() {
        if (currentUser == null) {
            currentUser = new MutableLiveData<>();
        }
        return currentUser;
    }

    /**
     * Update current user Map. Fetch Stored Map , add new content to the previous map and update user Map to avoid erase previous content.
     * @param currentUserID Id of the current user.
     * @param likedOrSelectedRestaurant Map with the new content.
     */
    public void updateFirestoreUser(String currentUserID, Map<String, UserAndRestaurant> likedOrSelectedRestaurant) {
        mFirebaseHelper.getUsersCollection().document(currentUserID).update("restaurantDataMap", likedOrSelectedRestaurant);
        onDataChanged(currentUserID);
    }

    /**
     * Delete user from Firestore.
     */
    public void deleteFirestoreUser() {
        String uid = Objects.requireNonNull(mFirebaseHelper.getCurrentUser()).getUid();
        mFirebaseHelper.getUsersCollection().document(uid).delete();
    }

    /**
     * Call when the user update the database for refresh listOfUser data.
     */
    private void onDataChanged(String userId) {
        mFirebaseHelper.getUsersCollection().document(userId).addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w(TAG, "Listen failed.", error);
                    return;
                }
                if (value != null && value.exists()) {
                    User user = value.toObject(User.class);
                    for (int index = 0; index < Objects.requireNonNull(listOfUser.getValue()).size(); index++) {
                        if (listOfUser.getValue().get(index).getUid().equals(userId)) {
                            listOfUser.getValue().set(index, user);
                        }
                    }
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });
    }
}
