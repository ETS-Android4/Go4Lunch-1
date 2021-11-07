package com.openclassrooms.p7.go4lunch.service;

import com.openclassrooms.p7.go4lunch.model.FavoriteOrSelectedRestaurant;
import com.openclassrooms.p7.go4lunch.model.Restaurant;
import com.openclassrooms.p7.go4lunch.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lleotraas on 21.
 */
public abstract class DummyRestaurant {
    public static List<Restaurant> DUMMY_RESTAURANT = new ArrayList<>();
    public static List<FavoriteOrSelectedRestaurant> DUMMY_FAVORITE_RESTAURANT = new ArrayList<>();
    public static List<User> DUMMY_FAVORITE_USER = new ArrayList<>();

    static List<Restaurant> generateRestaurant(){ return new ArrayList<>(DUMMY_RESTAURANT);}
    static List<FavoriteOrSelectedRestaurant> generateFavoriteRestaurant(){ return new ArrayList<>(DUMMY_FAVORITE_RESTAURANT); }
    static List<User> generateUsers() { return new ArrayList<>(DUMMY_FAVORITE_USER); }
}
