package com.openclassrooms.p7.go4lunch.ui;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.openclassrooms.p7.go4lunch.LiveDataTestUtils;
import com.openclassrooms.p7.go4lunch.model.User;
import com.openclassrooms.p7.go4lunch.repository.FirebaseHelper;
import com.openclassrooms.p7.go4lunch.repository.MapViewRepository;
import com.openclassrooms.p7.go4lunch.repository.RestaurantDataRepository;
import com.openclassrooms.p7.go4lunch.repository.UserRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class ViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private UserRepository userRepository;
    @Mock
    private RestaurantDataRepository restaurantDataRepository;
    @Mock
    private MapViewRepository mapViewRepository;

    private UserAndRestaurantViewModel viewModel;

    @Before
    public void setup() {
        MutableLiveData<List<User>> listMutableLiveData = new MutableLiveData<>();
        List<User> userList = getDefaultUser();
        listMutableLiveData.setValue(userList);

        given(userRepository.getAllUsers()  ).willReturn(listMutableLiveData);

        viewModel = new UserAndRestaurantViewModel(userRepository, restaurantDataRepository, mapViewRepository);
    }

    @Test
    public void getAllUsersWithSuccess() {
        List<User> userList = new ArrayList<>();
        LiveDataTestUtils.observeForTesting(viewModel.getAllUsers(), new LiveDataTestUtils.OnObservedListener<List<User>>() {
            @Override
            public void onObserved(LiveData<List<User>> liveData) {
                userList.addAll(liveData.getValue());
            }
        });

        assertEquals(userList.size(), 3);
    }

    @Test
    public void getAllInterestedUserWithSuccess() {
        MutableLiveData<List<User>> listMutableLiveData = new MutableLiveData<>();
        listMutableLiveData.setValue(getDefaultUser());
        given((userRepository.getAllInterestedUsers())).willReturn(listMutableLiveData);
        List<User> userList = new ArrayList<>();
        LiveDataTestUtils.observeForTesting(viewModel.getAllInterestedUsers(), liveData -> {
            userList.addAll(liveData.getValue());

        });
        Mockito.verify(userRepository, times(1)).getAllInterestedUsers();
        assertEquals(userList.size(), 2);
    }

    private List<User> getDefaultUser() {
        List<User> userList = new ArrayList<>();
        User userTest1 = new User("1111", "test1", "photo", "restaurant1", "AAAA", true);
        User userTest2 = new User("2222", "test2", "photo", "restaurant2", "BBBB", false);
        User userTest3 = new User("3333", "test3", "photo", "restaurant3", "CCCC", true);

        userList.add(userTest1);
        userList.add(userTest2);
        userList.add(userTest3);

        return userList;
    }


}