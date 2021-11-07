package com.openclassrooms.p7.go4lunch.manager;

import android.content.Context;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.openclassrooms.p7.go4lunch.model.Restaurant;
import com.openclassrooms.p7.go4lunch.model.User;
import com.openclassrooms.p7.go4lunch.repository.CurrenUserRepository;

import java.util.List;

/**
 * Created by lleotraas on 15.
 */
public class CurrentUserManager {

    private static volatile CurrentUserManager INSTANCE;
    private CurrenUserRepository mCurrenUserRepository;

    private CurrentUserManager() {
        mCurrenUserRepository = CurrenUserRepository.getInstance();
    }

    public static CurrentUserManager getInstance() {
        CurrentUserManager result = INSTANCE;
        if (result != null){
            return result;
        }
        synchronized (CurrenUserRepository.class) {
            if (INSTANCE == null) {
                INSTANCE = new CurrentUserManager();
            }
            return INSTANCE;
        }
    }




}
