package com.openclassrooms.p7.go4lunch.model;

import android.app.Activity;

/**
 * Created by lleotraas on 21.
 */
public class Restaurant {
    private String name;
    private String foodType;
    private String adress;
    private String openningHours;
    private int distance;
    private int interestedFriends;
    private double rating;
    private String pictureUrl;

    public Restaurant(String name, String foodType, String adress, String openningHours, int distance, int interestedFriends, double rating, String pictureUrl) {
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
    public String getPictureUrl() { return pictureUrl; }
}
