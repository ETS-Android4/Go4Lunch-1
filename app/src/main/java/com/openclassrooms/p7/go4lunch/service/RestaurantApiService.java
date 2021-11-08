package com.openclassrooms.p7.go4lunch.service;

import com.openclassrooms.p7.go4lunch.model.FavoriteOrSelectedRestaurant;
import com.openclassrooms.p7.go4lunch.model.Restaurant;
import com.openclassrooms.p7.go4lunch.model.User;

import java.util.List;
import java.util.Map;

/**
 * Created by lleotraas on 21.
 */
public interface RestaurantApiService {

    List<Restaurant> getRestaurant();
    List<FavoriteOrSelectedRestaurant> getFavoriteRestaurant();
    List<User> getUsers();

    void addFavoriteRestaurant(FavoriteOrSelectedRestaurant favoriteOrSelectedRestaurant);
    void deleteFavoriteRestaurant(FavoriteOrSelectedRestaurant favoriteOrSelectedRestaurant);

    void addRestaurant(Restaurant restaurant);
    void removeRestaurant(Restaurant restaurant);

    void addUser(User user);
    void deleteUser(User user);

    Map<String, FavoriteOrSelectedRestaurant> makeLikedOrSelectedRestaurantMap(String currentUserId);
    void makeLikedOrSelectedRestaurantList(Restaurant restaurant);

    Restaurant searchRestaurantById(String id);
    User searchUserById(String uid);
    FavoriteOrSelectedRestaurant searchFavoriteRestaurantById(String currentUserId, String favoriteRestaurantId);
    FavoriteOrSelectedRestaurant searchSelectedRestaurantToDeselect(String currentUserId, String currentRestaurantId);
    FavoriteOrSelectedRestaurant searchSelectedRestaurant(User user);

    List<User> getUsersInterestedAtCurrentRestaurant(String currentUserId, Restaurant mCurrentRestaurant);

    int setRatingStars(int index, double rating);
    int setFavoriteImage(boolean favorite);
    int setSelectedImage(boolean Selected);
    int setMarker(String placeId);
}
