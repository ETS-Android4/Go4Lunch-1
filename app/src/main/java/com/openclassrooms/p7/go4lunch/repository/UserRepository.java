package com.openclassrooms.p7.go4lunch.repository;

import static android.content.ContentValues.TAG;

import static com.openclassrooms.p7.go4lunch.ui.DetailActivity.LIKE_BTN_TAG;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.openclassrooms.p7.go4lunch.injector.DI;
import com.openclassrooms.p7.go4lunch.model.UserAndRestaurant;
import com.openclassrooms.p7.go4lunch.model.User;
import com.openclassrooms.p7.go4lunch.service.ApiService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class UserRepository {

    private static volatile UserRepository INSTANCE;
    private static final String USERS_COLLECTION_NAME = "users";

    private UserRepository() { }

    public static UserRepository getInstance() {
        UserRepository result = INSTANCE;
        if (result != null) {
            return result;
        }
        synchronized (UserRepository.class) {
            if (INSTANCE == null) {
                INSTANCE = new UserRepository();
            }
            return INSTANCE;
        }
    }

    /**
     * Get current user from Firebase.
     * @return current User.
     */
    @Nullable
    public FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    /**
     * Call to signOut the current user.
     * @param context context of the activity.
     * @return a task to signOut.
     */
    public Task<Void> signOut(Context context) {
        return AuthUI.getInstance().signOut(context);
    }

    /**
     * Call to delete user account from Firestore.
     * @param context context of the activity.
     * @return a task to delete user.
     */
    public Task<Void> deleteUser(Context context) {
        return AuthUI.getInstance().delete(context);
    }

    /**
     * Call to get the collection where user is store in Firestore.
     * @return Collection reference.
     */
    private CollectionReference getUsersCollection() { return FirebaseFirestore.getInstance().collection(USERS_COLLECTION_NAME); }

    /**
     * Call a task to do a request to the collection where current user.
     * @return A query task.
     */
    private Task<DocumentSnapshot> getUserData() {
        String uid = Objects.requireNonNull(this.getCurrentUser()).getUid();
        return this.getUsersCollection().document(uid).get();
    }

    public Task<DocumentSnapshot> getUserData(String userId) {
        return this.getUsersCollection().document(userId).get();
    }

    /**
     * Call a task to do a request to get all users in Firestore.
     * @return A query task.
     */
    public Task<QuerySnapshot> getUserDataCollection() {
        return this.getUsersCollection().get();
    }

    /**
     * Create user in Firestore
     */
    public void createUser() {
        FirebaseUser user = getCurrentUser();
        HashMap<String, UserAndRestaurant> likedOrSelectedRestaurant = new HashMap<>();
        assert user != null;
        User userToCreate = new User(
                user.getUid(),
                user.getDisplayName(),
                Objects.requireNonNull(user.getPhotoUrl()).toString(),
                likedOrSelectedRestaurant
        );
        getUserData().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.contains("userAndRestaurant")) {
                this.getUsersCollection().document().update("userName", userToCreate.getUserName());
                this.getUsersCollection().document().update("photoUrl", userToCreate.getPhotoUrl());
                this.getUsersCollection().document().update("uid", userToCreate.getUid());
            } else {
                this.getUsersCollection().document(user.getUid()).set(userToCreate);
            }
        });
    }

    /**
     * Get userList from Firestore and store it in DUMMY_USER.
     */
    public void getUsersDataList() {
        ApiService apiService = DI.getRestaurantApiService();
        Objects.requireNonNull(this.getUserDataCollection()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                apiService.getUsers().clear();
                apiService.getUserAndRestaurant().clear();
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        User user = documentSnapshot.toObject(User.class);
                        apiService.addUser(user);
                        for (Map.Entry<String, UserAndRestaurant> mapEntry : user.getUserAndRestaurant().entrySet()) {
                            apiService.addUserAndRestaurant(mapEntry.getValue());
                        }
                    }
            }
        });
    }

    /**
     * Update current user Map. Fetch Stored Map , add new content to the previous map and update user Map to avoid erase previous content.
     * @param currentUserID Id of the current user.
     * @param likedOrSelectedRestaurant Map with the new content.
     */
    public void updateUser(String currentUserID, Map<String, UserAndRestaurant> likedOrSelectedRestaurant) {
               this.getUsersCollection().document(currentUserID).update("userAndRestaurant", likedOrSelectedRestaurant);
    }

    /**
     * Delete user from Firestore.
     */
    public void deleteUserFromFirestore() {
        String uid = this.getCurrentUser().getUid();
        if (uid != null) {
            this.getUsersCollection().document(uid).delete();
        }
    }
}
