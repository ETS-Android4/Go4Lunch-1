package com.openclassrooms.p7.go4lunch.model;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lleotraas on 21.
 */
public class Restaurant implements Parcelable {

    private final String id;
    private final String name;
    private final String foodType;
    private final String adress;
    private final String openningHours;
    private final float distance;
    private final int interestedFriends;
    private final double rating;
    private Bitmap pictureUrl;

    public Restaurant(String id, String name, String foodType, String adress, String openningHours, float distance, int interestedFriends, double rating, Bitmap pictureUrl) {
        this.id = id;
        this.name = name;
        this.foodType = foodType;
        this.adress = adress;
        this.openningHours = openningHours;
        this.distance = distance;
        this.interestedFriends = interestedFriends;
        this.rating = rating;
        this.pictureUrl = pictureUrl;
    }

    protected Restaurant(Parcel in) {
        id = in.readString();
        name = in.readString();
        foodType = in.readString();
        adress = in.readString();
        openningHours = in.readString();
        distance = in.readFloat();
        interestedFriends = in.readInt();
        rating = in.readDouble();
        pictureUrl = in.readParcelable(Bitmap.class.getClassLoader());
    }

    public static final Creator<Restaurant> CREATOR = new Creator<Restaurant>() {
        @Override
        public Restaurant createFromParcel(Parcel in) {
            return new Restaurant(in);
        }

        @Override
        public Restaurant[] newArray(int size) {
            return new Restaurant[size];
        }
    };

    // --- GETTERS ---
    public String getId() { return id; }
    public String getName() { return name; }
    public String getFoodType() { return foodType; }
    public String getAdress() { return adress; }
    public String getOpenningHours() { return openningHours; }
    public float getDistance() { return distance; }
    public int getInterestedFriends() { return interestedFriends; }
    public double getRating() { return rating; }
    public Bitmap getPictureUrl() { return pictureUrl; }

    // --- SETTERS ---
    public void setPictureUrl(Bitmap pictureUrl){ this.pictureUrl = pictureUrl;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(foodType);
        parcel.writeString(adress);
        parcel.writeString(openningHours);
        parcel.writeFloat(distance);
        parcel.writeInt(interestedFriends);
        parcel.writeDouble(rating);
        parcel.writeParcelable(pictureUrl, i);
    }
}
