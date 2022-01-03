package com.openclassrooms.p7.go4lunch.service;


import android.location.Location;
import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.openclassrooms.p7.go4lunch.R;
import com.openclassrooms.p7.go4lunch.model.Restaurant;
import com.openclassrooms.p7.go4lunch.model.User;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class DummyApiService implements ApiService {

    public String getCurrentDay(Calendar calendar) {
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        String currentDays = "";
        switch (day - 1) {
            case 0:
                currentDays = "SUNDAY";
                break;
            case 1:
                currentDays = "MONDAY";
                break;
            case 2:
                currentDays = "TUESDAY";
                break;
            case 3:
                currentDays = "WEDNESDAY";
                break;
            case 4:
                currentDays = "THURSDAY";
                break;
            case 5:
                currentDays = "FRIDAY";
                break;
            case 6:
                currentDays = "SATURDAY";
                break;
        }
        return currentDays;
    }

    /**
     * Used to format username with his first name and put a uppercase on the first letter.
     *
     * @param userName name of the user.
     * @return first name format.
     */
    @Override
    public String formatUserFirstName(String userName) {
        int i = userName.indexOf(' ');
        if (i > 0) {
            String firstName = userName.substring(0, i);
            char[] firstNameArray = firstName.toCharArray();
            firstNameArray[0] = Character.toUpperCase(firstNameArray[0]);
            return new String(firstNameArray);
        } else {
            return userName;
        }
    }

    //TODO comment
    @Override
    public String formatRestaurantName(String restaurantName) {
        if (!restaurantName.equals("")) {
            restaurantName = restaurantName.replace("RESTAURANT ", "");
            restaurantName = restaurantName.replace("Restaurant ", "");
            restaurantName = restaurantName.replace("restaurant ", "");
            String restaurant = restaurantName.toLowerCase();
            char[] restaurantNameArray = restaurant.toCharArray();
            restaurantNameArray[0] = Character.toUpperCase(restaurantNameArray[0]);
            return new String(restaurantNameArray);
        }
        return restaurantName;
    }

    @Override
    public String makeInterestedFriendsString(List<User> interestedFriendList) {
        String friendsInterested = "";
        for (User user : interestedFriendList) {
            friendsInterested = friendsInterested + "\n" + formatUserFirstName(user.getUserName());
        }
        return friendsInterested;
    }

    /**
     * Get the Restaurant website if available and format it to a String.
     *
     * @param websiteUri uri website.
     * @return String url.
     */
    @Override
    public String getWebsiteUri(Uri websiteUri) {
        if (websiteUri != null) {
            return websiteUri.toString();
        }
        return "";
    }

    /**
     * Get the Restaurant Rating if available.
     *
     * @param rating rating.
     * @return rating or noRating.
     */
    @Override
    public double getRating(Double rating) {
        double noRating = 0.0;
        if (rating != null) {
            return rating;
        }
        return noRating;
    }

    /**
     * Calculate the distance between User and Restaurant.
     *
     * @param placeLocation   Restaurant location.
     * @param currentLocation User Location.
     * @return distance between User and Restaurant.
     */
    @Override
    public float getDistance(LatLng placeLocation, LatLng currentLocation) {
        if (currentLocation != null) {
            float[] distance = new float[10];
            Location.distanceBetween(
                    currentLocation.latitude,
                    currentLocation.longitude,
                    Objects.requireNonNull(placeLocation).latitude,
                    Objects.requireNonNull(placeLocation).longitude,
                    distance
            );
            return distance[0];
        } else {
            return 0;
        }
    }

    /**
     * set a limit zone around the current User location for better search result.
     *
     * @param currentLocation current User Location.
     * @return RectangularBound around the User.
     */
    @Override
    public RectangularBounds getRectangularBound(LatLng currentLocation) {
        return RectangularBounds.newInstance(
                new LatLng(currentLocation.latitude - 0.050000, currentLocation.longitude + 0.050000),
                new LatLng(currentLocation.latitude + 0.050000, currentLocation.longitude + 0.050000));
    }

    /**
     * Call three time to show the rating of Restaurant in ListViewFragment and DetailsActivity.
     *
     * @param index  Loop index.
     * @param rating Restaurant rating.
     * @return rating image to set.
     */
    @Override
    public int setRatingStars(int index, double rating) {
        int convertedRating = (int) rating;
        if (convertedRating == 2 && index == 1 || convertedRating == 4 && index == 2) {
            return R.drawable.baseline_star_half_black_24;
        }
        if (convertedRating < 4 && index == 2 || convertedRating < 2 && index == 1 || convertedRating == 0) {
            return R.drawable.baseline_star_border_black_24;
        }
        return R.drawable.baseline_star_rate_black_24;
    }

    /**
     * Call if Restaurant is liked or not.
     *
     * @param isFavorite if restaurant is favorite or not.
     * @return liked image to set.
     */
    @Override
    public int setFavoriteImage(boolean isFavorite) {
        if (isFavorite) {
            return R.drawable.ic_star;
        }
        return R.drawable.ic_star_outline;
    }

    /**
     * Call if Restaurant is selected or not.
     *
     * @param selected if restaurant is selected or not.
     * @return selected image to set.
     */
    @Override
    public int setSelectedImage(boolean selected) {
        if (selected) {
            return R.drawable.baseline_check_circle_24;
        }
        return R.drawable.baseline_check_circle_outline_black_24;
    }

    /**
     * Call to sort restaurantList with most users interested first.
     */
    @Override
    public void listViewComparator(List<Restaurant> restaurantList) {
        Collections.sort(restaurantList, new Restaurant.RestaurantComparator());
    }

    @Override
    public void workmatesViewComparator(List<User> userList) {
        Collections.sort(userList, new User.UserComparator());
    }
}
