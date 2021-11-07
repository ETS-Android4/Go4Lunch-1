package com.openclassrooms.p7.go4lunch.repository;

import android.content.Context;

import androidx.annotation.Nullable;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.RuntimeExecutionException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.openclassrooms.p7.go4lunch.injector.DI;
import com.openclassrooms.p7.go4lunch.model.FavoriteOrSelectedRestaurant;
import com.openclassrooms.p7.go4lunch.model.User;
import com.openclassrooms.p7.go4lunch.service.RestaurantApiService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class CurrenUserRepository {

    private static volatile CurrenUserRepository INSTANCE;
    private static final String USERS_COLLECTION_NAME = "users";


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

    public void createUser() {
        FirebaseUser user = getCurrentUser();
        Map<String, Object> userToCreate = new HashMap<>();
        if (user != null) {
            userToCreate.put("photo_url", user.getPhotoUrl().toString());
            userToCreate.put("user_name", user.getDisplayName());
            userToCreate.put("user_id", user.getUid());
            userToCreate.put("favorite_or_selected_restaurants", Arrays.asList());
            Task<DocumentSnapshot> userData = getUserData();
            assert userData != null;
            userData.addOnSuccessListener(documentSnapshot -> {
                this.getUsersCollection().document(user.getUid()).set(userToCreate);
            });
        }
    }

    public Task<DocumentSnapshot> getUserData() {
        String uid = this.getCurrentUser().getUid();
        return this.getUsersCollection().document(uid).get();
    }

    public Task<QuerySnapshot> getUserDataList() {
            return this.getUsersCollection().get();
    }

    public void getUsersDataList() {
        RestaurantApiService apiService = DI.getRestaurantApiService();
        Objects.requireNonNull(this.getUserDataList()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                apiService.getUsers().clear();
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        String photoUrl = Objects.requireNonNull(documentSnapshot.get("photo_url")).toString();
                        String username = Objects.requireNonNull(documentSnapshot.get("user_name")).toString();
                        String uid = Objects.requireNonNull(documentSnapshot.get("user_id")).toString();
                        User user = new User(uid, username, photoUrl);
                        apiService.getUsers().add(user);
                    }
            }
        });
    }

    public void updateUser(String key, Object object) {
        String uid = this.getCurrentUser().getUid();
        if (uid != null) {
            this.getUsersCollection().document(uid).update(key, object);
        }
    }

    public void deleteUserFromFirestore() {
        String uid = this.getCurrentUser().getUid();
        if (uid != null) {
            this.getUsersCollection().document(uid).delete();
        }
    }
}
