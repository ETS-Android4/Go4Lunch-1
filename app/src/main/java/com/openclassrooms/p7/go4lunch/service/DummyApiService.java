package com.openclassrooms.p7.go4lunch.service;


import static com.openclassrooms.p7.go4lunch.model.SortMethod.DISTANCE_ASCENDING;
import static com.openclassrooms.p7.go4lunch.model.SortMethod.DISTANCE_DESCENDING;
import static com.openclassrooms.p7.go4lunch.model.SortMethod.FAVORITE_ASCENDING;
import static com.openclassrooms.p7.go4lunch.model.SortMethod.FAVORITE_DESCENDING;
import static com.openclassrooms.p7.go4lunch.model.SortMethod.INTERESTED_ASCENDING;
import static com.openclassrooms.p7.go4lunch.model.SortMethod.INTERESTED_DESCENDING;
import static com.openclassrooms.p7.go4lunch.model.SortMethod.RATING_ASCENDING;
import static com.openclassrooms.p7.go4lunch.model.SortMethod.RATING_DESCENDING;
import static com.openclassrooms.p7.go4lunch.model.SortMethod.SEARCHED_ASCENDING;
import static com.openclassrooms.p7.go4lunch.model.SortMethod.SEARCHED_DESCENDING;

import android.location.Location;
import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.LocalTime;
import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.Period;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.openclassrooms.p7.go4lunch.R;
import com.openclassrooms.p7.go4lunch.model.Restaurant;
import com.openclassrooms.p7.go4lunch.model.RestaurantFavorite;
import com.openclassrooms.p7.go4lunch.model.SortMethod;
import com.openclassrooms.p7.go4lunch.model.User;

import java.util.ArrayList;
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
     * Format opening hour for show it.
     * @param openingHours place opening hours.
     * @return opening hours if available.
     */
    public String getOpeningHours(OpeningHours openingHours) {
        if (openingHours != null) {
            String currentDay = getCurrentDay(Calendar.getInstance());
            LocalTime currentTime = LocalTime.newInstance(Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE));
            return makeStringOpeningHours(openingHours, currentDay, currentTime);
        }
        // no details code.
        return "5";
    }

    /**
     * Get the current hour and openingHour of the currentDay and calculate the remaining time before Restaurant close.
     *
     * @param openingHours Restaurant openingHours.
     * @return String with remaining time before restaurant close.
     */
    public String makeStringOpeningHours(OpeningHours openingHours, String currentDay, LocalTime currentTime) {
        int currentHour = currentTime.getHours();
        for (Period openingDay : openingHours.getPeriods()) {
            if (Objects.requireNonNull(openingDay.getOpen()).getDay().toString().equals(currentDay)) {
                if (openingDay.getOpen().getTime().getHours() <= (currentHour + 4)) {
                    int hours = Objects.requireNonNull(openingDay.getOpen()).getTime().getHours();
                    int minutes = Objects.requireNonNull(openingDay.getOpen()).getTime().getMinutes();
                    int closeHours = Objects.requireNonNull(openingDay.getClose()).getTime().getHours();
                    int closeMinutes = Objects.requireNonNull(openingDay.getClose()).getTime().getMinutes();

                    // open hours code.
                    int firstCode = 0;
                    if (
                            closeHours > currentHour && hours <= currentHour ||
                                    closeHours < currentHour && closeHours <= 3 && closeHours >= 0 && hours < currentHour
                    ) {

                        // close hour code.
                        firstCode = 1;
                        hours = closeHours;
                        minutes = closeMinutes;

                    }

                    // morning code.
                    int lastCode = 0;

                    if (currentHour > 12) {
                        // afternoon code.
                        lastCode = 1;
                    }
                    // show minutes.
                    if (minutes > 0 && hours > currentHour) {
                        return String.format("%s%s:%s%s", firstCode, hours, minutes, lastCode);
                    }
                    // don't show minutes.
                    if (hours > currentHour || hours <= 3 && hours >= 0 && hours < currentHour) {
                        return String.format("%s%s%s", firstCode, hours, lastCode);
                    }
                }
            }
        }
        // still closed code.
        return "4";
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
    public String formatInterestedFriends(List<User> interestedFriendList) {
        String friendsInterested = "";
        for (User user : interestedFriendList) {
            friendsInterested = String.format("%s\n%s",formatUserFirstName(user.getUserName()), friendsInterested);
        }
        return friendsInterested;
    }

    @Override
    public List<User> getInterestedFriend(List<User> userList, String restaurantId) {
        List<User> interestedFriends = new ArrayList<>();
        for (User user : userList) {
            if (user.getRestaurantId().equals(restaurantId)) {
                interestedFriends.add(user);
            }
        }
        return interestedFriends;
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
                new LatLng(currentLocation.latitude - 0.060000, currentLocation.longitude + 0.060000),
                new LatLng(currentLocation.latitude + 0.060000, currentLocation.longitude + 0.060000));
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
            return R.drawable.baseline_star_half_24;
        }
        if (convertedRating < 4 && index == 2 || convertedRating < 2 && index == 1 || convertedRating == 0) {
            return R.drawable.baseline_star_border_24;
        }
        return R.drawable.baseline_star_rate_24;
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
        return R.drawable.baseline_check_circle_outline_24;
    }

    /**
     * Call to sort restaurantList with most users interested first.
     */

    public void restaurantComparator(List<Restaurant> restaurantList, SortMethod sortMethod) {
        switch (sortMethod) {
            case INTERESTED_ASCENDING: Collections.sort(restaurantList, new Restaurant.RestaurantFriendInterestedAscendingComparator());
            break;
            case INTERESTED_DESCENDING: Collections.sort(restaurantList, new Restaurant.RestaurantFriendInterestedDescendingComparator());
                break;
            case RATING_ASCENDING: Collections.sort(restaurantList, new Restaurant.RestaurantRatingAscendingComparator());
                break;
            case RATING_DESCENDING: Collections.sort(restaurantList, new Restaurant.RestaurantRatingDescendingComparator());
                break;
            case DISTANCE_ASCENDING: Collections.sort(restaurantList, new Restaurant.RestaurantDistanceAscendingComparator());
                break;
            case DISTANCE_DESCENDING: Collections.sort(restaurantList, new Restaurant.RestaurantDistanceDescendingComparator());
                break;
            case FAVORITE_ASCENDING: Collections.sort(restaurantList, new Restaurant.RestaurantFavoriteAscendingComparator());
                break;
            case FAVORITE_DESCENDING: Collections.sort(restaurantList, new Restaurant.RestaurantFavoriteDescendingComparator());
                break;
            case SEARCHED_ASCENDING: Collections.sort(restaurantList, new Restaurant.RestaurantSearchedAscendingComparator());
                break;
            case SEARCHED_DESCENDING: Collections.sort(restaurantList, new Restaurant.RestaurantSearchedDescendingComparator());
                break;
        }


    }

    @Override
    public void workmatesViewComparator(List<User> userList) {
        Collections.sort(userList, new User.UserComparator());
    }
}
