package com.openclassrooms.p7.go4lunch.service;

import com.openclassrooms.p7.go4lunch.model.FavoriteRestaurant;
import com.openclassrooms.p7.go4lunch.model.Restaurant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lleotraas on 21.
 */
public abstract class DummyRestaurant {
    public static List<Restaurant> DUMMY_RESTAURANT = new ArrayList<>();
    public static List<FavoriteRestaurant> DUMMY_FAVORITE_RESTAURANT = new ArrayList<>();

    static List<Restaurant> generateRestaurant(){ return new ArrayList<>(DUMMY_RESTAURANT);}
    static List<FavoriteRestaurant> generateFavoriteRestaurant(){ return new ArrayList<>(DUMMY_FAVORITE_RESTAURANT); }
}
