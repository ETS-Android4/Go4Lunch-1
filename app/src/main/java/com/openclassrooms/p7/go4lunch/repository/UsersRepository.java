package com.openclassrooms.p7.go4lunch.repository;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public final class UsersRepository {

    private static volatile UsersRepository INSTANCE;

    private UsersRepository(){}

    public static UsersRepository getInstance() {
        UsersRepository result = INSTANCE;
        if (result != null) {
            return result;
        }
        synchronized (UsersRepository.class) {
            if (INSTANCE == null) {
                INSTANCE = new UsersRepository();
            }
            return INSTANCE;
        }
    }
    //TODO initialiser une base de donnée
}
