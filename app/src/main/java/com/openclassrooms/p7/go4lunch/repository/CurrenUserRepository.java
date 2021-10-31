package com.openclassrooms.p7.go4lunch.repository;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.openclassrooms.p7.go4lunch.injector.DI;
import com.openclassrooms.p7.go4lunch.model.User;
import com.openclassrooms.p7.go4lunch.service.RestaurantApiService;

import java.util.List;

public final class CurrenUserRepository {

    private static volatile CurrenUserRepository INSTANCE;
    private static final String COLLECTION_NAME = "users";
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = mDatabase.getReference(COLLECTION_NAME);
    private RestaurantApiService mApiService;


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

    public DatabaseReference getDatabaseReference() { return FirebaseDatabase.getInstance().getReference(COLLECTION_NAME);}

    public Task<Void> signOut(Context context) {
        return AuthUI.getInstance().signOut(context);
    }

    public Task<Void> deleteUser(Context context) {
        return AuthUI.getInstance().delete(context);
    }

    private CollectionReference getUsersCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    public void createUserInDatabase(){
        FirebaseUser user = getCurrentUser();
        mApiService = DI.getRestaurantApiService();
        if (user != null) {
            Uri uri = null;
            String urlPicture = "";
            if (user.getPhotoUrl() != null) {
                uri = user.getPhotoUrl();
                urlPicture = uri.toString();
            }

            String username = user.getDisplayName();
            String uid = user.getUid();

            User userToCreate = new User(uid, username, mApiService.getFavoriteRestaurant(), urlPicture);

            Task<DocumentSnapshot> userData = getUserData();

            assert userData != null;
            userData.addOnSuccessListener(documentSnapshot -> {
//                if (documentSnapshot.contains("quelque chose")){
//                    userToCreate.setLeBoolean((Boolean) documentSnapshot.get("quelque chose"));
//                }
                this.getUsersCollection().document(uid).set(userToCreate);
            });
        }
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


}
