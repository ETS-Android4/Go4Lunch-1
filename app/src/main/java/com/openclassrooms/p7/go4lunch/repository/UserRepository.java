package com.openclassrooms.p7.go4lunch.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.openclassrooms.p7.go4lunch.model.User;
import com.openclassrooms.p7.go4lunch.model.UserAndRestaurant;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class UserRepository {

    private final MutableLiveData<List<User>> listOfUser = new MutableLiveData<>();

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
                null
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
    public MutableLiveData<List<User>> getFirestoreUsersDataList() {
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
//        ApiService apiService = DI.getRestaurantApiService();
//        Objects.requireNonNull(this.getUserDataCollection()).addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                apiService.getUsers().clear();
//                apiService.getUserAndRestaurant().clear();
//                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
//                        User user = documentSnapshot.toObject(User.class);
//                        apiService.addUser(user);
//                        if (user.getUserAndRestaurant() != null) {
//                            for (Map.Entry<String, UserAndRestaurant> mapEntry : user.getUserAndRestaurant().entrySet()) {
//                                apiService.addUserAndRestaurant(mapEntry.getValue());
//                            }
//                        }
//                    }
//            }
//        });
    }

    /**
     * Update current user Map. Fetch Stored Map , add new content to the previous map and update user Map to avoid erase previous content.
     * @param currentUserID Id of the current user.
     * @param likedOrSelectedRestaurant Map with the new content.
     */
    public void updateFirestoreUser(String currentUserID, Map<String, UserAndRestaurant> likedOrSelectedRestaurant) {
               mFirebaseHelper.getUsersCollection().document(currentUserID).update("userAndRestaurant", likedOrSelectedRestaurant);
    }

    /**
     * Delete user from Firestore.
     */
    public void deleteFirestoreUser() {
        String uid = Objects.requireNonNull(mFirebaseHelper.getCurrentUser()).getUid();
        mFirebaseHelper.getUsersCollection().document(uid).delete();
    }
}
