package com.openclassrooms.p7.go4lunch.manager;

import android.content.Context;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.openclassrooms.p7.go4lunch.repository.CurrenUserRepository;

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

    public FirebaseUser getCurrentUser() {
        return mCurrenUserRepository.getCurrentUser();
    }

    public Boolean isCurrentUserLogged() {
        return (this.getCurrentUser() != null);
    }

    public Task<Void> signOut(Context context) {
        return mCurrenUserRepository.signOut(context);
    }

    public Task<Void> deleteUser(Context context) {
        return mCurrenUserRepository.deleteUser(context);
    }

    public DatabaseReference getDatabaseReference() { return mCurrenUserRepository.getDatabaseReference(); }

    public void createUser(){
        mCurrenUserRepository.createUserInDatabase();
    }
}
