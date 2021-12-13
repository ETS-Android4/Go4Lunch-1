package com.openclassrooms.p7.go4lunch.ui;

import static org.junit.Assert.*;

import android.app.Application;
import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.ViewModelProvider;

import com.openclassrooms.p7.go4lunch.model.User;
import com.openclassrooms.p7.go4lunch.repository.UserRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UserAndRestaurantViewModelTest {

    @Mock
    private Context context;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Rule
    public MockitoRule initRule = MockitoJUnit.rule();
    private final Application application = Mockito.mock(Application.class);
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private UserAndRestaurantViewModel userAndRestaurantViewModel;


    @Before
    public void setup() throws Exception {
        User userTest1 = new User("1111","test1", null, null);
        User userTest2 = new User("2222","test2", null, null);
        User userTest3 = new User("3333","test3", null, null);


    }

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void getUserData() {
    }

    @Test
    public void getUserCollection() {
    }

    @Test
    public void getCurrentUser() {
    }

    @Test
    public void deleteFirebaseUser() {
    }

    @Test
    public void isCurrentUserLogged() {
    }

    @Test
    public void signOut() {
    }
}