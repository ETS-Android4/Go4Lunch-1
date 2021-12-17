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
import com.openclassrooms.p7.go4lunch.model.RestaurantData;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RestaurantDataRepository {
    private final MutableLiveData<Map<String, RestaurantData>> restaurantDataMap = new MutableLiveData<>();
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

    public void createRestaurantData(RestaurantData restaurantData) {
        mFirebaseHelper.getRestaurantDataReferenceForCurrentUser().document(restaurantData.getRestaurantId()).set(restaurantData);
    }

    public RestaurantData getCurrentRestaurantData(String currentRestaurantId) {
        RestaurantData restaurantData = null;
        if (restaurantDataMap.getValue() != null) {
            for (Map.Entry<String, RestaurantData> userAndRestaurantEntry : Objects.requireNonNull(restaurantDataMap.getValue()).entrySet()) {
                if (userAndRestaurantEntry.getValue().getRestaurantId().equals(currentRestaurantId)) {
                    restaurantData = userAndRestaurantEntry.getValue();
                }
            }
        }
        return restaurantData;
    }

    public MutableLiveData<Map<String, RestaurantData>> getRestaurantData() {
        mFirebaseHelper.getRestaurantsDataCollection().addOnCompleteListener(task -> {
           if (task.isSuccessful()) {
               Map<String, RestaurantData> userAndRestaurantMap = new HashMap<>();
               for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                   userAndRestaurantMap.put(documentSnapshot.toObject(RestaurantData.class).getRestaurantId(), documentSnapshot.toObject(RestaurantData.class));
               }
               restaurantDataMap.postValue(userAndRestaurantMap);
           } else {
               Log.d("Error", "Error getting documents: ", task.getException());
           }
        }).addOnFailureListener(exception -> {

        });
        return restaurantDataMap;
    }

    public void updateRestaurantData(RestaurantData restaurantData) {
        mFirebaseHelper.getRestaurantDataReferenceForCurrentUser()
                .document(restaurantData.getRestaurantId())
                .update("selected", restaurantData.isSelected());
        mFirebaseHelper.getRestaurantDataReferenceForCurrentUser()
                .document(restaurantData.getRestaurantId())
                .update("favorite", restaurantData.isFavorite());
        onDataChanged(restaurantData.getRestaurantId());
    }

    public void deleteRestaurantData() {

    }

    private void onDataChanged(String restaurantId) {
        mFirebaseHelper.getRestaurantDataReferenceForCurrentUser().document(restaurantId).addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w(TAG, "Listen failed.", error);
                    return;
                }
                if (value != null && value.exists()) {
                    Map<String, RestaurantData> restaurantMap = new HashMap<>();
                    restaurantMap.put(value.getId(), value.toObject(RestaurantData.class));
                    restaurantDataMap.postValue(restaurantMap);
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });
    }
}
