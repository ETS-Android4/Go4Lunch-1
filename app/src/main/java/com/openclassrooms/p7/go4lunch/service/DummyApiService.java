package com.openclassrooms.p7.go4lunch.service;

import static com.openclassrooms.p7.go4lunch.ui.fragment.map_view.MapViewFragment.currentLocation;

import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.openclassrooms.p7.go4lunch.R;
import com.openclassrooms.p7.go4lunch.model.Restaurant;
import com.openclassrooms.p7.go4lunch.model.User;
import com.openclassrooms.p7.go4lunch.model.UserAndRestaurant;
import com.openclassrooms.p7.go4lunch.ui.DetailActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * Created by lleotraas on 21.
 */
public class DummyApiService implements ApiService {

    // --- LIST ---
    private final List<Restaurant> mRestaurantList = DummyUserAndRestaurant.generateRestaurant();
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

    /**
     * Make a Map to copy it in the currentUser and send it to the Database.
     * @param currentUserId th id of the current user.
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
     * Make a UserAndRestaurantList with all User contains Restaurant found nearby.
     * @param restaurant restaurant found in nearby search.
     */
    @Override
    public void makeUserAndRestaurantList(Restaurant restaurant) {
        UserAndRestaurant userAndRestaurant;
        for (int index = 0;index < getUsers().size();index++) {
            userAndRestaurant = getUsers().get(index).getUserAndRestaurant ().get(restaurant.getId());
            if (userAndRestaurant != null) {
                addUserAndRestaurant(userAndRestaurant);
            }
        }
    }

    /**
     * Get the current hour and openingHour of the currentDay and calculate the remaining time before Restaurant close.
     * @param openingHours Restaurant openingHours.
     * @return String with remaining time before restaurant close.
     */
    @Override
    public String makeStringOpeningHours(OpeningHours openingHours) {
        Calendar calendar = Calendar.getInstance(Locale.FRANCE);
        int currentHour = calendar.get(Calendar.HOUR);
        int hours = Objects.requireNonNull(openingHours.getPeriods().get(0).getOpen()).getTime().getHours();
        //TODO modify
        if (currentHour > hours) {
            return "Still closed";
        }else {
            return "open until " + (hours - currentHour) + "pm";
        }
    }

    /**
     * Search current Restaurant to detail it.
     * @param restaurantId id of the clicked Restaurant.
     * @return Restaurant to detail.
     */
    @Override
    public Restaurant searchCurrentRestaurantById(String restaurantId) {
        Restaurant restaurantFound = null;
        for (Restaurant restaurant : getRestaurant()) {
            if (restaurantId.equals(restaurant.getId())){
                restaurantFound = restaurant;
            }
        }
        return restaurantFound;
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
                if (buttonId == DetailActivity.LIKE_BTN_TAG) {
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
     * @param currentUserId current User id.
     * @param restaurantId Restaurant to compare with UserAndRestaurant.
     * @return List of User interested at the Restaurant.
     */
    @Override
    public List<User> getUsersInterestedAtCurrentRestaurant(String currentUserId, String restaurantId) {
        List<User> userList = new ArrayList<>();
        for (UserAndRestaurant userAndRestaurants : getUserAndRestaurant()) {
            if (
                    userAndRestaurants.isSelected() &&
                    !currentUserId.equals(userAndRestaurants.getUserId()) &&
                    restaurantId.equals(userAndRestaurants.getRestaurantId())
            ) {
                User user = searchUserById(userAndRestaurants.getUserId());
                userList.add(user);
            }
        }
        return userList;
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
     * @param isFavorite
     * @return liked image to set.
     */
    @Override
    public int setFavoriteImage(boolean isFavorite) {
            if (isFavorite) {
                return R.drawable.baseline_star_rate_black_36;
            }
            return R.drawable.baseline_star_border_black_36;
    }

    /**
     * Call if Restaurant is selected or not.
     * @param selected
     * @return selected image to set.
     */
    @Override
    public int setSelectedImage(boolean selected) {
        if (selected) {
            return R.drawable.baseline_check_circle_black_24;
        }
        return R.drawable.baseline_check_circle_outline_24;
    }

    /**
     * Call to set marker at Restaurant Location default return red marker, selected Restaurant return green marker, searched Restaurant return blue marker.
     * @param placeId place id.
     * @param isSearched to define if it's a nearby search or a search.
     * @param restaurantPosition
     * @param mMap the current GoogleMap
     * @return marker image to set.
     */
    public int setMarkerIcon(String placeId, boolean isSearched, LatLng restaurantPosition, GoogleMap mMap) {
        for (UserAndRestaurant userAndRestaurant : getUserAndRestaurant()) {
            if (userAndRestaurant.getRestaurantId().equals(placeId) && userAndRestaurant.isSelected() && !isSearched) {
                return R.drawable.baseline_place_cyan;
            } else if (isSearched) {
                return R.drawable.baseline_place_green;
            }
        }
        return R.drawable.baseline_place_orange;
    }

    /**
     * set a limit zone around the current User location for better search result.
     * @param currentLocation current User Location.
     * @return RectangularBound around the User.
     */
    @Override
    public RectangularBounds getRectangularBound(LatLng currentLocation) {
        return RectangularBounds.newInstance(
                new LatLng(currentLocation.latitude - 0.001000, currentLocation.longitude + 0.025000),
                new LatLng(currentLocation.latitude + 0.025000, currentLocation.longitude + 0.001000));
    }

    /**
     * Format openning hour for show it.
     * @param openingHours
     * @return opening hours if available
     */
    @Override
    public String getOpeningHours(OpeningHours openingHours) {
        String noDetails = String.format("%s", R.string.no_details_here);
        if (openingHours != null) {
            return makeStringOpeningHours(openingHours);
        }
        return noDetails;
    }

    /**
     * Calculate the distance between User and Restaurant.
     * @param placeLocation Restaurant location.
     * @param currentLocation User Location.
     * @return distance between User and Restaurant
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
     * Create a Restaurant from nearby search.
     * @param place Restaurant.
     * @param placeImage Restaurant image.
     * @return Restaurant.
     */
    @Override
    public Restaurant createRestaurant(Place place, Bitmap placeImage) {
        return new Restaurant(
                place.getId(),
                place.getName(),
                place.getAddress(),
                getOpeningHours(place.getOpeningHours()),
                place.getPhoneNumber(),
                getWebsiteUri(place.getWebsiteUri()),
                getDistance(place.getLatLng(), currentLocation),
                getRating(place.getRating()),
                place.getLatLng(),
                placeImage
        );
    }

    /**
     * Update markers on map when the user go back on the MapViewFragment.
     * @param isSearched to define if it's a nearby search or a search.
     * @param map current Google Maps
     */
    //TODO isSearched maybe useless.
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
     * @param isSearched to define if it's a nearby search or a search.
     * @param map current Google Maps
     */
    public void setMarkerOnMap(Restaurant restaurant , boolean isSearched, GoogleMap map) {
        MarkerOptions options = new MarkerOptions();
        options.icon(BitmapDescriptorFactory.fromResource(setMarkerIcon(restaurant.getId(), isSearched, restaurant.getPosition(), map)));
        LatLng latLng = new LatLng(restaurant.getPosition().latitude, restaurant.getPosition().longitude);
        options.position(latLng);
        options.snippet(restaurant.getId());
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(restaurant.getPosition().latitude,
                        restaurant.getPosition().longitude),15));
        map.addMarker(options);
    }
}
