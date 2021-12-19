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
import com.openclassrooms.p7.go4lunch.model.RestaurantFavorite;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RestaurantDataRepository {
    private final MutableLiveData<Map<String, RestaurantFavorite>> restaurantDataMap = new MutableLiveData<>();
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

    public void createRestaurantData(RestaurantFavorite restaurantFavorite) {
        mFirebaseHelper.getRestaurantDataReferenceForCurrentUser().document(restaurantFavorite.getRestaurantId()).set(restaurantFavorite);
    }

    public RestaurantFavorite getCurrentRestaurantData(String currentRestaurantId) {
        RestaurantFavorite restaurantFavorite = null;
        if (restaurantDataMap.getValue() != null) {
            for (Map.Entry<String, RestaurantFavorite> userAndRestaurantEntry : Objects.requireNonNull(restaurantDataMap.getValue()).entrySet()) {
                if (userAndRestaurantEntry.getValue().getRestaurantId().equals(currentRestaurantId)) {
                    restaurantFavorite = userAndRestaurantEntry.getValue();
                }
            }
        }
        return restaurantFavorite;
    }

    public MutableLiveData<Map<String, RestaurantFavorite>> getRestaurantData() {
        mFirebaseHelper.getRestaurantsDataCollection().addOnCompleteListener(task -> {
           if (task.isSuccessful()) {
               Map<String, RestaurantFavorite> restaurantFavoriteMap = new HashMap<>();
               for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                   restaurantFavoriteMap.put(documentSnapshot.toObject(RestaurantFavorite.class).getRestaurantId(), documentSnapshot.toObject(RestaurantFavorite.class));
               }
               restaurantDataMap.postValue(restaurantFavoriteMap);
           } else {
               Log.d("Error", "Error getting documents: ", task.getException());
           }
        }).addOnFailureListener(exception -> {

        });
        return restaurantDataMap;
    }

    public void updateRestaurantData(RestaurantFavorite restaurantFavorite) {
        mFirebaseHelper.getRestaurantDataReferenceForCurrentUser()
                .document(restaurantFavorite.getRestaurantId())
                .update("favorite", restaurantFavorite.isFavorite());
//        onDataChanged(restaurantFavorite.getRestaurantId());
    }

    public void deleteRestaurantData(RestaurantFavorite restaurantFavorite) {
        mFirebaseHelper.getRestaurantDataReferenceForCurrentUser()
                .document(restaurantFavorite.getRestaurantId())
                .delete();
    }

    private void onDataChanged(String restaurantId) {
        mFirebaseHelper.getRestaurantDataReferenceForCurrentUser().document(restaurantId).addSnapshotListener(MetadataChanges.INCLUDE, (value, error) -> {
            if (error != null) {
                Log.w(TAG, "Listen failed.", error);
                return;
            }
            if (value != null && value.exists()) {
                Map<String, RestaurantFavorite> restaurantMap = new HashMap<>();
                restaurantMap.put(value.getId(), value.toObject(RestaurantFavorite.class));
                restaurantDataMap.postValue(restaurantMap);
            } else {
                Log.d(TAG, "Current data: null");
            }
        });
    }
}
