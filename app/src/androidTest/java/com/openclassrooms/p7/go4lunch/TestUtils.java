package com.openclassrooms.p7.go4lunch;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.openclassrooms.p7.go4lunch.model.RestaurantFavorite;

import org.junit.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public abstract class TestUtils {

    public static final String USER_EMAIL_TEST = "user@test.fr";
    public static final String USER_PASSWORD_TEST = "password";
    public static final String USER_RESTAURANT_NAME_TEST = "restaurant test";
    public static final String USER_RESTAURANT_ID = "123456";

    public static final String FIRST_RESTAURANT_ID = "ChIJexGknqI1sRIRWr8XRhcWfKw";
    public static final String SECOND_RESTAURANT_ID = "ChIJKb7Sg6E1sRIREV7wSM";
    public static final String THIRD_RESTAURANT_ID = "ChIJ34_PP541sRIR_sG_wtG5EZE";

    public static void signInUser(String userEmail, String userPassword) throws ExecutionException, InterruptedException {
        FirebaseUser currentFirebaseUser = Tasks.await(FirebaseAuth.getInstance().signInWithEmailAndPassword(userEmail, userPassword)).getUser();
        Assert.assertNotNull(currentFirebaseUser);
    }

    public static Task<DocumentSnapshot> getCurrentFirestoreUser(String userID) {
        return FirebaseFirestore.getInstance()
                .collection("users_test")
                .document(userID).get();
    }

    public static void createFirebaseUser(String userEmail, String userPassword) throws ExecutionException, InterruptedException {
        Tasks.await(FirebaseAuth.getInstance().createUserWithEmailAndPassword(userEmail, userPassword));
    }

    public static RestaurantFavorite getDefaultRestaurantFavorite() {
        return new RestaurantFavorite("1111", true);
    }

    public static Task<DocumentSnapshot> getFirestoreRestaurantFavorite(String userId, String restaurantId) {
        return FirebaseFirestore.getInstance()
                .collection("users_test")
                .document(userId)
                .collection("restaurants_test")
                .document(restaurantId)
                .get();
    }
}
