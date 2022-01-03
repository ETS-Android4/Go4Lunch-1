package com.openclassrooms.p7.go4lunch.repository;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentSnapshot;
import com.openclassrooms.p7.go4lunch.model.RestaurantFavorite;

public class RestaurantFavoriteRepository {
    private static RestaurantFavoriteRepository mRestaurantFavoriteRepository;
    private final FirebaseHelper mFirebaseHelper;
    private final MutableLiveData<RestaurantFavorite> currentRestaurantFavorite = new MutableLiveData<>();

    public static RestaurantFavoriteRepository getInstance() {
        if (mRestaurantFavoriteRepository == null) {
            mRestaurantFavoriteRepository = new RestaurantFavoriteRepository();
        }
        return mRestaurantFavoriteRepository;
    }

    public RestaurantFavoriteRepository() {
        this.mFirebaseHelper = FirebaseHelper.getInstance();
    }

    public void createRestaurantFavorite(RestaurantFavorite restaurantFavorite) {
        mFirebaseHelper.getRestaurantDataReferenceForCurrentUser().document(restaurantFavorite.getRestaurantId()).set(restaurantFavorite);
    }

    public MutableLiveData<RestaurantFavorite> getCurrentRestaurantFavorite(String currentRestaurantId) {
        mFirebaseHelper.getRestaurantDataReferenceForCurrentUser().document(currentRestaurantId).get().addOnCompleteListener(task -> {
            DocumentSnapshot documentSnapshot = task.getResult();
            currentRestaurantFavorite.postValue(documentSnapshot.toObject(RestaurantFavorite.class));
        });
        return currentRestaurantFavorite;
    }

    public void deleteRestaurantFavorite(RestaurantFavorite restaurantFavorite) {
        mFirebaseHelper.getRestaurantDataReferenceForCurrentUser()
                .document(restaurantFavorite.getRestaurantId())
                .delete();
    }
}
