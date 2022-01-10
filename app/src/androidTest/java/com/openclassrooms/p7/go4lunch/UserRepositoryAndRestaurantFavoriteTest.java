package com.openclassrooms.p7.go4lunch;

import static com.openclassrooms.p7.go4lunch.TestUtils.USER_EMAIL_TEST;
import static com.openclassrooms.p7.go4lunch.TestUtils.USER_PASSWORD_TEST;
import static com.openclassrooms.p7.go4lunch.TestUtils.USER_RESTAURANT_ID;
import static com.openclassrooms.p7.go4lunch.TestUtils.USER_RESTAURANT_NAME_TEST;
import static com.openclassrooms.p7.go4lunch.TestUtils.createFirebaseUser;
import static com.openclassrooms.p7.go4lunch.TestUtils.getCurrentFirestoreUser;
import static com.openclassrooms.p7.go4lunch.TestUtils.getDefaultRestaurantFavorite;
import static com.openclassrooms.p7.go4lunch.TestUtils.getFirestoreRestaurantFavorite;
import static com.openclassrooms.p7.go4lunch.TestUtils.signInUser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.runner.AndroidJUnit4;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.openclassrooms.p7.go4lunch.injector.Go4LunchApplication;
import com.openclassrooms.p7.go4lunch.model.RestaurantFavorite;
import com.openclassrooms.p7.go4lunch.model.User;
import com.openclassrooms.p7.go4lunch.repository.FirebaseHelper;
import com.openclassrooms.p7.go4lunch.repository.RestaurantFavoriteRepository;
import com.openclassrooms.p7.go4lunch.repository.UserRepository;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserRepositoryAndRestaurantFavoriteTest {

    private final UserRepository userRepository = UserRepository.getInstance();
    private final RestaurantFavoriteRepository restaurantFavoriteRepository = RestaurantFavoriteRepository.getInstance();
    private final Context context = Go4LunchApplication.getContext();

    private FirebaseUser currentUser;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setup() throws Exception {
        assert FirebaseHelper.USERS_COLLECTION_NAME.equals("users_test");
        signInUser(USER_EMAIL_TEST, USER_PASSWORD_TEST);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Test
    public void testa_createFirestoreUser_WhenUserNotExist_WithSuccess() throws ExecutionException, InterruptedException {
        userRepository.createFireStoreUser();
        Thread.sleep(500);
        DocumentSnapshot documentSnapshot = Tasks.await(getCurrentFirestoreUser(currentUser.getUid()));
        User userExpected = documentSnapshot.toObject(User.class);
        assert Objects.requireNonNull(userExpected).getUid().equals(currentUser.getUid());
    }

    @Test
    public void testb_createFirestoreUser_WhenUserExist_WithSuccess() throws ExecutionException, InterruptedException {
        userRepository.createFireStoreUser();
        DocumentSnapshot documentSnapshot = Tasks.await(getCurrentFirestoreUser(currentUser.getUid()));
        User userExpected = documentSnapshot.toObject(User.class);
        assert Objects.requireNonNull(userExpected).getUid().equals(currentUser.getUid());
    }

    @Test
    public void testc_updateUser_WithSuccess() throws ExecutionException, InterruptedException {
        DocumentSnapshot documentSnapshot = Tasks.await(getCurrentFirestoreUser(currentUser.getUid()));
        User userToTest = documentSnapshot.toObject(User.class);

        assert Objects.requireNonNull(userToTest).getUid().equals(currentUser.getUid());
        assertFalse(userToTest.isRestaurantSelected());
        assert userToTest.getRestaurantName().equals("");
        assert userToTest.getRestaurantId().equals("");

        userToTest.setRestaurantId(USER_RESTAURANT_ID);
        userToTest.setRestaurantName(USER_RESTAURANT_NAME_TEST);
        userToTest.setRestaurantSelected(true);

        userRepository.updateUser(userToTest);
        DocumentSnapshot documentSnapshotExpected = Tasks.await(getCurrentFirestoreUser(currentUser.getUid()));
        User userExpected = documentSnapshotExpected.toObject(User.class);
        assert Objects.requireNonNull(userExpected).getUid().equals(currentUser.getUid());
        assertTrue(userToTest.isRestaurantSelected());
        assert userToTest.getRestaurantName().equals(USER_RESTAURANT_NAME_TEST);
        assert userToTest.getRestaurantId().equals(USER_RESTAURANT_ID);
    }

    @Test
    public void testd_getCurrentFirestoreUser_withSuccess() throws InterruptedException {
        LiveDataTestUtils.observeForTesting(userRepository.getCurrentFirestoreUser(), liveData -> {
            Thread.sleep(500);
            User user = liveData.getValue();
            assert Objects.requireNonNull(user).getUid().equals(currentUser.getUid());
        });
    }

    @Test
    public void teste_createFavoriteRestaurant_withSuccess() throws ExecutionException, InterruptedException {
        RestaurantFavorite restaurantFavoriteExpected = getDefaultRestaurantFavorite();
        restaurantFavoriteRepository.createRestaurantFavorite(restaurantFavoriteExpected);
        DocumentSnapshot documentSnapshot = Tasks.await(getFirestoreRestaurantFavorite(currentUser.getUid(), restaurantFavoriteExpected.getRestaurantId()));
        RestaurantFavorite restaurantFavoriteToTest = documentSnapshot.toObject(RestaurantFavorite.class);
        assert Objects.requireNonNull(restaurantFavoriteExpected.getRestaurantId()).equals(Objects.requireNonNull(restaurantFavoriteToTest).getRestaurantId());
    }

    @Test
    public void testf_getCurrentRestaurantFavorite_shouldReturnCurrentRestaurantFavorite() throws InterruptedException {
        LiveDataTestUtils.observeForTesting(restaurantFavoriteRepository.getCurrentRestaurantFavorite("1111"), liveData -> {
            Thread.sleep(500);
            RestaurantFavorite restaurantFavorite = liveData.getValue();
            assert (Objects.requireNonNull(restaurantFavorite).getRestaurantId().equals("1111"));
        });
    }

    @Test
    public void testg_deleteRestaurantFavorite_withSuccess() throws InterruptedException {
        RestaurantFavorite restaurantFavoriteToDelete = getDefaultRestaurantFavorite();
        restaurantFavoriteRepository.deleteRestaurantFavorite(restaurantFavoriteToDelete);
        LiveDataTestUtils.observeForTesting(restaurantFavoriteRepository.getCurrentRestaurantFavorite(restaurantFavoriteToDelete.getRestaurantId()), liveData -> {
            Thread.sleep(500);
            RestaurantFavorite restaurantFavorite = liveData.getValue();
            assertNull(restaurantFavorite);
        });
    }

    @Test
    public void testh_deleteUserFromFirestore_withSuccess() throws ExecutionException, InterruptedException {
        userRepository.deleteUserFromFirestore(context);
        DocumentSnapshot documentSnapshotExpected = Tasks.await(getCurrentFirestoreUser(currentUser.getUid()));
        User userExpected = documentSnapshotExpected.toObject(User.class);
        assertNull(userExpected);
    }

    @Test
    public void testi_getAllUsers_withSuccess() throws InterruptedException {
        List<User> userList = new ArrayList<>();
        LiveDataTestUtils.observeForTesting(userRepository.getAllUsers(), liveData -> {
            Thread.sleep(500);
            userList.addAll(Objects.requireNonNull(liveData.getValue()));
            assertEquals(userList.size(), 4);
                });
    }

    @Test
    public void testj_getAllInterestedUsers_withSuccess() throws InterruptedException {
        List<User> interestedUserList = new ArrayList<>();
        LiveDataTestUtils.observeForTesting(userRepository.getAllInterestedUsers(), liveData -> {
            Thread.sleep(500);
            interestedUserList.addAll(Objects.requireNonNull(liveData.getValue()));
            assertEquals(interestedUserList.size(), 3);
        });
    }

    @Test
    public void testk_getAllInterestedUsersAtCurrentRestaurant_withSuccess() throws InterruptedException {
        List<User> interestedUserList = new ArrayList<>();
        LiveDataTestUtils.observeForTesting(userRepository.getAllInterestedUsers(), liveData -> {
            Thread.sleep(500);
            interestedUserList.addAll(Objects.requireNonNull(liveData.getValue()));
            assertEquals(interestedUserList.size(), 3);
        });
        List<User> userInterestedAtCurrentRestaurants = userRepository.getAllInterestedUsersAtCurrentRestaurant("1111", interestedUserList);
        assertEquals(userInterestedAtCurrentRestaurants.size(), 2);
    }

    @Test
    public void testl_getCurrentFirebaseUser_withSuccess() {
        FirebaseUser currentUser = userRepository.getCurrentUser();
        assert currentUser.getUid().equals(currentUser.getUid());
    }

    @Test
    public void testm_signOut_withSuccess() throws InterruptedException {
        userRepository.signOut(context);
        Thread.sleep(500);
        FirebaseUser currentUser = userRepository.getCurrentUser();
        assertNull(currentUser);
    }

    @Test
    public void testn_deleteUser_withSuccess() throws InterruptedException, ExecutionException {
        userRepository.deleteUser(context);
        Thread.sleep(500);
        createFirebaseUser(USER_EMAIL_TEST, USER_PASSWORD_TEST);
        FirebaseUser userDeleted = currentUser;
        FirebaseUser newUser = userRepository.getCurrentUser();
        assertNotEquals(userDeleted.getUid(), newUser.getUid());
    }
}
