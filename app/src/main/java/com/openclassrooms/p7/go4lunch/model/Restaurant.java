package com.openclassrooms.p7.go4lunch.model;

import android.app.Activity;
import android.graphics.Bitmap;

/**
 * Created by lleotraas on 21.
 */
public class Restaurant {
    private final String name;
    private final String foodType;
    private final String adress;
    private final String openningHours;
    private final int distance;
    private final int interestedFriends;
    private final double rating;
    private final Bitmap pictureUrl;

    public Restaurant(String name, String foodType, String adress, String openningHours, int distance, int interestedFriends, double rating, Bitmap pictureUrl) {
        this.name = name;
        this.foodType = foodType;
        this.adress = adress;
        this.openningHours = openningHours;
        this.distance = distance;
        this.interestedFriends = interestedFriends;
        this.rating = rating;
        this.pictureUrl = pictureUrl;
    }

    // --- GETTERS ---
    public String getName() { return name; }
    public String getFoodType() { return foodType; }
    public String getAdress() { return adress; }
    public String getOpenningHours() { return openningHours; }
    public int getDistance() { return distance; }
    public int getInterestedFriends() { return interestedFriends; }
    public double getRating() { return rating; }
    public Bitmap getPictureUrl() { return pictureUrl; }
}
