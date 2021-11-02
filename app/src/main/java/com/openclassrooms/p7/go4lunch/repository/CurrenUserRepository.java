package com.openclassrooms.p7.go4lunch.repository;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.openclassrooms.p7.go4lunch.injector.DI;
import com.openclassrooms.p7.go4lunch.model.FavoriteRestaurant;
import com.openclassrooms.p7.go4lunch.model.Restaurant;
import com.openclassrooms.p7.go4lunch.model.User;
import com.openclassrooms.p7.go4lunch.service.RestaurantApiService;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class CurrenUserRepository {

    private static volatile CurrenUserRepository INSTANCE;
    private static final String USERS_COLLECTION_NAME = "users";
    private static final String FAVORITE_COLLECTION_NAME = "favorite";

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

    public String getCurrentUID() { return FirebaseAuth.getInstance().getUid(); }

    public DatabaseReference getDatabaseReference() { return FirebaseDatabase.getInstance().getReference(USERS_COLLECTION_NAME);}

    public Task<Void> signOut(Context context) {
        return AuthUI.getInstance().signOut(context);
    }

    public Task<Void> deleteUser(Context context) {
        return AuthUI.getInstance().delete(context);
    }

    private CollectionReference getUsersCollection() {
        return FirebaseFirestore.getInstance().collection(USERS_COLLECTION_NAME);
    }

    private CollectionReference getFavoriteCollection() {
        return FirebaseFirestore.getInstance().collection(FAVORITE_COLLECTION_NAME);
    }

    public void createUserInDatabase() {
        FirebaseUser user = getCurrentUser();
        if (user != null) {
            Uri uri;
            String urlPicture = "";
            if (user.getPhotoUrl() != null) {
                uri = user.getPhotoUrl();
                urlPicture = uri.toString();
            }
            String username = user.getDisplayName();
            String uid = user.getUid();
            User userToCreate = new User(uid, username, urlPicture);
            Task<DocumentSnapshot> userData = getUserData();
            assert userData != null;
            userData.addOnSuccessListener(documentSnapshot -> {
                this.getUsersCollection().document(uid).set(userToCreate);
            });
        }
    }

    public void createUserFavoriteRestaurantList(FirebaseUser user, Restaurant restaurant, boolean isFavorite, boolean isSelected) {
        String favoriteId = user.getUid() + restaurant.getId();
        String uid = user.getUid();
        String restaurantName = restaurant.getName();
        String restaurantId = restaurant.getId();
        FavoriteRestaurant favoriteRestaurantToCreate = new FavoriteRestaurant(uid, restaurantId, restaurantName, isFavorite, isSelected);
        this.getFavoriteCollection().document(favoriteId).set(favoriteRestaurantToCreate);
    }

    public Task<DocumentSnapshot> getUserData() {
        String uid = this.getCurrentUID();
        if (uid != null) {
            return this.getUsersCollection().document(uid).get();
        } else {
            return null;
        }
    }

    public void deleteUserFromFirestore() {
        String uid = this.getCurrentUID();
        if (uid != null) {
            this.getUsersCollection().document(uid).delete();
        }
    }

    public void deleteFavoriteRestaurant(String uid){
        DocumentReference documentReference = getFavoriteCollection().document(uid);
        Map<String, Object> updates = new HashMap<>();
        updates.put("restaurantId", FieldValue.delete());
        updates.put("uid", FieldValue.delete());
        updates.put("favorite", FieldValue.delete());
        updates.put("selected", FieldValue.delete());
        updates.put("restaurantName", FieldValue.delete());
        documentReference.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            }
        });
        getFavoriteCollection().document(uid).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                    }
                });
    }

    public void updateFavoriteRestaurant(String uid, boolean isFavorite) {
        DocumentReference documentReference = getFavoriteCollection().document(uid);
        documentReference.update("favorite", isFavorite).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
    }

    public void updateSelectedRestaurant(String uid, boolean isSelected) {
        DocumentReference documentReference = getFavoriteCollection().document(uid);
        documentReference.update("selected", isSelected);
    }
}
