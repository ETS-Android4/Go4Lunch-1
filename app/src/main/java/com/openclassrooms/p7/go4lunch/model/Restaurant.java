package com.openclassrooms.p7.go4lunch.model;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;

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
    private final double rating;
    private final LatLng position;
    private Bitmap pictureUrl;

    public Restaurant(
            String id,
            String name,
            String adress,
            String openningHours,
            String phoneNumber,
            String uriWebsite,
            float distance,
            double rating,
            LatLng position,
            Bitmap pictureUrl
    ) {

        this.id = id;
        this.name = name;
        this.adress = adress;
        this.openningHours = openningHours;
        this.phoneNumber = phoneNumber;
        this.uriWebsite = uriWebsite;
        this.distance = distance;
        this.rating = rating;
        this.position = position;
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
    public double getRating() { return rating; }
    public LatLng getPosition() {
        return position;
    }
    public Bitmap getPictureUrl() { return pictureUrl; }

    // --- SETTERS ---
    public void setPictureUrl(Bitmap pictureUrl){ this.pictureUrl = pictureUrl;}
}
