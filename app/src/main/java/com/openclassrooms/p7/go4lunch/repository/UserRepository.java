package com.openclassrooms.p7.go4lunch.repository;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.openclassrooms.p7.go4lunch.R;
import com.openclassrooms.p7.go4lunch.dialog.CustomChoiceDialogPopup;
import com.openclassrooms.p7.go4lunch.injector.Go4LunchApplication;
import com.openclassrooms.p7.go4lunch.model.User;
import com.openclassrooms.p7.go4lunch.ui.login.LoginActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserRepository {

    private final MutableLiveData<User> currentUser;
    private final MutableLiveData<List<User>> listOfUser;
    private final MutableLiveData<List<User>> listOfUserInterested;
    private final boolean isUnderTest = Go4LunchApplication.isIsRunningTest();

    private static UserRepository mUserRepository;
    private final FirebaseHelper mFirebaseHelper;

    public static UserRepository getInstance() {
        if (mUserRepository == null) {
            FirebaseHelper firebaseHelper = FirebaseHelper.getInstance();
            mUserRepository = new UserRepository(firebaseHelper);
        }
        return mUserRepository;
    }

    public UserRepository(FirebaseHelper firebaseHelper) {
        mFirebaseHelper = firebaseHelper;
        currentUser = new MutableLiveData<>();
        listOfUser = new MutableLiveData<>();
        listOfUserInterested = new MutableLiveData<>();
    }

    /**
     * Create user in Firestore and initialize current firestore user , if user already exist just initialize current firestore user.
     */
    public void createFireStoreUser() {
        FirebaseUser user = mFirebaseHelper.getCurrentUser();
        String photoUrl = "";
        if (user != null && user.getPhotoUrl() != null) {
            photoUrl = user.getPhotoUrl().toString();
        }
        User userToCreate = new User(
                Objects.requireNonNull(user).getUid(),
                user.getDisplayName(),
                photoUrl,
                "",
                "",
                false
        );
        mFirebaseHelper.getCurrentFirestoreUser().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (!task.getResult().exists()) {
                    mFirebaseHelper.getUsersCollection().document(user.getUid()).set(userToCreate);
                    getCurrentFirestoreUser().postValue(userToCreate);
                } else {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    User currentUser = documentSnapshot.toObject(User.class);
                    getCurrentFirestoreUser().postValue(currentUser);
                    Log.d(TAG, "createUser: User exist");
                }
            }
        }).addOnFailureListener(exception -> Log.d(TAG, "createFireStoreUser: Failed to create user: " + exception.getMessage()));
    }

    /**
     * Get the current user livedata.
     * @return Current user livedata.
     */
    public MutableLiveData<User> getCurrentFirestoreUser() {
        return currentUser;
    }

    /**
     * Call to update in firestore the selected restaurant whith restaurant id, name and if selected or not.
     * @param user user contains information.
     */
    public void updateUser(User user) {
        mFirebaseHelper.getUsersCollection().document(user.getUid()).update(
                "restaurantId", user.getRestaurantId(),
                "restaurantName", user.getRestaurantName(),
                "restaurantSelected", user.isRestaurantSelected()
        );
    }

    /**
     * Call a task to delete firestore user collection and favorite restaurant collection,
     * when favorite collection deleted, a new task delete firebase account.
     * @param context of the fragment.
     */
    public void deleteUserAccount(Context context) {
        FirebaseUser currentUser = Objects.requireNonNull(mFirebaseHelper.getCurrentUser());
        mFirebaseHelper.getUsersCollection().document(currentUser.getUid()).delete();
        mFirebaseHelper.getRestaurantFavoriteReferenceForCurrentUser().get().continueWith(task -> {
            List<DocumentSnapshot> documentSnapshotList = task.getResult().getDocuments();
            List<Task<Void>> taskList = new ArrayList<>();
            if (!documentSnapshotList.isEmpty()) {
                for (DocumentSnapshot documentSnapshot : documentSnapshotList) {
                    mFirebaseHelper.getRestaurantFavoriteReferenceForCurrentUser().document(documentSnapshot.getId()).delete();
                }
            }
            return Tasks.whenAllComplete(taskList);
        }).continueWith(task -> {
            if (task.isComplete()) {
                return Tasks.await(deleteUser(context).addOnCompleteListener(task1 -> {
                    if (!isUnderTest) {
                        Toast.makeText(context, context.getResources().getString(R.string.preference_popup_account_deleted), Toast.LENGTH_SHORT).show();
                        context.startActivity(new Intent(context, LoginActivity.class));
                    }
                }));
            } else {
                return false;
            }
        });
    }

    /**
     * Call to verify if the account need to login before deleted.
     * If user need to login, a new alert dialog is open.
     * @param context Context of the fragment.
     */
    public void verifyIfAccountDeletable(Context context) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
        AccessToken facebookToken = AccessToken.getCurrentAccessToken();
        AuthCredential credential = null;
        if (facebookToken != null) {
            credential = FacebookAuthProvider.getCredential(Objects.requireNonNull(AccessToken.getCurrentAccessToken()).getToken());
        }
        if (account != null) {
            credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        }
        Objects.requireNonNull(user).reauthenticate(Objects.requireNonNull(credential)).addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                deleteUserAccount(context);
            } else {
                CustomChoiceDialogPopup customChoiceDialogPopup = new CustomChoiceDialogPopup(context);
                customChoiceDialogPopup.setTitle(context.getResources().getString(R.string.preference_need_to_logout));
                customChoiceDialogPopup.setPositiveBtnText(context.getResources().getString(R.string.preference_logout));
                customChoiceDialogPopup.setNegativeBtnText(context.getResources().getString(R.string.main_activity_signout_confirmation_negative_btn));
                customChoiceDialogPopup.getPositiveBtn().setOnClickListener(view -> signOut(context).addOnCompleteListener(task2 -> context.startActivity(new Intent(context, LoginActivity.class))));
                customChoiceDialogPopup.getNegativeBtn().setOnClickListener(view -> customChoiceDialogPopup.close());
                customChoiceDialogPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                customChoiceDialogPopup.build();
            }
        });
    }

    /**
     * Get list of user from Firestore and initialize a list of user livedata.
     * @return listOfUser livedata.
     */
    public MutableLiveData<List<User>> getAllUsers() {
        mFirebaseHelper.getAllUsers().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<User> users = new ArrayList<>();
                for (DocumentSnapshot document : task.getResult()) {
                    users.add(document.toObject(User.class));
                }
                listOfUser.postValue(users);
            } else {
                Log.d("Error", "Error getting documents: ", task.getException());
            }
        }).addOnFailureListener(exception -> Log.d(TAG, "getAllUsers: ERROR GETTING DOCUMENT"));
        return listOfUser;
    }

    /**
     * Get list of interested user from Firestore and initialize a list of user interested livedata.
     * @return listOfUserInterested livedata.
     */
    public MutableLiveData<List<User>> getAllInterestedUsers() {
        mFirebaseHelper.getUsersCollection().whereEqualTo("restaurantSelected", true).addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.w(TAG, "Listen failed.", error);
                return;
            }
            if (value != null) {
                ArrayList<User> users = new ArrayList<>();
                for (DocumentSnapshot document : value.getDocuments()) {
                    users.add(document.toObject(User.class));
                }
                listOfUserInterested.postValue(users);
            } else {
                Log.d(TAG, "Current data: null");
            }
        });
        return listOfUserInterested;
    }

    /**
     * Used to create a list of interested users for a unique restaurant.
     * @param restaurantId Current restaurant.
     * @param users list of all users interested.
     * @return list of interested users to the restaurant.
     */
    public List<User> getAllInterestedUsersAtCurrentRestaurant(String restaurantId, List<User> users) {
        List<User> userList = new ArrayList<>();
        String userId = Objects.requireNonNull(mFirebaseHelper.getCurrentUser()).getUid();
        for (User user : users) {
            if (
                    user.getRestaurantId().equals(restaurantId)
                            && !user.getUid().equals(userId)
            ) {
                userList.add(user);
            }
        }
        return userList;
    }

    /**
     * Call to get the current firebase user.
     * @return FirebaseHelper call.
     */
    public FirebaseUser getCurrentUser() {
        return mFirebaseHelper.getCurrentUser();
    }

    /**
     * Call to delete the current firebase user.
     * @param context context of the fragment.
     * @return FirebaseHelper call.
     */
    public Task<Void> deleteUser(Context context) {
        return mFirebaseHelper.deleteUser(context);
    }

    /**
     * Call to signOut the current firebase user.
     * @param context context of the fragment.
     * @return FirebaseHelper call.
     */
    public Task<Void> signOut(Context context) {
        return mFirebaseHelper.signOut(context);
    }

    /**
     * Call to verify if the current user is logged.
     * @return FirebaseHelper call.
     */
    public Boolean isCurrentUserLogged() {
        return getCurrentUser() != null;
    }
}
