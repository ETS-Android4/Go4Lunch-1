package com.openclassrooms.p7.go4lunch;

import static com.openclassrooms.p7.go4lunch.TestUtils.FIRST_RESTAURANT_ID;
import static com.openclassrooms.p7.go4lunch.TestUtils.RESTAURANT_ADDRESS;
import static com.openclassrooms.p7.go4lunch.TestUtils.RESTAURANT_DISTANCE;
import static com.openclassrooms.p7.go4lunch.TestUtils.RESTAURANT_LOCATION;
import static com.openclassrooms.p7.go4lunch.TestUtils.RESTAURANT_NAME;
import static com.openclassrooms.p7.go4lunch.TestUtils.RESTAURANT_OPENING_HOURS;
import static com.openclassrooms.p7.go4lunch.TestUtils.RESTAURANT_PHONE_NUMBER;
import static com.openclassrooms.p7.go4lunch.TestUtils.RESTAURANT_RATING;
import static com.openclassrooms.p7.go4lunch.TestUtils.RESTAURANT_WEBSITE_URL;
import static com.openclassrooms.p7.go4lunch.TestUtils.SECOND_RESTAURANT_ID;
import static com.openclassrooms.p7.go4lunch.TestUtils.THIRD_RESTAURANT_ID;
import static com.openclassrooms.p7.go4lunch.TestUtils.USER_CURRENT_LOCATION;
import static com.openclassrooms.p7.go4lunch.TestUtils.USER_RESTAURANT_ID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.runner.AndroidJUnit4;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseUser;
import com.openclassrooms.p7.go4lunch.injector.Go4LunchApplication;
import com.openclassrooms.p7.go4lunch.model.Restaurant;
import com.openclassrooms.p7.go4lunch.model.User;
import com.openclassrooms.p7.go4lunch.repository.MapViewRepository;
import com.openclassrooms.p7.go4lunch.repository.PlaceTask;
import com.openclassrooms.p7.go4lunch.repository.RestaurantFavoriteRepository;
import com.openclassrooms.p7.go4lunch.repository.UserRepository;
import com.openclassrooms.p7.go4lunch.service.DummyApiService;
import com.openclassrooms.p7.go4lunch.ui.UserAndRestaurantViewModel;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MapViewRepositoryTest {

    private final RestaurantFavoriteRepository restaurantFavoriteRepository = RestaurantFavoriteRepository.getInstance();
    private final MapViewRepository mapViewRepository = MapViewRepository.getInstance();
    private final PlaceTask placeTask = new PlaceTask();
    private UserAndRestaurantViewModel viewModel;
    private final UserRepository userRepository = UserRepository.getInstance();
    private final Context context = Go4LunchApplication.getContext();

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setup() {
        viewModel = new UserAndRestaurantViewModel(
                userRepository,
                restaurantFavoriteRepository,
                mapViewRepository,
                placeTask
        );
        Places.initialize(context, BuildConfig.GMP_KEY);
    }

    @Test
    public void testa_requestForPlaceDetails_withSuccess_andGetRestaurantList_shouldReturn3() throws InterruptedException {
        LiveDataTestUtils.observeForTesting(viewModel.getIsAlreadyNearbySearched(), liveData -> {
            assertTrue(liveData.getValue());
        });
        List<String> placeId = new ArrayList<>();
        placeId.add(FIRST_RESTAURANT_ID);
        placeId.add(SECOND_RESTAURANT_ID);
        placeId.add(THIRD_RESTAURANT_ID);
        viewModel.requestForPlaceDetails(placeId, context, false);
        LiveDataTestUtils.observeForTesting(viewModel.getAllRestaurants(), liveData -> {
            Thread.sleep(1000);
            List<Restaurant> restaurantList = new ArrayList<>(Objects.requireNonNull(liveData.getValue()));
            assertEquals(restaurantList.size(), 3);
        });
        LiveDataTestUtils.observeForTesting(viewModel.getIsAlreadyNearbySearched(), liveData -> {
            assertFalse(liveData.getValue());
        });
    }

    @Test
    public void testb_requestForPlaceDetails_withSuccess_andGetRestaurantList_shouldReturn1() throws InterruptedException {
        LiveDataTestUtils.observeForTesting(viewModel.getIsAlreadyNearbySearched(), liveData -> {
            assertFalse(liveData.getValue());
        });
        List<String> placeId = new ArrayList<>();
        placeId.add(FIRST_RESTAURANT_ID);
        viewModel.requestForPlaceDetails(placeId, context, true);
        LiveDataTestUtils.observeForTesting(viewModel.getAllRestaurants(), liveData -> {
            Thread.sleep(1000);
            List<Restaurant> restaurantList = new ArrayList<>(Objects.requireNonNull(liveData.getValue()));
            assertEquals(restaurantList.size(), 4);
        });
    }

    @Test
    public void testc_getCurrentRestaurant_shouldReturnRestaurant() throws InterruptedException {
        LiveDataTestUtils.observeForTesting(viewModel.getAllRestaurants(), liveData -> {
            Thread.sleep(500);
            List<Restaurant> restaurantList = new ArrayList<>(Objects.requireNonNull(liveData.getValue()));
            assertEquals(restaurantList.size(), 4);
            Restaurant restaurant = viewModel.getCurrentRestaurant(FIRST_RESTAURANT_ID, restaurantList);
            assert restaurant.getId().equals(FIRST_RESTAURANT_ID);
        });
    }

    @Test
    public void testd_setNumberOfFriendInterested_withSuccess() throws InterruptedException {
        LiveDataTestUtils.observeForTesting(viewModel.getAllRestaurants(), liveData -> {
            viewModel.setNumberOfFriendInterested(getDefaultUser(), Objects.requireNonNull(liveData.getValue()));

            Restaurant firstRestaurant = viewModel.getCurrentRestaurant(FIRST_RESTAURANT_ID, liveData.getValue());
            Restaurant secondRestaurant = viewModel.getCurrentRestaurant(SECOND_RESTAURANT_ID, liveData.getValue());
            Restaurant thirdRestaurant = viewModel.getCurrentRestaurant(THIRD_RESTAURANT_ID, liveData.getValue());

            Integer twoFriendInterested = 2, oneFriendInterested = 1, noFriendInterested = 0;

            assertEquals(twoFriendInterested, firstRestaurant.getNumberOfFriendInterested());
            assertEquals(oneFriendInterested, secondRestaurant.getNumberOfFriendInterested());
            assertEquals(noFriendInterested, thirdRestaurant.getNumberOfFriendInterested());
        });

    }

    private List<User> getDefaultUser() {
        List<User> userList = new ArrayList<>();
        User userTest1 = new User("1111", "test1", "photo", "restaurant1", "ChIJexGknqI1sRIRWr8XRhcWfKw", true);
        User userTest2 = new User("2222", "test2", "photo", "", "", false);
        User userTest3 = new User("3333", "test3", "photo", "restaurant1", "ChIJexGknqI1sRIRWr8XRhcWfKw", true);
        User userTest4 = new User("4444", "test4", "photo", "restaurant2", "ChIJ5X07y6M1sRIRNiPZimTgF-4", true);

        userList.add(userTest1);
        userList.add(userTest2);
        userList.add(userTest3);
        userList.add(userTest4);

        return userList;
    }
}
