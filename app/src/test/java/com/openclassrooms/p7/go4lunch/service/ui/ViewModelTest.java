package com.openclassrooms.p7.go4lunch.service.ui;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.openclassrooms.p7.go4lunch.model.User;
import com.openclassrooms.p7.go4lunch.repository.MapViewRepository;
import com.openclassrooms.p7.go4lunch.repository.PlaceTask;
import com.openclassrooms.p7.go4lunch.repository.RestaurantFavoriteRepository;
import com.openclassrooms.p7.go4lunch.repository.UserRepository;
import com.openclassrooms.p7.go4lunch.service.LiveDataTestUtils;
import com.openclassrooms.p7.go4lunch.ui.UserAndRestaurantViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RunWith(MockitoJUnitRunner.class)
public class ViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private UserRepository userRepositoryMocked;
    @Mock
    private RestaurantFavoriteRepository restaurantFavoriteRepository;
    @Mock
    private MapViewRepository mapViewRepository;
    @Mock
    private PlaceTask placeTask;
    private UserAndRestaurantViewModel viewModel;

    @Before
    public void setup() {
        viewModel = new UserAndRestaurantViewModel(
                userRepositoryMocked,
                restaurantFavoriteRepository,
                mapViewRepository,
                placeTask
        );
        MutableLiveData<List<User>> listOfUsers = new MutableLiveData<>();
        List<User> userList = getDefaultUser();
        listOfUsers.setValue(userList);

        MutableLiveData<List<User>> listOfInterestedUsers = new MutableLiveData<>();
        List<User> interestedUsers = getDefaultInterestedUsers();
        listOfInterestedUsers.setValue(interestedUsers);

        given(userRepositoryMocked.getAllUsers()).willReturn(listOfUsers);
        given(userRepositoryMocked.getAllInterestedUsers()).willReturn(listOfInterestedUsers);
        given(userRepositoryMocked.getAllInterestedUsersAtCurrentRestaurant("1111", getDefaultUser())).willReturn(getDefaultInterestedUsers());
    }

    @Test
    public void getAllUsersWithSuccess() throws InterruptedException {
        List<User> userListToTest = new ArrayList<>();
        LiveDataTestUtils.observeForTesting(viewModel.getAllUsers(), liveData -> {
            userListToTest.addAll(Objects.requireNonNull(liveData.getValue()));
            assertEquals(userListToTest.size(), 3);
        });
    }

    @Test
    public void getAllInterestedUserWithSuccess() throws InterruptedException {
        List<User> userListToTest = new ArrayList<>();
        LiveDataTestUtils.observeForTesting(viewModel.getAllInterestedUsers(), liveData -> {
            userListToTest.addAll(Objects.requireNonNull(liveData.getValue()));
            assertEquals(2, userListToTest.size());
        });
    }

    @Test
    public void getAllInterestedUser_shouldReturn_2() {
        String restaurantId = "1111";
//        when(userRepositoryMocked.getAllInterestedUsersAtCurrentRestaurant(restaurantId, getDefaultUser())).thenReturn(getDefaultInterestedUsers());
        List<User> userListToTest =  viewModel.getAllInterestedUsersAtCurrentRestaurant("1111", getDefaultUser());
        assertEquals(2, userListToTest.size());
    }

    private List<User> getDefaultUser() {
        List<User> userList = new ArrayList<>();
        User userTest1 = new User("1111", "test1", "photo", "restaurant1", "AAAA", true);
        User userTest2 = new User("2222", "test2", "photo", "restaurant2", "BBBB", false);
        User userTest3 = new User("3333", "test3", "photo", "restaurant1", "AAAA", true);

        userList.add(userTest1);
        userList.add(userTest2);
        userList.add(userTest3);

        return userList;
    }

    private List<User> getDefaultInterestedUsers() {
        List<User> userList = new ArrayList<>();
        User userTest1 = new User("1111", "test1", "photo", "restaurant1", "AAAA", true);
        User userTest3 = new User("3333", "test3", "photo", "restaurant1", "AAAA", true);

        userList.add(userTest1);
        userList.add(userTest3);

        return userList;
    }
}