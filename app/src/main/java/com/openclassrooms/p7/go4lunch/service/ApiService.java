package com.openclassrooms.p7.go4lunch.service;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;
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
    String getCurrentDay(Calendar calendar);
    String makeUserFirstName(String userName);
    String makeInterestedFriendsString(List<User> interestedFriendList);
    String removeUselessWords(String restaurantName);

    // --- SEARCH ---
    User searchUserById(String uid);
    Restaurant searchCurrentRestaurantById(String id);
//    Restaurant searchUserAndRestaurantById(String RestaurantId);
    UserAndRestaurant searchSelectedRestaurant(Map<String, UserAndRestaurant> userAndRestaurant);
//    void likeOrSelectRestaurant(String currentUserId, String currentRestaurantId, int buttonId);
//    UserAndRestaurant searchSelectedRestaurant(User user);

    // --- GETTERS ---
//    List<User> getUsersInterestedAtCurrentRestaurants(String currentUserId, Restaurant currentRestaurant);
//    List<User> getUsersInterestedAtCurrentRestaurantForNotification(String currentUserId, String restaurantId);
//    UserAndRestaurant getCurrentUserSelectedRestaurant(User currentUser);
    String getWebsiteUri(Uri websiteUri);
    double getRating(Double rating);
    float getDistance(LatLng latLng, LatLng currentLocation);
    RectangularBounds getRectangularBound(LatLng currentLocation);

    // --- SET IMAGE OR ICON ---
    int setRatingStars(int index, double rating);
    int setFavoriteImage(boolean favorite);
    int setSelectedImage(boolean Selected);

    // --- ORDER ---
    void listViewComparator();
//    void filterUsersInterestedAtCurrentRestaurant();
}
