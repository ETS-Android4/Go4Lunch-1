package com.openclassrooms.p7.go4lunch.service;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.LocalTime;
import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.openclassrooms.p7.go4lunch.model.Restaurant;
import com.openclassrooms.p7.go4lunch.model.User;

import java.util.Calendar;
import java.util.List;

/**
 * Created by lleotraas on 21.
 */
public interface ApiService {

    // --- MAKE ---
    String getCurrentDay(Calendar calendar);
    String formatUserFirstName(String userName);
    String formatRestaurantName(String restaurantName);
    String makeStringOpeningHours(OpeningHours openingHours, String currentDay, LocalTime currentTime);

    // --- GETTER ---
    String getOpeningHours(OpeningHours openingHours);
    String getWebsiteUri(Uri websiteUri);
    double getRating(Double rating);
    float getDistance(LatLng latLng, LatLng currentLocation);
    RectangularBounds getRectangularBound(LatLng currentLocation);

    // --- SET IMAGE OR ICON ---
    int setRatingStars(int index, double rating);
    int setFavoriteImage(boolean favorite);
    int setSelectedImage(boolean Selected);

    // --- ORDER ---
    void listViewComparator(List<Restaurant> restaurantList);
    void workmatesViewComparator(List<User> userList);

    String makeInterestedFriendsString(List<User> userList);
}
