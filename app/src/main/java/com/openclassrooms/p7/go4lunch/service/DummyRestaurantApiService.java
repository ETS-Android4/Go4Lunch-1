package com.openclassrooms.p7.go4lunch.service;

import android.view.View;

import com.openclassrooms.p7.go4lunch.R;
import com.openclassrooms.p7.go4lunch.model.FavoriteRestaurant;
import com.openclassrooms.p7.go4lunch.model.Restaurant;

import java.util.HashMap;
import java.util.List;

/**
 * Created by lleotraas on 21.
 */
public class DummyRestaurantApiService implements RestaurantApiService {

    private final List<Restaurant> mRestaurantList = DummyRestaurant.generateRestaurant();
    private final List<FavoriteRestaurant> mFavoriteRestaurantList = DummyRestaurant.generateFavoriteRestaurant();

    @Override
    public List<Restaurant> getRestaurant() {
        return mRestaurantList;
    }

    @Override
    public List<FavoriteRestaurant> getFavoriteRestaurant() {
        return mFavoriteRestaurantList;
    }

    @Override
    public void addFavoriteRestaurant(FavoriteRestaurant favoriteRestaurant) {
        mFavoriteRestaurantList.add(favoriteRestaurant);
    }

    @Override
    public void deleteFavoriteRestaurant(FavoriteRestaurant favoriteRestaurant) {
        mFavoriteRestaurantList.remove(favoriteRestaurant);
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

    @Override
    public int setFavoriteImage(boolean favoriteOrSelected) {
            if (favoriteOrSelected) {
                return R.drawable.baseline_star_rate_black_24;
            }
            return R.drawable.baseline_star_border_24;
    }

    @Override
    public int setSelectedImage(boolean selected) {
        if (selected) {
            return R.drawable.baseline_check_circle_black_24;
        }
        return R.drawable.baseline_check_circle_outline_24;

    }
}
