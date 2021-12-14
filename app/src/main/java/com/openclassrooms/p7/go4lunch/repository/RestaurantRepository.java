package com.openclassrooms.p7.go4lunch.repository;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.openclassrooms.p7.go4lunch.model.UserAndRestaurant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RestaurantRepository {
    private final MutableLiveData<List<UserAndRestaurant>> listOfRestaurant = new MutableLiveData<>();

    private static RestaurantRepository mRestaurantRepository;
    private static FirebaseHelper mFirebaseHelper;

    public static RestaurantRepository getInstance() {
        if (mRestaurantRepository == null) {
            mRestaurantRepository = new RestaurantRepository();
        }
        return mRestaurantRepository;
    }

    public RestaurantRepository() { mFirebaseHelper = FirebaseHelper.getInstance(); }

    public void createFireStoreRestaurant() {
        Map<String, UserAndRestaurant> userAndRestaurantMap = new HashMap<>();
        mFirebaseHelper.getRestaurantsCollection().document(Objects.requireNonNull(mFirebaseHelper.getCurrentUser()).getUid()).set(userAndRestaurantMap);
    }

    public void updateFireStoreRestaurant(UserAndRestaurant userAndRestaurant) {
        mFirebaseHelper.getRestaurantsCollection().document(Objects.requireNonNull(mFirebaseHelper.getCurrentUser().getUid())).update(userAndRestaurant.getRestaurantId(), userAndRestaurant);
    }

    public MutableLiveData<List<UserAndRestaurant>> getFirestoreRestaurantsList() {
        mFirebaseHelper.getRestaurantsDataCollection().addOnCompleteListener(task -> {
            ArrayList<UserAndRestaurant> userAndRestaurants = new ArrayList<>();
            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                userAndRestaurants.add(documentSnapshot.toObject(UserAndRestaurant.class));
            }
            listOfRestaurant.postValue(userAndRestaurants);
        }).addOnFailureListener(exception -> {

        });
        return listOfRestaurant;
    }
}
