package com.openclassrooms.p7.go4lunch.service;

import com.openclassrooms.p7.go4lunch.model.Restaurant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lleotraas on 21.
 */
public abstract class DummyRestaurant {
    public static List<Restaurant> DUMMY_RESTAURANT = new ArrayList<>();
    public static HashMap<String,Boolean> DUMMY_FAVORITE_RESTAURANT = new HashMap<>();

    static List<Restaurant> generateRestaurant(){ return new ArrayList<>(DUMMY_RESTAURANT);}
    static HashMap<String,Boolean> generateFavoriteRestaurant(){ return new HashMap<>(DUMMY_FAVORITE_RESTAURANT); }
}
