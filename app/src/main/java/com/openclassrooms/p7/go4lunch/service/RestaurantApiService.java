package com.openclassrooms.p7.go4lunch.service;

import android.graphics.Bitmap;
import android.net.Uri;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
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
    String makeStringOpeningHours(OpeningHours openingHours);

    Restaurant searchRestaurantById(String id);
    User searchUserById(String uid);
    FavoriteOrSelectedRestaurant searchFavoriteRestaurantById(String currentUserId, String favoriteRestaurantId);
    void searchSelectedRestaurantToDeselect(String currentUserId, String currentRestaurantId);
    void selectRestaurant(String currentUserId, String currentRestaurantId, int buttonId);
    FavoriteOrSelectedRestaurant searchSelectedRestaurant(User user);

    List<User> getUsersInterestedAtCurrentRestaurant(String currentUserId, Restaurant mCurrentRestaurant);

    int setRatingStars(int index, double rating);
    int setFavoriteImage(boolean favorite);
    int setSelectedImage(boolean Selected);
    int setMarker(String placeId, boolean isSearched, LatLng restaurantPosition, GoogleMap mMap);

    RectangularBounds getRectangularBound(LatLng currentLocation);
    void setInfoOnMarker(boolean isSearched, GoogleMap map);
    void setInfoOnMarker(Restaurant restaurant, boolean isSearched, GoogleMap map);
    String getOpeningHours(OpeningHours openingHours);
    float getDistance(LatLng latLng, LatLng currentLocation);
    String getWebsiteUri(Uri websiteUri);
    double getRating(Double rating);

    Restaurant createRestaurant(Place place, Bitmap placeImage);
}
