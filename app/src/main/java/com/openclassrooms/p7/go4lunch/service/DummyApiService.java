package com.openclassrooms.p7.go4lunch.service;


import static com.openclassrooms.p7.go4lunch.model.UserSettings.CUSTOM_THEME;
import static com.openclassrooms.p7.go4lunch.model.UserSettings.LIGHT_THEME;
import static com.openclassrooms.p7.go4lunch.model.UserSettings.NOTIFICATION;
import static com.openclassrooms.p7.go4lunch.model.UserSettings.NOTIFICATION_ENABLED;
import static com.openclassrooms.p7.go4lunch.model.UserSettings.PREFERENCES;
import static com.openclassrooms.p7.go4lunch.ui.fragment.DetailFragment.LIKE_BTN_TAG;
import static com.openclassrooms.p7.go4lunch.ui.fragment.map_view.MapViewFragment.currentLocation;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.location.Location;
import android.net.Uri;
import android.view.View;
import android.view.Window;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.Period;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.material.tabs.TabLayout;
import com.openclassrooms.p7.go4lunch.R;
import com.openclassrooms.p7.go4lunch.model.Restaurant;
import com.openclassrooms.p7.go4lunch.model.User;
import com.openclassrooms.p7.go4lunch.model.UserAndRestaurant;
import com.openclassrooms.p7.go4lunch.model.UserSettings;
import com.openclassrooms.p7.go4lunch.ui.MainActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DummyApiService implements ApiService {

    // --- LIST ---
    private final List<Restaurant> mRestaurantList = DummyUserAndRestaurant.generateRestaurant();
    private final List<Restaurant> mSearchedRestaurant = new ArrayList<>();
    private final List<UserAndRestaurant> mUserAndRestaurantList = DummyUserAndRestaurant.generateUserAndRestaurant();
    private final List<User> mUserList = DummyUserAndRestaurant.generateUsers();

    // --- GET LIST ---
    @Override
    public List<Restaurant> getRestaurant() {
        return mRestaurantList;
    }
    @Override
    public List<UserAndRestaurant> getUserAndRestaurant() {
        return mUserAndRestaurantList;
    }
    @Override
    public List<User> getUsers() {
        return mUserList;
    }
    @Override
    public List<Restaurant> getSearchedRestaurant() { return mSearchedRestaurant; }

    // --- ADD TO LIST ---
    @Override
    public void addUserAndRestaurant(UserAndRestaurant userAndRestaurant) {
        mUserAndRestaurantList.add(userAndRestaurant);
    }
    @Override
    public void addRestaurant(Restaurant restaurant) {
        mRestaurantList.add(restaurant);
    }
    @Override
    public void addUser(User user) {
        mUserList.add(user);
    }
    @Override
    public void deleteUser(User user) {
        mUserList.remove(user);
    }
    @Override
    public void addSearchedRestaurant(Restaurant restaurant) {

    }

    /**
     * Make a Map to copy it in the currentUser and send it to the Database.
     * @param currentUserId the id of the current user.
     * @return a UserAndRestaurant Map.
     */
    @Override
    public Map<String, UserAndRestaurant> makeUserAndRestaurantMap(String currentUserId) {
        Map<String, UserAndRestaurant> userAndRestaurantMap = new HashMap<>();
        for (UserAndRestaurant userAndRestaurant : getUserAndRestaurant()) {
            if (userAndRestaurant.getUserId().equals(currentUserId)) {
                userAndRestaurantMap.put(userAndRestaurant.getRestaurantId(), userAndRestaurant);
            }
        }
        return userAndRestaurantMap;
    }

    /**
     * Get the current hour and openingHour of the currentDay and calculate the remaining time before Restaurant close.
     * @param openingHours Restaurant openingHours.
     * @param context context of the fragment.
     * @return String with remaining time before restaurant close.
     */
    @Override
    public String makeStringOpeningHours(OpeningHours openingHours, Context context) {
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        String currentDays = this.getCurrentDay(calendar);
        for (Period openingDay : openingHours.getPeriods()) {
            if (Objects.requireNonNull(openingDay.getOpen()).getDay().toString().equals(currentDays)) {
                int closeHour = Objects.requireNonNull(openingDay.getClose()).getTime().getHours();
                int closeMinute = Objects.requireNonNull(openingDay.getClose()).getTime().getMinutes();
                int openHour = Objects.requireNonNull(openingDay.getOpen()).getTime().getHours();
                int openMinute = Objects.requireNonNull(openingDay.getOpen()).getTime().getMinutes();
                String closeTime = String.format("%s", closeHour);
                String openTime = String.format("%s", openHour);
                if (closeMinute > 0) {
                    closeTime = String.format("%s:%s", closeHour, closeMinute);
                }
                if (openMinute > 0) {
                    openTime = String.format("%s:%s", openHour, openMinute);
                }

                if (openingDay.getOpen().getTime().getHours() > currentHour) {
                    return String.format("%s %spm %s %spm",context.getResources().getString(R.string.list_view_holder_open_at), openTime,context.getResources().getString(R.string.list_view_holder_until), closeTime);
                }
                if (openingDay.getClose().getTime().getHours() > currentHour || openingDay.getClose().getTime().getHours() < 3) {
                    return String.format("%s %s pm",context.getResources().getString(R.string.list_view_holder_still_open_until), Math.abs(closeHour - currentHour));
                }
            }
        }
        return "still closed";
    }

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
     * @param userName name of the user.
     * @return first name format.
     */
    @Override
    public String makeUserFirstName(String userName) {
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
    public String makeInterestedFriendsString(List<User> interestedFriendList) {
        String friendsInterested = "";
        for (User user : interestedFriendList) {
            friendsInterested = friendsInterested + "\n" + makeUserFirstName(user.getUserName());
        }
        return friendsInterested;
    }

    //TODO comment
    @Override
    public String removeUselessWords(String restaurantName) {
        restaurantName = restaurantName.replace("RESTAURANT ", "");
        restaurantName = restaurantName.replace("Restaurant ", "");
        restaurantName = restaurantName.replace("restaurant ", "");
        String restaurant = restaurantName.toLowerCase();
        char[] restaurantNameArray = restaurant.toCharArray();
        restaurantNameArray[0] = Character.toUpperCase(restaurantNameArray[0]);
        return new String(restaurantNameArray);
    }

    /**
     * Search User currently logged to the application.
     * @param currentUserId id of the current User.
     * @return current User.
     */
    @Override
    public User searchUserById(String currentUserId) {
        User userFound = null;
        for (User user : getUsers()) {
            if (currentUserId.equals(user.getUid())) {
                userFound = user;
            }
        }
        return userFound;
    }

    /**
     * Search current Restaurant to detail it.
     * @param restaurantId id of the clicked Restaurant.
     * @return Restaurant to detail.
     */
    @Override
    public Restaurant searchCurrentRestaurantById(String restaurantId) {
        Restaurant restaurantFound = null;
        if ( !getSearchedRestaurant().isEmpty()) {
            if (restaurantId.equals(getSearchedRestaurant().get(0).getId())) {
                restaurantFound = getSearchedRestaurant().get(0);
            }
        }
        for (Restaurant restaurant : getRestaurant()) {
            if (restaurantId.equals(restaurant.getId())){
                restaurantFound = restaurant;
            }
        }
        return restaurantFound;
    }

    /**
     * Make a UserAndRestaurantList with all User contains Restaurant found nearby.
     * @param userId current user id.
     * @param restaurantId current restaurant id.
     * @return current userAndRestaurant.
     */
    @Override
    public UserAndRestaurant searchUserAndRestaurantById(String userId, String restaurantId) {
        UserAndRestaurant userAndRestaurantFound = null;
        for (UserAndRestaurant userAndRestaurant : getUserAndRestaurant()) {
            if (
                    userAndRestaurant.getUserId().equals(userId) &&
                            userAndRestaurant.getRestaurantId().equals(restaurantId)
            ) {
                userAndRestaurantFound = userAndRestaurant;
            }
        }
        return userAndRestaurantFound;
    }

    /**
     * Deselect the previous UserAndRestaurant selected in the UserAndRestaurantList.
     * @param currentUserId Current User id.
     * @param currentRestaurantId Current Restaurant id.
     */
    @Override
    public void searchSelectedUserAndRestaurantToDeselect(String currentUserId, String currentRestaurantId) {
        for (int index = 0; index < getUserAndRestaurant().size(); index++) {
            if (
                    getUserAndRestaurant().get(index).getUserId().equals(currentUserId) &&
                    !getUserAndRestaurant().get(index).getRestaurantId().equals(currentRestaurantId) &&
                    getUserAndRestaurant().get(index).isSelected())
            {
                getUserAndRestaurant().get(index).setSelected(false);
            }
        }
    }

    /**
     * Like or select Restaurant show in Details depend which button was clicked.
     * @param currentUserId current User id.
     * @param currentRestaurantId current Restaurant id.
     * @param buttonId Clicked button id.
     */
    @Override
    public void likeOrSelectRestaurant(String currentUserId, String currentRestaurantId, int buttonId) {
        for (int index = 0; index < getUserAndRestaurant().size(); index++) {
            if (
                    currentRestaurantId.equals(getUserAndRestaurant().get(index).getRestaurantId()) &&
                            currentUserId.equals(getUserAndRestaurant().get(index).getUserId())
            ) {
                if (buttonId == LIKE_BTN_TAG) {
                    getUserAndRestaurant().get(index).setFavorite(!getUserAndRestaurant().get(index).isFavorite());
                } else {
                    getUserAndRestaurant().get(index).setSelected(!getUserAndRestaurant().get(index).isSelected());
                }
            }
        }
    }

    /**
     * Call to search the selected Restaurant by the User and show it in the WorkmatesRecyclerView.
     * @param user User in the Database.
     * @return UserAndRestaurant selected.
     */
    @Override
    public UserAndRestaurant searchSelectedRestaurant(User user) {
        UserAndRestaurant userAndRestaurantFound = null;
        for (UserAndRestaurant userAndRestaurant : getUserAndRestaurant()) {
            if (userAndRestaurant.isSelected() && user.getUid().equals(userAndRestaurant.getUserId())) {
                userAndRestaurantFound = userAndRestaurant;
            }
        }
        return userAndRestaurantFound;
    }

    /**
     * Call to know how many users are interested at current Restaurant for the ListViewFragment and the DetailsActivity RecyclerView.
     * First we search the userId corresponding to the restaurant selected.
     * Second we looking for the user with his id and add him to the list.
     * @param currentUserId current User id.
     * @param currentRestaurant Restaurant to compare with UserAndRestaurant.
     * @return List of User interested at the Restaurant.
     */
    @Override
    public List<User> getUsersInterestedAtCurrentRestaurant(String currentUserId, Restaurant currentRestaurant) {
        List<User> usersInterested = new ArrayList<>();
        for (UserAndRestaurant restaurantSelected : getUserAndRestaurant()) {
            if (
                    restaurantSelected.isSelected() &&
                    !currentUserId.equals(restaurantSelected.getUserId()) &&
                    currentRestaurant.getId().equals(restaurantSelected.getRestaurantId())
            ) {
                User user = searchUserById(restaurantSelected.getUserId());
                usersInterested.add(user);
            }
        }
        currentRestaurant.setNumberOfFriendInterested(usersInterested.size());
        return usersInterested;
    }

    public List<User> getUsersInterestedAtCurrentRestaurant(String currentUserId) {
        List<User> usersInterested = new ArrayList<>();
        for (UserAndRestaurant restaurantSelected : getUserAndRestaurant()) {
            if (
                    restaurantSelected.isSelected() &&
                            !currentUserId.equals(restaurantSelected.getUserId())
            ) {
                User user = searchUserById(restaurantSelected.getUserId());
                usersInterested.add(user);
            }
        }
        return usersInterested;
    }

    //TODO comments
    @Override
    public UserAndRestaurant getCurrentUserSelectedRestaurant(User user) {
        for (Map.Entry<String, UserAndRestaurant> userAndRestaurant : user.getUserAndRestaurant().entrySet()) {
            if (
                    userAndRestaurant.getValue().getUserId().equals(user.getUid()) &&
                    userAndRestaurant.getValue().isSelected()
            ) {
                return userAndRestaurant.getValue();
            }
        }
        return null;
    }

    /**
     * Format opening hour for show it.
     * @param openingHours place opening hours.
     * @param context context of the fragment.
     * @return opening hours if available.
     */
    @Override
    public String getOpeningHours(OpeningHours openingHours, Context context) {
        String noDetails = String.format("%s", R.string.workmates_list_view_holder_no_details_here);
        if (openingHours != null) {
            return makeStringOpeningHours(openingHours, context);
        }
        return noDetails;
    }

    /**
     * Get the Restaurant website if available and format it to a String.
     * @param websiteUri uri website.
     * @return String url.
     */
    @Override
    public String getWebsiteUri(Uri websiteUri) {
        String noWebsiteUrl = "";
        if (websiteUri != null) {
            return String.format("%s", websiteUri);
        }
        return noWebsiteUrl;
    }

    /**
     * Get the Restaurant Rating if available.
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
     * @param placeLocation Restaurant location.
     * @param currentLocation User Location.
     * @return distance between User and Restaurant.
     */
    @Override
    public float getDistance(LatLng placeLocation, LatLng currentLocation) {
        float[] distance = new float[10];
        Location.distanceBetween(
                currentLocation.latitude,
                currentLocation.longitude,
                Objects.requireNonNull(placeLocation).latitude,
                Objects.requireNonNull(placeLocation).longitude,
                distance
        );
        return distance[0];
    }

    /**
     * set a limit zone around the current User location for better search result.
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
     * @param index Loop index.
     * @param rating Restaurant rating.
     * @return rating image to set.
     */
    @Override
    public int setRatingStars(int index, double rating) {
        int convertedRating = (int) rating;
            if (convertedRating == 2 && index == 1|| convertedRating == 4 && index == 2) {
                return R.drawable.baseline_star_half_black_24;
            }
            if (convertedRating < 4 && index == 2 || convertedRating < 2 && index == 1) {
               return R.drawable.baseline_star_border_black_24;
            }
        return R.drawable.baseline_star_rate_black_24;
    }

    /**
     * Call if Restaurant is liked or not.
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
     * Call to set marker at Restaurant Location default return red marker, selected Restaurant return green marker, searched Restaurant return blue marker.
     * @param placeId place id.
     * @param isSearched to define if it's a nearby search or a search.
     * @param restaurantPosition restaurant location.
     * @param mMap the current GoogleMap.
     * @return marker image to set.
     */
    public int setMarkerIcon(String placeId, boolean isSearched, LatLng restaurantPosition, GoogleMap mMap) {
        for (UserAndRestaurant userAndRestaurant : getUserAndRestaurant()) {
            if (userAndRestaurant.getRestaurantId().equals(placeId) && userAndRestaurant.isSelected() && !isSearched) {
                //TODO these marker still here even the restaurant is deselected.
                return R.drawable.baseline_place_cyan;
            }
            if (isSearched) {
                return R.drawable.baseline_place_green;
            }
        }
        return R.drawable.baseline_place_orange;
    }



    /**
     * Update markers on map when the user go back on the MapViewFragment.
     * @param isSearched to define if it's a nearby search or a search.
     * @param map current Google Maps
     */
    public void updateMarkerOnMap(boolean isSearched, GoogleMap map) {
        for (Restaurant restaurantFound : getRestaurant()) {
            MarkerOptions options = new MarkerOptions();
            options.icon(BitmapDescriptorFactory.fromResource(setMarkerIcon(restaurantFound.getId(), isSearched, restaurantFound.getPosition(), map)));
            LatLng latLng = new LatLng(restaurantFound.getPosition().latitude, restaurantFound.getPosition().longitude);
            options.position(latLng);
            options.snippet(restaurantFound.getId());
            map.addMarker(options);
        }
    }

    /**
     * Put markers on map for each Restaurant found after a nearby search or a search.
     * @param restaurant Restaurant to set a marker on.
     * @param map current Google Maps
     */
    public void setMarkerOnMap(Restaurant restaurant, GoogleMap map, boolean isSearched) {
        MarkerOptions options = new MarkerOptions();
        options.icon(BitmapDescriptorFactory.fromResource(setMarkerIcon(restaurant.getId(), isSearched, restaurant.getPosition(), map)));
        LatLng latLng = new LatLng(restaurant.getPosition().latitude, restaurant.getPosition().longitude);
        options.position(latLng);
        options.snippet(restaurant.getId());
        if (isSearched) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(restaurant.getPosition().latitude,
                            restaurant.getPosition().longitude), 15));
        }
        map.addMarker(options);
    }

    /**
     * Call to sort restaurantList with most users interested first.
     */
    @Override
    public void listViewComparator() {
        Collections.sort(getRestaurant(), new Restaurant.RestaurantComparator());
    }

    /**
     * Call to found user have selected a restaurant and set it to the top of userList.
     */
    @Override
    public void filterUsersInterestedAtCurrentRestaurant() {
        for (UserAndRestaurant userAndRestaurants : getUserAndRestaurant()) {
            if (userAndRestaurants.isSelected()) {
                User userFound = searchUserById(userAndRestaurants.getUserId());
                deleteUser(userFound);
                getUsers().add(0, userFound);
            }
        }
    }

    @Override
    public boolean notificationEnabled(Context context) {
        return getCurrentNotification(context).equals(NOTIFICATION_ENABLED);
    }

    private String getCurrentNotification(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        return sharedPreferences.getString(NOTIFICATION, NOTIFICATION_ENABLED);
    }
}
