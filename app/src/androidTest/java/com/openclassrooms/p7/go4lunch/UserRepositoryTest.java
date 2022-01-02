package com.openclassrooms.p7.go4lunch;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.openclassrooms.p7.go4lunch.repository.UserRepository;

import org.junit.Rule;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class UserRepositoryTest {

    private final UserRepository userRepository = UserRepository.getInstance();

    @Rule
    InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();


}
