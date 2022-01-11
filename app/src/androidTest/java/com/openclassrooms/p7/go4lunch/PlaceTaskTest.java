package com.openclassrooms.p7.go4lunch;

import static com.openclassrooms.p7.go4lunch.TestUtils.NEARBY_SEARCH_URL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.runner.AndroidJUnit4;

import com.openclassrooms.p7.go4lunch.repository.MapViewRepository;
import com.openclassrooms.p7.go4lunch.repository.PlaceTask;
import com.openclassrooms.p7.go4lunch.repository.RestaurantFavoriteRepository;
import com.openclassrooms.p7.go4lunch.repository.UserRepository;
import com.openclassrooms.p7.go4lunch.ui.UserAndRestaurantViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

@RunWith(AndroidJUnit4.class)
public class PlaceTaskTest {

    private final RestaurantFavoriteRepository restaurantFavoriteRepository = RestaurantFavoriteRepository.getInstance();
    private final MapViewRepository mapViewRepository = MapViewRepository.getInstance();
    private final PlaceTask placeTask = new PlaceTask();
    private UserAndRestaurantViewModel viewModel;
    private final UserRepository userRepository = UserRepository.getInstance();

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
    }

    @Test
    public void getAListOfNearbyRestaurant() throws InterruptedException, ExecutionException {
        assertNull(viewModel.getListOfPlaceId().getValue());
        viewModel.placeTaskExecutor(NEARBY_SEARCH_URL);
        int listOfIdSize = placeTask.get().size();
        LiveDataTestUtils.observeForTesting(viewModel.getListOfPlaceId(), liveData -> {
            assertEquals(listOfIdSize, Objects.requireNonNull(liveData.getValue()).size());
        });
    }
}
