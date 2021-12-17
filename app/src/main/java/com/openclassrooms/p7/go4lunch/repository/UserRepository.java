package com.openclassrooms.p7.go4lunch.repository;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.openclassrooms.p7.go4lunch.model.User;
import com.openclassrooms.p7.go4lunch.model.RestaurantData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserRepository {

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
        User userToCreate = new User(
                Objects.requireNonNull(user).getUid(),
                user.getDisplayName(),
                Objects.requireNonNull(user.getPhotoUrl()).toString(),
                null,
                null,
                false
        );
        if (getFirestoreUser(mFirebaseHelper.getCurrentUser().getUid()) == null) {
            mFirebaseHelper.getCurrentUserData().addOnSuccessListener(documentSnapshot -> {
                mFirebaseHelper.getUsersCollection().document(user.getUid()).set(userToCreate);
            });
        }
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

    /**
     * Get userList from Firestore and store it in DUMMY_USER.
     */
    public MutableLiveData<List<User>> getListOfUsers() {
        mFirebaseHelper.getUsersCollection().get().addOnCompleteListener(task -> {
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

    public MutableLiveData<List<User>> getListOfUsersInterested(String restaurantId) {
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

    /**
     * Update current user Map. Fetch Stored Map , add new content to the previous map and update user Map to avoid erase previous content.
     * @param user user to update.
     */
    public void updateFirestoreUser(User user) {
        mFirebaseHelper.getUsersCollection().document(user.getUid()).update("restaurantIsSelected", user.isRestaurantIsSelected());
        mFirebaseHelper.getUsersCollection().document(user.getUid()).update("restaurantId", user.getRestaurantId());
        mFirebaseHelper.getUsersCollection().document(user.getUid()).update("restaurantName", user.getRestaurantName());
        onDataChangedUsersInterested();
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
     * @param restaurantName
     * @param restaurantId
     */
    public void onDataChangedToTrue(String restaurantName, String restaurantId, Boolean restaurantIsSelected) {
        String userId = Objects.requireNonNull(mFirebaseHelper.getCurrentUser()).getUid();
        ArrayList<User> users = new ArrayList<>();
        for (User user : Objects.requireNonNull(listOfUser.getValue())) {
            if (user.getUid().equals(userId)) {
                if (restaurantIsSelected) {
                    user.setRestaurantName(restaurantName);
                    user.setRestaurantId(restaurantId);
                } else {
                    user.setRestaurantName(null);
                    user.setRestaurantId(null);
                }
                user.setRestaurantIsSelected(restaurantIsSelected);
                updateFirestoreUser(user);
                users.add(user);
            }
        }
        listOfUser.postValue(users);
    }

    public void onDataChangedUsersInterested() {
        mFirebaseHelper.getUsersCollection().whereEqualTo("restaurantIsSelected", true).addSnapshotListener((value, error) -> {
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
    }
}
