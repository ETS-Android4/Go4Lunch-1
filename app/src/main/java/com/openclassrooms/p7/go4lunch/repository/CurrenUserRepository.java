package com.openclassrooms.p7.go4lunch.repository;

import static android.content.ContentValues.TAG;

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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class CurrenUserRepository {

    private static volatile CurrenUserRepository INSTANCE;
    private static final String USERS_COLLECTION_NAME = "users";
    private static final String RESTAURANT_COLLECTION_NAME = "favorite_or_selected_restaurants";

    private CurrenUserRepository() { }

    public static CurrenUserRepository getInstance() {
        CurrenUserRepository result = INSTANCE;
        if (result != null) {
            return result;
        }
        synchronized (CurrenUserRepository.class) {
            if (INSTANCE == null) {
                INSTANCE = new CurrenUserRepository();
            }
            return INSTANCE;
        }
    }


    @Nullable
    public FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }
    public Task<Void> signOut(Context context) {
        return AuthUI.getInstance().signOut(context);
    }
    public Task<Void> deleteUser(Context context) {
        return AuthUI.getInstance().delete(context);
    }
    private CollectionReference getUsersCollection() { return FirebaseFirestore.getInstance().collection(USERS_COLLECTION_NAME); }
    private CollectionReference getFavoriteOrSelectedRestaurantCollection(String userId) {
        return FirebaseFirestore.getInstance().collection(USERS_COLLECTION_NAME).document(userId).collection(RESTAURANT_COLLECTION_NAME);
    }

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
            if (documentSnapshot.contains("likedOrSelectedRestaurant")) {
                this.getUsersCollection().document().update("userName", userToCreate.getUserName());
                this.getUsersCollection().document().update("photoUrl", userToCreate.getPhotoUrl());
                this.getUsersCollection().document().update("uid", userToCreate.getUid());
            } else {
                this.getUsersCollection().document(user.getUid()).set(userToCreate);
            }
        });
    }

    public Task<DocumentSnapshot> getUserData() {
        String uid = Objects.requireNonNull(this.getCurrentUser()).getUid();
        return this.getUsersCollection().document(uid).get();
    }

    public Task<QuerySnapshot> getUserDataList() {
            return this.getUsersCollection().get();
    }

    public void getUsersDataList() {
        ApiService apiService = DI.getRestaurantApiService();
        Objects.requireNonNull(this.getUserDataList()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                apiService.getUsers().clear();
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        User user = documentSnapshot.toObject(User.class);
                        for (int index = 0; index < user.getUserAndRestaurant().size(); index++) {
                            Log.d(TAG, "getUsersDataList: " + user.getUserAndRestaurant().size());
                        }
                        apiService.getUsers().add(user);
                    }
            }
        });
    }

    public void updateUser(String currentUserID, Map<String, UserAndRestaurant> likedOrSelectedRestaurant) {
        this.getUsersCollection().document(currentUserID).update("likedOrSelectedRestaurant", likedOrSelectedRestaurant);
    }

    public void deleteUserFromFirestore() {
        String uid = this.getCurrentUser().getUid();
        if (uid != null) {
            this.getUsersCollection().document(uid).delete();
        }
    }
}
