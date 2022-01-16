package com.openclassrooms.p7.go4lunch;

import static com.openclassrooms.p7.go4lunch.TestUtils.FIRST_RESTAURANT_ID;
import static com.openclassrooms.p7.go4lunch.TestUtils.SECOND_RESTAURANT_ID;
import static com.openclassrooms.p7.go4lunch.TestUtils.THIRD_RESTAURANT_ID;
import static com.openclassrooms.p7.go4lunch.TestUtils.getDefaultRestaurantFavorite;
import static com.openclassrooms.p7.go4lunch.TestUtils.getDefaultUserList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.runner.AndroidJUnit4;

import com.google.android.libraries.places.api.Places;
import com.openclassrooms.p7.go4lunch.injector.Go4LunchApplication;
import com.openclassrooms.p7.go4lunch.model.Restaurant;
import com.openclassrooms.p7.go4lunch.model.RestaurantFavorite;
import com.openclassrooms.p7.go4lunch.model.User;
import com.openclassrooms.p7.go4lunch.repository.MapViewRepository;
import com.openclassrooms.p7.go4lunch.repository.PlaceTask;
import com.openclassrooms.p7.go4lunch.repository.RestaurantFavoriteRepository;
import com.openclassrooms.p7.go4lunch.repository.UserRepository;
import com.openclassrooms.p7.go4lunch.ui.UserAndRestaurantViewModel;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.List;
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
            viewModel.setNumberOfFriendInterested(getDefaultUserList(), liveData.getValue());

            Restaurant firstRestaurant = viewModel.getCurrentRestaurant(FIRST_RESTAURANT_ID, liveData.getValue());
            Restaurant secondRestaurant = viewModel.getCurrentRestaurant(SECOND_RESTAURANT_ID, liveData.getValue());
            Restaurant thirdRestaurant = viewModel.getCurrentRestaurant(THIRD_RESTAURANT_ID, liveData.getValue());

            Integer twoFriendInterested = 2, oneFriendInterested = 1, noFriendInterested = 0;

            assertEquals(twoFriendInterested, firstRestaurant.getNumberOfFriendInterested());
            assertEquals(oneFriendInterested, secondRestaurant.getNumberOfFriendInterested());
            assertEquals(noFriendInterested, thirdRestaurant.getNumberOfFriendInterested());
        });
    }

    @Test
    public void teste_setRestaurantFavorite_withSuccess() throws InterruptedException {
        LiveDataTestUtils.observeForTesting(viewModel.getAllRestaurants(), liveData -> {
            viewModel.setRestaurantFavorite(getDefaultRestaurantFavorite(), liveData.getValue());

            Restaurant firstRestaurant = viewModel.getCurrentRestaurant(FIRST_RESTAURANT_ID, liveData.getValue());
            Restaurant secondRestaurant = viewModel.getCurrentRestaurant(SECOND_RESTAURANT_ID, liveData.getValue());
            Restaurant thirdRestaurant = viewModel.getCurrentRestaurant(THIRD_RESTAURANT_ID, liveData.getValue());

            assertTrue(firstRestaurant.isFavorite());
            assertTrue(secondRestaurant.isFavorite());
            assertTrue(thirdRestaurant.isFavorite());
        });
    }
}
