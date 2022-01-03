package com.openclassrooms.p7.go4lunch;

import static com.openclassrooms.p7.go4lunch.TestUtils.FIRST_RESTAURANT_ID;
import static com.openclassrooms.p7.go4lunch.TestUtils.SECOND_RESTAURANT_ID;
import static com.openclassrooms.p7.go4lunch.TestUtils.THIRD_RESTAURANT_ID;
import static com.openclassrooms.p7.go4lunch.TestUtils.USER_RESTAURANT_ID;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.runner.AndroidJUnit4;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.openclassrooms.p7.go4lunch.injector.Go4LunchApplication;
import com.openclassrooms.p7.go4lunch.model.Restaurant;
import com.openclassrooms.p7.go4lunch.repository.MapViewRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@RunWith(AndroidJUnit4.class)
public class MapViewRepositoryTest {

    private final MapViewRepository mapViewRepository = MapViewRepository.getInstance();
    private final Context context = Go4LunchApplication.getContext();

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setup() {
        Places.initialize(context, BuildConfig.GMP_KEY, Locale.FRANCE);
    }

    @Test
    public void requestForPlaceDetails_withSuccess() throws InterruptedException {
        List<String> placeId = new ArrayList<>();
//        placeId.add(FIRST_RESTAURANT_ID);
        placeId.add(SECOND_RESTAURANT_ID);
        placeId.add(THIRD_RESTAURANT_ID);
        mapViewRepository.requestForPlaceDetails(placeId, context, false);
        Thread.sleep(500);
        LiveDataTestUtils.observeForTesting(mapViewRepository.getAllRestaurants(), liveData -> {
            Thread.sleep(500);
            List<Restaurant> restaurantList = new ArrayList<>(Objects.requireNonNull(liveData.getValue()));
            assertEquals(restaurantList.size(), 2);
        });
    }
}
