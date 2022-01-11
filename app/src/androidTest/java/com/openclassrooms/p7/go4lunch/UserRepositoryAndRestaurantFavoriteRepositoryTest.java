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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.runner.AndroidJUnit4;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.openclassrooms.p7.go4lunch.LiveDataTestUtils;
import com.openclassrooms.p7.go4lunch.injector.Go4LunchApplication;
import com.openclassrooms.p7.go4lunch.model.RestaurantFavorite;
import com.openclassrooms.p7.go4lunch.model.User;
import com.openclassrooms.p7.go4lunch.repository.FirebaseHelper;
import com.openclassrooms.p7.go4lunch.repository.MapViewRepository;
import com.openclassrooms.p7.go4lunch.repository.PlaceTask;
import com.openclassrooms.p7.go4lunch.repository.RestaurantFavoriteRepository;
import com.openclassrooms.p7.go4lunch.repository.UserRepository;
import com.openclassrooms.p7.go4lunch.ui.UserAndRestaurantViewModel;

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
public class UserRepositoryAndRestaurantFavoriteRepositoryTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private final RestaurantFavoriteRepository restaurantFavoriteRepository = RestaurantFavoriteRepository.getInstance();
    private final MapViewRepository mapViewRepository = MapViewRepository.getInstance();
    private final PlaceTask placeTask = new PlaceTask();
    private UserAndRestaurantViewModel viewModel;
    private final UserRepository userRepository = UserRepository.getInstance();
    private final Context context = Go4LunchApplication.getContext();
    private FirebaseUser currentUser;

    @Before
    public void setup() throws ExecutionException, InterruptedException {
        viewModel = new UserAndRestaurantViewModel(
                userRepository,
                restaurantFavoriteRepository,
                mapViewRepository,
                placeTask
        );
        assert FirebaseHelper.USERS_COLLECTION_NAME.equals("users_test");
        signInUser(USER_EMAIL_TEST, USER_PASSWORD_TEST);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Test
    public void testa_isCurrentUserLogged_withSuccess() {
        assertTrue(viewModel.isCurrentUserLogged());
    }

    @Test
    public void testb_createFirestoreUser_WhenUserNotExist_WithSuccess() throws InterruptedException, ExecutionException {
        viewModel.createUser();
        Thread.sleep(500);
        DocumentSnapshot documentSnapshot = Tasks.await(getCurrentFirestoreUser(currentUser.getUid()));
        User userExpected = documentSnapshot.toObject(User.class);
        assert Objects.requireNonNull(userExpected).getUid().equals(currentUser.getUid());
    }

    @Test
    public void testc_createFirestoreUser_WhenUserExist_WithSuccess() throws InterruptedException, ExecutionException {
        viewModel.createUser();
        DocumentSnapshot documentSnapshot = Tasks.await(getCurrentFirestoreUser(currentUser.getUid()));
        User userExpected = documentSnapshot.toObject(User.class);
        assert Objects.requireNonNull(userExpected).getUid().equals(currentUser.getUid());
    }

    @Test
    public void testd_updateUser_WithSuccess() throws ExecutionException, InterruptedException {
        DocumentSnapshot documentSnapshot = Tasks.await(getCurrentFirestoreUser(currentUser.getUid()));
        User userToTest = documentSnapshot.toObject(User.class);

        assert Objects.requireNonNull(userToTest).getUid().equals(currentUser.getUid());
        assertFalse(userToTest.isRestaurantSelected());
        assert userToTest.getRestaurantName().equals("");
        assert userToTest.getRestaurantId().equals("");

        userToTest.setRestaurantId(USER_RESTAURANT_ID);
        userToTest.setRestaurantName(USER_RESTAURANT_NAME_TEST);
        userToTest.setRestaurantSelected(true);

        viewModel.updateUser(userToTest);
        DocumentSnapshot documentSnapshotExpected = Tasks.await(getCurrentFirestoreUser(currentUser.getUid()));
        User userExpected = documentSnapshotExpected.toObject(User.class);
        assert Objects.requireNonNull(userExpected).getUid().equals(currentUser.getUid());
        assertTrue(userToTest.isRestaurantSelected());
        assert userToTest.getRestaurantName().equals(USER_RESTAURANT_NAME_TEST);
        assert userToTest.getRestaurantId().equals(USER_RESTAURANT_ID);
    }

    @Test
    public void teste_getCurrentFirestoreUser_withSuccess() throws InterruptedException {
        LiveDataTestUtils.observeForTesting(viewModel.getCurrentFirestoreUser(), liveData -> {
            Thread.sleep(500);
            User user = liveData.getValue();
            assert Objects.requireNonNull(user).getUid().equals(currentUser.getUid());
        });
    }

    @Test
    public void testf_createFavoriteRestaurant_withSuccess() throws ExecutionException, InterruptedException {
        RestaurantFavorite restaurantFavoriteExpected = getDefaultRestaurantFavorite(0);
        viewModel.createRestaurantFavorite(restaurantFavoriteExpected);
        DocumentSnapshot documentSnapshot = Tasks.await(getFirestoreRestaurantFavorite(currentUser.getUid(), restaurantFavoriteExpected.getRestaurantId()));
        RestaurantFavorite restaurantFavoriteToTest = documentSnapshot.toObject(RestaurantFavorite.class);
        assert Objects.requireNonNull(restaurantFavoriteExpected.getRestaurantId()).equals(Objects.requireNonNull(restaurantFavoriteToTest).getRestaurantId());
        viewModel.createRestaurantFavorite(getDefaultRestaurantFavorite(1));
        viewModel.createRestaurantFavorite(getDefaultRestaurantFavorite(2));
    }

    @Test
    public void testg_getCurrentRestaurantFavorite_shouldReturnCurrentRestaurantFavorite() throws InterruptedException {
        LiveDataTestUtils.observeForTesting(viewModel.getCurrentRestaurantFavorite("1111"), liveData -> {
            Thread.sleep(500);
            RestaurantFavorite restaurantFavorite = liveData.getValue();
            assert (Objects.requireNonNull(restaurantFavorite).getRestaurantId().equals("1111"));
        });
    }

    @Test
    public void testh_deleteRestaurantFavorite_withSuccess() throws InterruptedException {
        RestaurantFavorite restaurantFavoriteToDelete = getDefaultRestaurantFavorite(0);
        viewModel.deleteRestaurantFavorite(restaurantFavoriteToDelete);
        LiveDataTestUtils.observeForTesting(viewModel.getCurrentRestaurantFavorite(restaurantFavoriteToDelete.getRestaurantId()), liveData -> {
            Thread.sleep(500);
            RestaurantFavorite restaurantFavorite = liveData.getValue();
            assertNull(restaurantFavorite);
        });
    }

    @Test
    public void testi_deleteUser_withSuccess() throws ExecutionException, InterruptedException {
        viewModel.deleteUser(context);
        DocumentSnapshot documentSnapshotExpected = Tasks.await(getCurrentFirestoreUser(currentUser.getUid()));
        User userExpected = documentSnapshotExpected.toObject(User.class);
        assertNull(userExpected);
        Thread.sleep(500);
        createFirebaseUser(USER_EMAIL_TEST, USER_PASSWORD_TEST);
        FirebaseUser userDeleted = currentUser;
        FirebaseUser newUser = viewModel.getCurrentFirebaseUser();
        assertNotEquals(userDeleted.getUid(), newUser.getUid());
    }

    @Test
    public void testj_getAllUsers_withSuccess() throws InterruptedException {
        List<User> userList = new ArrayList<>();
        LiveDataTestUtils.observeForTesting(viewModel.getAllUsers(), liveData -> {
            Thread.sleep(500);
            userList.addAll(Objects.requireNonNull(liveData.getValue()));
            assertEquals(userList.size(), 4);
        });
    }

    @Test
    public void testk_getAllInterestedUsers_withSuccess() throws InterruptedException {
        List<User> interestedUserList = new ArrayList<>();
        LiveDataTestUtils.observeForTesting(viewModel.getAllInterestedUsers(), liveData -> {
            Thread.sleep(500);
            interestedUserList.addAll(Objects.requireNonNull(liveData.getValue()));
            assertEquals(interestedUserList.size(), 3);
        });
    }

    @Test
    public void testl_getAllInterestedUsersAtCurrentRestaurant_withSuccess() throws InterruptedException {
        List<User> interestedUserList = new ArrayList<>();
        LiveDataTestUtils.observeForTesting(viewModel.getAllInterestedUsers(), liveData -> {
            Thread.sleep(500);
            interestedUserList.addAll(Objects.requireNonNull(liveData.getValue()));
            assertEquals(interestedUserList.size(), 3);
        });
        List<User> userInterestedAtCurrentRestaurants = viewModel.getAllInterestedUsersAtCurrentRestaurant("1111", interestedUserList);
        assertEquals(userInterestedAtCurrentRestaurants.size(), 2);
    }

    @Test
    public void testm_getCurrentFirebaseUser_withSuccess() {
        FirebaseUser currentUser = viewModel.getCurrentFirebaseUser();
        assert currentUser.getUid().equals(currentUser.getUid());
    }

    @Test
    public void testn_signOut_withSuccess() throws InterruptedException {
        viewModel.signOut(context);
        Thread.sleep(500);
        FirebaseUser currentUser = viewModel.getCurrentFirebaseUser();
        assertNull(currentUser);
    }

    @Test
    public void testo_deleteUser_withSuccess() throws InterruptedException, ExecutionException {
        viewModel.deleteFirebaseUser(context);
        Thread.sleep(500);
        createFirebaseUser(USER_EMAIL_TEST, USER_PASSWORD_TEST);
        FirebaseUser userDeleted = currentUser;
        FirebaseUser newUser = viewModel.getCurrentFirebaseUser();
        assertNotEquals(userDeleted.getUid(), newUser.getUid());
    }


}