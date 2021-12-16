package com.openclassrooms.p7.go4lunch.repository;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.openclassrooms.p7.go4lunch.model.User;
import com.openclassrooms.p7.go4lunch.model.UserAndRestaurant;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
                false
        );
        mFirebaseHelper.getUserData().addOnSuccessListener(documentSnapshot -> {
            mFirebaseHelper.getUsersCollection().document(user.getUid()).set(userToCreate);
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
                .whereEqualTo("restaurantIsSelected", true)
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

    /**
     * Update current user Map. Fetch Stored Map , add new content to the previous map and update user Map to avoid erase previous content.
     * @param user user to update.
     */
    public void updateFirestoreUser(User user) {
        mFirebaseHelper.getUsersCollection().document(user.getUid()).update("restaurantIsSelected", user.isRestaurantIsSelected());
        mFirebaseHelper.getUsersCollection().document(user.getUid()).update("restaurantId", user.getRestaurantId());

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
     * @param userId
     */
    public void onDataChangedToTrue(String userId) {
            mFirebaseHelper.onDataChangedToTrue().addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Log.w(TAG, "Listen failed.", error);
                        return;
                    }
                    if (value != null) {
                        UserAndRestaurant userAndRestaurant = null;
                        for (DocumentSnapshot document : value.getDocuments()) {
                            userAndRestaurant = document.toObject(UserAndRestaurant.class);
                        }
                        if (userAndRestaurant != null) {
                            ArrayList<User> users = new ArrayList<>();
                            for (User user : Objects.requireNonNull(listOfUser.getValue())) {
                                if (user.getUid().equals(userId)) {
                                    user.setRestaurantId(Objects.requireNonNull(userAndRestaurant).getRestaurantId());
                                    user.setRestaurantIsSelected(true);
                                    updateFirestoreUser(user);
                                    users.add(user);
                                }
                            }
                            listOfUser.postValue(users);
                        }
                    } else {
                        Log.d(TAG, "Current data: null");
                    }
                }
            });
    }

    public void onDataChangedToFalse(String userId) {
        mFirebaseHelper.onDataChangedToFalse().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w(TAG, "Listen failed.", error);
                    return;
                }
                if (value != null) {
                    ArrayList<User> users = new ArrayList<>();
                    for (User user : Objects.requireNonNull(listOfUser.getValue())) {
                        if (user.getUid().equals(userId)) {
                            user.setRestaurantId(null);
                            user.setRestaurantIsSelected(false);
                            updateFirestoreUser(user);
                            users.add(user);
                        }
                    }
                    listOfUser.postValue(users);
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });
    }

    public void onDataChangedUsersInterested() {
        //TODO add content method to refresh listOfUsersInterested.
    }
}
