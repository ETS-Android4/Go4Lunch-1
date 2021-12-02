package com.openclassrooms.p7.go4lunch.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.openclassrooms.p7.go4lunch.model.UserAndRestaurant;
import com.openclassrooms.p7.go4lunch.model.Restaurant;
import com.openclassrooms.p7.go4lunch.model.User;

import java.util.List;
import java.util.Map;

/**
 * Created by lleotraas on 21.
 */
public interface ApiService {

    // --- GET LIST ---
    List<Restaurant> getRestaurant();
    List<Restaurant> getSearchedRestaurant();
    List<UserAndRestaurant> getUserAndRestaurant();
    List<User> getUsers();

    // --- ADD TO LIST ---
    void addUserAndRestaurant(UserAndRestaurant userAndRestaurant);
    void addRestaurant(Restaurant restaurant);
    void addUser(User user);
    void deleteUser(User user);

    Map<String, UserAndRestaurant> makeUserAndRestaurantMap(String currentUserId);
    UserAndRestaurant searchUserAndRestaurantById(String userId, String RestaurantId);
    String makeStringOpeningHours(OpeningHours openingHours, Context context);

    Restaurant searchCurrentRestaurantById(String id);
    User searchUserById(String uid);
    void searchSelectedUserAndRestaurantToDeselect(String currentUserId, String currentRestaurantId);
    void likeOrSelectRestaurant(String currentUserId, String currentRestaurantId, int buttonId);
    UserAndRestaurant searchSelectedRestaurant(User user);

    List<User> getUsersInterestedAtCurrentRestaurant(String currentUserId, Restaurant currentRestaurantId);
    List<User> getUsersInterestedAtCurrentRestaurant(String uid);
    UserAndRestaurant getCurrentuserSelectedRestaurant(User currentUserId);
    void filterUsersInterestedAtCurrentRestaurant();

    int setRatingStars(int index, double rating);
    int setFavoriteImage(boolean favorite);
    int setSelectedImage(boolean Selected);
    int setMarkerIcon(String placeId, boolean isSearched, LatLng restaurantPosition, GoogleMap mMap);

    RectangularBounds getRectangularBound(LatLng currentLocation);
    void updateMarkerOnMap(boolean isSearched, GoogleMap map);
    void setMarkerOnMap(Restaurant restaurant, GoogleMap map, boolean isSearched);
    String getOpeningHours(OpeningHours openingHours, Context context);
    float getDistance(LatLng latLng, LatLng currentLocation);
    String getWebsiteUri(Uri websiteUri);
    double getRating(Double rating);

    Restaurant createRestaurant(Place place, Bitmap placeImage, Context context);

    void listViewComparator();

    String makeUserFirstName(String userName);

    String makeInterestedFriendsString(List<User> interestedFriendList);
    String removeRestaurantWord(String restaurantName);
}
