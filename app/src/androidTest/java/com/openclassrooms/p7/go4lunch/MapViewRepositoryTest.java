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
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.runner.AndroidJUnit4;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.openclassrooms.p7.go4lunch.injector.Go4LunchApplication;
import com.openclassrooms.p7.go4lunch.model.Restaurant;
import com.openclassrooms.p7.go4lunch.repository.MapViewRepository;
import com.openclassrooms.p7.go4lunch.service.DummyApiService;

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

    private final MapViewRepository mapViewRepository = MapViewRepository.getInstance();
    private final Context context = Go4LunchApplication.getContext();
    @Mock
    private Place place;
    @Mock
    private MapViewRepository mapViewRepositoryMocked;
    @Mock
    private DummyApiService dummyApiService;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setup() {
        Places.initialize(context, BuildConfig.GMP_KEY);
    }

    @Test
    public void testa_requestForPlaceDetails_withSuccess_andGetRestaurantList_shouldReturn3() throws InterruptedException {
        List<String> placeId = new ArrayList<>();
        placeId.add(FIRST_RESTAURANT_ID);
        placeId.add(SECOND_RESTAURANT_ID);
        placeId.add(THIRD_RESTAURANT_ID);
        mapViewRepository.requestForPlaceDetails(placeId, context, false);
        LiveDataTestUtils.observeForTesting(mapViewRepository.getAllRestaurants(), liveData -> {
            Thread.sleep(1000);
            List<Restaurant> restaurantList = new ArrayList<>(Objects.requireNonNull(liveData.getValue()));
            assertEquals(restaurantList.size(), 3);
        });
    }

    @Test
    public void testb_getCurrentRestaurant_shouldReturnRestaurant() throws InterruptedException {
        LiveDataTestUtils.observeForTesting(mapViewRepository.getAllRestaurants(), liveData -> {
            Thread.sleep(500);
            List<Restaurant> restaurantList = new ArrayList<>(Objects.requireNonNull(liveData.getValue()));
            assertEquals(restaurantList.size(), 3);
            Restaurant restaurant = mapViewRepository.getCurrentRestaurant(FIRST_RESTAURANT_ID, restaurantList);
            assert restaurant.getId().equals(FIRST_RESTAURANT_ID);
        });
    }
}
