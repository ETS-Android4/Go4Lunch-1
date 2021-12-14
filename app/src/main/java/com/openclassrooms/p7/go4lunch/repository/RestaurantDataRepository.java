package com.openclassrooms.p7.go4lunch.repository;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.openclassrooms.p7.go4lunch.model.RestaurantDataMap;
import com.openclassrooms.p7.go4lunch.model.UserAndRestaurant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RestaurantDataRepository {
    private final MutableLiveData<List<RestaurantDataMap>> listOfRestaurantData = new MutableLiveData<>();

    private static RestaurantDataRepository mRestaurantDataRepository;
    private static FirebaseHelper mFirebaseHelper;

    public static RestaurantDataRepository getInstance() {
        if (mRestaurantDataRepository == null) {
            mRestaurantDataRepository = new RestaurantDataRepository();
        }
        return mRestaurantDataRepository;
    }

    public RestaurantDataRepository() { mFirebaseHelper = FirebaseHelper.getInstance(); }

    public void createFireStoreRestaurant() {
        Map<String, UserAndRestaurant> userAndRestaurantMap = new HashMap<>();
        mFirebaseHelper.getRestaurantsCollection().document(Objects.requireNonNull(mFirebaseHelper.getCurrentUser()).getUid()).set(userAndRestaurantMap);
    }

    public void updateFireStoreRestaurant(UserAndRestaurant userAndRestaurant) {
        mFirebaseHelper.getRestaurantsCollection().document(Objects.requireNonNull(Objects.requireNonNull(mFirebaseHelper.getCurrentUser()).getUid())).update(userAndRestaurant.getRestaurantId(), userAndRestaurant);
    }

    public MutableLiveData<List<RestaurantDataMap>> getFirestoreRestaurantsDataList() {
        mFirebaseHelper.getRestaurantsDataCollection().addOnCompleteListener(task -> {
            ArrayList<RestaurantDataMap> userAndRestaurants = new ArrayList<>();
            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                userAndRestaurants.add(documentSnapshot.toObject(RestaurantDataMap.class));
            }
            listOfRestaurantData.postValue(userAndRestaurants);
        }).addOnFailureListener(exception -> {

        });
        return listOfRestaurantData;
    }
}
