package com.openclassrooms.p7.go4lunch.manager;

import com.openclassrooms.p7.go4lunch.repository.FavoriteOrSelectedRestaurantRepository;

public class FavoriteRestaurantManager {

    private static volatile FavoriteRestaurantManager INSTANCE;
    private FavoriteOrSelectedRestaurantRepository mFavoriteRestaurantRepository;

    private FavoriteRestaurantManager() {
        mFavoriteRestaurantRepository = FavoriteOrSelectedRestaurantRepository.getInstance();
    }

    public static FavoriteRestaurantManager getInstance() {
        FavoriteRestaurantManager result = INSTANCE;
        if (result != null){
            return result;
        }
        synchronized (FavoriteRestaurantManager.class) {
            if (INSTANCE == null) {
                INSTANCE = new FavoriteRestaurantManager();
            }
            return INSTANCE;
        }
    }



}
