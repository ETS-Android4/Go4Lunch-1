package com.openclassrooms.p7.go4lunch;

import static com.openclassrooms.p7.go4lunch.TestUtils.NEARBY_SEARCH_URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.runner.AndroidJUnit4;

import com.openclassrooms.p7.go4lunch.repository.PlaceTask;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

@RunWith(AndroidJUnit4.class)
public class PlaceTaskTest {

    private final PlaceTask placeTask = new PlaceTask();

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setup() {

    }

    @Test
    public void getAListOfNearbyRestaurant() throws InterruptedException, ExecutionException {
        assertNull(placeTask.getListOfPlaceId().getValue());
        placeTask.execute(NEARBY_SEARCH_URL);
        int listOfIdSize = placeTask.get().size();
        LiveDataTestUtils.observeForTesting(placeTask.getListOfPlaceId(), liveData -> {
            assertEquals(listOfIdSize, Objects.requireNonNull(liveData.getValue()).size());
        });

    }
}
