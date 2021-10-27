package com.openclassrooms.p7.go4lunch.repository;

import android.content.Context;

import androidx.annotation.Nullable;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by lleotraas on 15.
 */
public final class UserRepository {

    private static volatile UserRepository INSTANCE;

    private UserRepository() { }

    public static UserRepository getInstance() {
        UserRepository result = INSTANCE;
        if (result != null) {
            return result;
        }
        synchronized (UserRepository.class) {
            if (INSTANCE == null) {
                INSTANCE = new UserRepository();
            }
            return INSTANCE;
        }
    }

    @Nullable
    public FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public Task<Void> signOut(Context context) {
        return AuthUI.getInstance().signOut(context);
    }

    public Task<Void> deleteUser(Context context) {
        return AuthUI.getInstance().delete(context);
    }
}
