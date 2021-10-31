package com.openclassrooms.p7.go4lunch.service;

import com.openclassrooms.p7.go4lunch.R;
import com.openclassrooms.p7.go4lunch.model.Restaurant;

import java.util.HashMap;
import java.util.List;

/**
 * Created by lleotraas on 21.
 */
public class DummyRestaurantApiService implements RestaurantApiService {

    private final List<Restaurant> mRestaurantList = DummyRestaurant.generateRestaurant();
    private final HashMap<String, Boolean> mFavoriteRestaurantList = DummyRestaurant.generateFavoriteRestaurant();

    @Override
    public List<Restaurant> getRestaurant() {
        return mRestaurantList;
    }

    @Override
    public HashMap<String, Boolean> getFavoriteRestaurant() {
        return mFavoriteRestaurantList;
    }

    @Override
    public void addFavoriteRestaurant(String restaurantId, Boolean isFavorite) {
        mFavoriteRestaurantList.put(restaurantId, isFavorite);
    }

    @Override
    public void deleteFavoriteRestaurant(String restaurantId) {
        mFavoriteRestaurantList.remove(restaurantId);
    }

    @Override
    public void addRestaurant(Restaurant restaurant) {
        mRestaurantList.add(restaurant);
    }

    @Override
    public void removeRestaurant(Restaurant restaurant) {
        mRestaurantList.remove(restaurant);
    }

    @Override
    public int setRatingStars(int index, double rating) {
        int convertedRating = (int) rating;

            if (convertedRating == 2 && index == 1|| convertedRating == 4 && index == 2) {
              return R.drawable.baseline_star_half_black_18;
            }
            if (convertedRating < 4 && index == 2 || convertedRating < 2 && index == 1) {
               return R.drawable.baseline_star_border_black_18;
            }
        return R.drawable.baseline_star_rate_black_18;
    }
}
