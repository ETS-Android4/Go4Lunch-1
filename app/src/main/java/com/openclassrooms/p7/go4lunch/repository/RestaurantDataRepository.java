package com.openclassrooms.p7.go4lunch.repository;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.openclassrooms.p7.go4lunch.model.User;
import com.openclassrooms.p7.go4lunch.model.UserAndRestaurant;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RestaurantDataRepository {
    private final MutableLiveData<Map<String, UserAndRestaurant>> restaurantDataMap = new MutableLiveData<>();
    private static RestaurantDataRepository mRestaurantDataRepository;
    private final FirebaseHelper mFirebaseHelper;

    public static RestaurantDataRepository getInstance() {
        if (mRestaurantDataRepository == null) {
            mRestaurantDataRepository = new RestaurantDataRepository();
        }
        return mRestaurantDataRepository;
    }

    public RestaurantDataRepository() {
        this.mFirebaseHelper = FirebaseHelper.getInstance();
    }

    public void createRestaurantData(UserAndRestaurant userAndRestaurant) {
        mFirebaseHelper.getRestaurantDataReference().document(userAndRestaurant.getRestaurantId()).set(userAndRestaurant);
    }

    public UserAndRestaurant getCurrentRestaurantData(String currentRestaurantId) {
        UserAndRestaurant userAndRestaurant = null;
        if (restaurantDataMap.getValue() != null) {
            for (Map.Entry<String, UserAndRestaurant> userAndRestaurantEntry : Objects.requireNonNull(restaurantDataMap.getValue()).entrySet()) {
                if (userAndRestaurantEntry.getValue().getRestaurantId().equals(currentRestaurantId)) {
                    userAndRestaurant = userAndRestaurantEntry.getValue();
                }
            }
        }
        return userAndRestaurant;
    }

    public MutableLiveData<Map<String, UserAndRestaurant>> getRestaurantData() {
        mFirebaseHelper.getRestaurantsDataCollection().addOnCompleteListener(task -> {
           if (task.isSuccessful()) {
               Map<String, UserAndRestaurant> userAndRestaurantMap = new HashMap<>();
               for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                   userAndRestaurantMap.put(documentSnapshot.toObject(UserAndRestaurant.class).getRestaurantId(), documentSnapshot.toObject(UserAndRestaurant.class));
               }
               restaurantDataMap.postValue(userAndRestaurantMap);
           } else {
               Log.d("Error", "Error getting documents: ", task.getException());
           }
        }).addOnFailureListener(exception -> {

        });
        return restaurantDataMap;
    }

    public void updateRestaurantData(UserAndRestaurant userAndRestaurant) {
        mFirebaseHelper.getRestaurantDataReference()
                .document(userAndRestaurant.getRestaurantId())
                .update("selected", userAndRestaurant.isSelected());
        mFirebaseHelper.getRestaurantDataReference()
                .document(userAndRestaurant.getRestaurantId())
                .update("favorite", userAndRestaurant.isFavorite());
        onDataChanged(userAndRestaurant.getRestaurantId());
    }

    public void deleteRestaurantData() {

    }

    private void onDataChanged(String restaurantId) {
        mFirebaseHelper.getRestaurantDataReference().document(restaurantId).addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w(TAG, "Listen failed.", error);
                    return;
                }
                if (value != null && value.exists()) {
                    Map<String, UserAndRestaurant> restaurantMap = new HashMap<>();
                    restaurantMap.put(value.getId(), value.toObject(UserAndRestaurant.class));
                    restaurantDataMap.postValue(restaurantMap);
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });
    }
}
