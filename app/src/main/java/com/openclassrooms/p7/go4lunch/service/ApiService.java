package com.openclassrooms.p7.go4lunch.service;

import android.content.Context;
import android.net.Uri;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.LocalTime;
import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.openclassrooms.p7.go4lunch.model.UserAndRestaurant;
import com.openclassrooms.p7.go4lunch.model.Restaurant;
import com.openclassrooms.p7.go4lunch.model.User;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Created by lleotraas on 21.
 */
public interface ApiService {

    // --- GET DUMMY LIST ---
    List<Restaurant> getRestaurant();
    List<Restaurant> getSearchedRestaurant();
    List<UserAndRestaurant> getUserAndRestaurant();
    List<User> getUsers();

    // --- ADD TO ---
    void addUserAndRestaurant(UserAndRestaurant userAndRestaurant);
    void addRestaurant(Restaurant restaurant);
    void addUser(User user);
    void addSearchedRestaurant(Restaurant restaurant);

    // --- DELETE ---
    void deleteUser(User user);

    // --- MAKE ---
    Map<String, UserAndRestaurant> makeUserAndRestaurantMap(String currentUserId);
    String makeStringOpeningHours(OpeningHours openingHours, String currentDay, LocalTime currentTime);
    String getCurrentDay(Calendar calendar);
    String makeUserFirstName(String userName);
    String makeInterestedFriendsString(List<User> interestedFriendList);
    String removeUselessWords(String restaurantName);

    // --- SEARCH ---
    User searchUserById(String uid);
    Restaurant searchCurrentRestaurantById(String id);
    UserAndRestaurant searchUserAndRestaurantById(String userId, String RestaurantId);
    void searchSelectedUserAndRestaurantToDeselect(String currentUserId, String currentRestaurantId);
    void likeOrSelectRestaurant(String currentUserId, String currentRestaurantId, int buttonId);
    UserAndRestaurant searchSelectedRestaurant(User user);

    // --- GETTERS ---
    List<User> getUsersInterestedAtCurrentRestaurants(String currentUserId, Restaurant currentRestaurant);
    List<User> getUsersInterestedAtCurrentRestaurantForNotification(String currentUserId, String restaurantId);
    UserAndRestaurant getCurrentUserSelectedRestaurant(User currentUser);
    String getOpeningHours(OpeningHours openingHours);
    String getWebsiteUri(Uri websiteUri);
    double getRating(Double rating);
    float getDistance(LatLng latLng, LatLng currentLocation);
    RectangularBounds getRectangularBound(LatLng currentLocation);

    // --- SET IMAGE OR ICON ---
    int setRatingStars(int index, double rating);
    int setFavoriteImage(boolean favorite);
    int setSelectedImage(boolean Selected);
    int setMarkerIcon(String placeId, boolean isSearched);

    // --- MAP MARKER ---
    void updateMarkerOnMap(boolean isSearched, GoogleMap map);
    void setMarkerOnMap(Restaurant restaurant, GoogleMap map, boolean isSearched);

    // --- ORDER ---
    void listViewComparator();
    void filterUsersInterestedAtCurrentRestaurant();
}
