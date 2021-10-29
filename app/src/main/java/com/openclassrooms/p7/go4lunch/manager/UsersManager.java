package com.openclassrooms.p7.go4lunch.manager;

import com.openclassrooms.p7.go4lunch.model.User;
import com.openclassrooms.p7.go4lunch.repository.UsersRepository;

import java.util.List;

public class UsersManager {

    private static volatile UsersManager INSTANCE;
    private UsersRepository mUsersRepository;

    private UsersManager() {
        mUsersRepository = UsersRepository.getInstance();
    }

    public static UsersManager getInstance() {
        UsersManager result = INSTANCE;
        if (result != null){
            return result;
        }
        synchronized (UsersManager.class) {
            if (INSTANCE == null) {
                INSTANCE = new UsersManager();
            }
            return INSTANCE;
        }
    }

}
