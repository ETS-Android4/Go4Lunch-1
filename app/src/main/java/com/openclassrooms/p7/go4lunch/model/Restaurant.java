package com.openclassrooms.p7.go4lunch.model;

import android.graphics.Bitmap;

/**
 * Created by lleotraas on 21.
 */
public class Restaurant {

    private final String id;
    private final String name;
    private final String adress;
    private final String openningHours;
    private final String phoneNumber;
    private final String uriWebsite;
    private final float distance;
    private final double latitude;
    private final double longitude;
    private final int interestedFriends;
    private final double rating;
    private Bitmap pictureUrl;

    public Restaurant(
            String id,
            String name,
            String adress,
            String openningHours,
            String phoneNumber,
            String uriWebsite,
            float distance,
            double  latitude,
            double longitude,
            int interestedFriends,
            double rating,
            Bitmap pictureUrl
    ) {

        this.id = id;
        this.name = name;
        this.adress = adress;
        this.openningHours = openningHours;
        this.phoneNumber = phoneNumber;
        this.uriWebsite = uriWebsite;
        this.distance = distance;
        this.latitude = latitude;
        this.longitude = longitude;
        this.interestedFriends = interestedFriends;
        this.rating = rating;
        this.pictureUrl = pictureUrl;
    }

    // --- GETTERS ---
    public String getId() { return id; }
    public String getName() { return name; }
    public String getAdress() { return adress; }
    public String getOpenningHours() { return openningHours; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getUriWebsite() { return uriWebsite; }
    public float getDistance() { return distance; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public int getInterestedFriends() { return interestedFriends; }
    public double getRating() { return rating; }
    public Bitmap getPictureUrl() { return pictureUrl; }

    // --- SETTERS ---
    public void setPictureUrl(Bitmap pictureUrl){ this.pictureUrl = pictureUrl;}
}
