package com.openclassrooms.p7.go4lunch.repository;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.openclassrooms.p7.go4lunch.model.User;

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

    public MutableLiveData<List<User>> getListOfUserInterested() {
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
//        List<User> userList = new ArrayList<>();
//        for (User user : Objects.requireNonNull(listOfUser.getValue())) {
//            if (user.isRestaurantSelected()) {
//                userList.add(user);
//            }
//        }
//        listOfUserInterested.postValue(userList);
        return listOfUserInterested;
    }

    public MutableLiveData<List<User>> getListOfUsersInterestedAtCurrentRestaurant(String restaurantId) {
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

//    public void updateNumberOfFriendInterested(String restaurantId) {
//        for (User u : Objects.requireNonNull(listOfUserInterested.getValue())) {
//            if (u.getRestaurantId().equals(restaurantId)) {
//                u.setNumberOfFriendInterested(u.getNumberOfFriendInterested()+1);
//            }
//        }
//    }

    /**
     * Update current user.
     * @param user user to update.
     */
    public void updateFirestoreUser(User user) {
        mFirebaseHelper.getUsersCollection().document(user.getUid()).update(
                "restaurantId", user.getRestaurantId(),
                "restaurantName", user.getRestaurantName(),
                "restaurantSelected", user.isRestaurantSelected()
        );
    }

    /**
     * Delete user from Firestore.
     */
    public void deleteFirestoreUser() {
        String uid = Objects.requireNonNull(mFirebaseHelper.getCurrentUser()).getUid();
        mFirebaseHelper.getUsersCollection().document(uid).collection("restaurants").document().delete();
        mFirebaseHelper.getUsersCollection().document(uid).delete();
    }

    public FirebaseUser getCurrentUser() {
        return mFirebaseHelper.getCurrentUser();
    }

    public Task<Void> deleteUser(Context context) {
        return mFirebaseHelper.deleteUser(context);
    }

    public Task<Void> signOut(Context context) {
        return mFirebaseHelper.signOut(context);
    }
}
