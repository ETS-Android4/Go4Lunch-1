package com.openclassrooms.p7.go4lunch.service;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.material.tabs.TabLayout;
import com.openclassrooms.p7.go4lunch.model.UserAndRestaurant;
import com.openclassrooms.p7.go4lunch.model.Restaurant;
import com.openclassrooms.p7.go4lunch.model.User;
import com.openclassrooms.p7.go4lunch.ui.MainActivity;

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

    // --- DELETE ---
    void deleteUser(User user);

    // --- CREATE ---
    Restaurant createRestaurant(Place place, Bitmap placeImage, Context context);

    // --- MAKE ---
    Map<String, UserAndRestaurant> makeUserAndRestaurantMap(String currentUserId);
    UserAndRestaurant searchUserAndRestaurantById(String userId, String RestaurantId);
    String makeStringOpeningHours(OpeningHours openingHours, Context context);
    String makeUserFirstName(String userName);
    String makeInterestedFriendsString(List<User> interestedFriendList);
    String removeUselessWords(String restaurantName);

    // --- SEARCH ---
    Restaurant searchCurrentRestaurantById(String id);
    User searchUserById(String uid);
    void searchSelectedUserAndRestaurantToDeselect(String currentUserId, String currentRestaurantId);
    void likeOrSelectRestaurant(String currentUserId, String currentRestaurantId, int buttonId);
    UserAndRestaurant searchSelectedRestaurant(User user);

    // --- GETTERS ---
    List<User> getUsersInterestedAtCurrentRestaurant(String currentUserId, Restaurant currentRestaurantId);
    UserAndRestaurant getCurrentUserSelectedRestaurant(User currentUserId);
    String getOpeningHours(OpeningHours openingHours, Context context);
    String getWebsiteUri(Uri websiteUri);
    double getRating(Double rating);
    float getDistance(LatLng latLng, LatLng currentLocation);
    RectangularBounds getRectangularBound(LatLng currentLocation);

    // --- SET IMAGE OR ICON ---
    int setRatingStars(int index, double rating);
    int setFavoriteImage(boolean favorite);
    int setSelectedImage(boolean Selected);
    int setMarkerIcon(String placeId, boolean isSearched, LatLng restaurantPosition, GoogleMap mMap);

    // --- MAP MARKER ---
    void updateMarkerOnMap(boolean isSearched, GoogleMap map);
    void setMarkerOnMap(Restaurant restaurant, GoogleMap map, boolean isSearched);

    // --- ORDER ---
    void listViewComparator();
    void filterUsersInterestedAtCurrentRestaurant();

    // --- THEME ---
    void setTheme(Activity activity);
    void setMapTheme(FragmentActivity fragmentActivity, GoogleMap mMap);
    void setTabColor(TabLayout activityMainTabs, Activity application);
    void setSelectedTabColor(TabLayout.Tab tab, MainActivity activity);
    void setUnselectedTabColor(TabLayout.Tab tab, MainActivity activity);
    void setToolbarColor(Toolbar toolbar, View childAt, MainActivity activity);
}
