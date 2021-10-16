package com.openclassrooms.p7.go4lunch.manager;

import android.content.Context;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.openclassrooms.p7.go4lunch.repository.UserRepository;

/**
 * Created by lleotraas on 15.
 */
public class UserManager {

    private static volatile UserManager INSTANCE;
    private UserRepository mUserRepository;

    private UserManager() {
        mUserRepository = UserRepository.getInstance();
    }

    public static UserManager getInstance() {
        UserManager result = INSTANCE;
        if (result != null){
            return result;
        }
        synchronized (UserRepository.class) {
            if (INSTANCE == null) {
                INSTANCE = new UserManager();
            }
            return INSTANCE;
        }
    }

    public FirebaseUser getCurrentUser() {
        return mUserRepository.getCurrentUser();
    }

    public Boolean isCurrentUserLogged() {
        return (this.getCurrentUser() != null);
    }

    public Task<Void> signOut(Context context) {
        return mUserRepository.signOut(context);
    }

    public Task<Void> deleteUser(Context context) {
        return mUserRepository.deleteUser(context);
    }
}
