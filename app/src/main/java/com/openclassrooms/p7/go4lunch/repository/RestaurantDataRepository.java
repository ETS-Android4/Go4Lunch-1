package com.openclassrooms.p7.go4lunch.repository;

import androidx.lifecycle.MutableLiveData;

import com.openclassrooms.p7.go4lunch.model.UserAndRestaurant;

import java.util.Map;

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

    public void createRestaurantData() {
        mFirebaseHelper.getRestaurantDataReference().document().set(restaurantDataMap);
    }

    public void getRestaurantData() {

    }

    public void updateRestaurantData() {

    }

    public void deleteRestaurantData() {

    }
}
