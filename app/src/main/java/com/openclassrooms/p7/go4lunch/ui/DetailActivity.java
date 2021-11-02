package com.openclassrooms.p7.go4lunch.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.openclassrooms.p7.go4lunch.R;
import com.openclassrooms.p7.go4lunch.databinding.ActivityDetailBinding;
import com.openclassrooms.p7.go4lunch.injector.DI;
import com.openclassrooms.p7.go4lunch.manager.CurrentUserManager;
import com.openclassrooms.p7.go4lunch.model.FavoriteRestaurant;
import com.openclassrooms.p7.go4lunch.model.Restaurant;
import com.openclassrooms.p7.go4lunch.service.RestaurantApiService;

import java.util.Objects;

public class DetailActivity extends AppCompatActivity {

    private ActivityDetailBinding mBinding;
    private Restaurant mRestaurant;
    private RestaurantApiService mApiService;
    private final ImageView[] ratingStarsArray = new ImageView[3];
    private final CurrentUserManager mCurrentUserManager = CurrentUserManager.getInstance();
    private FavoriteRestaurant mFavoriteRestaurant;
    private static int LIKE_BTN_TAG;
    private static int SELECT_BTN_TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.configureViewBinding();
        mApiService = DI.getRestaurantApiService();
        Intent mainActivityIntent = getIntent();
        this.findRestaurantById(mainActivityIntent);
        this.configureView();
        this.configureListeners();
    }

    private void configureViewBinding() {
        mBinding = ActivityDetailBinding.inflate(getLayoutInflater());
        View view = mBinding.getRoot();
        setContentView(view);
        ratingStarsArray[0] = mBinding.activityDetailFirstRatingImg;
        ratingStarsArray[1] = mBinding.activityDetailSecondRatingImg;
        ratingStarsArray[2] = mBinding.activityDetailThirdRatingImg;
        LIKE_BTN_TAG = mBinding.activityDetailLikeBtn.getId();
        SELECT_BTN_TAG = mBinding.activityDetailFab.getId();
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("favorite")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        mApiService.getFavoriteRestaurant().clear();
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            initFavoriteRestaurantList(documentSnapshot);
                        }
                    }
                });
    }

    private void initFavoriteRestaurantList(QueryDocumentSnapshot documentSnapshot) {
        String uid = Objects.requireNonNull(documentSnapshot.get("uid")).toString();
        String restaurantId = Objects.requireNonNull(documentSnapshot.get("restaurantId")).toString();
        String restaurantName = Objects.requireNonNull(documentSnapshot.get("restaurantName")).toString();
        boolean isFavorite = Boolean.parseBoolean(Objects.requireNonNull(documentSnapshot.get("favorite")).toString());
        boolean isSelected = Boolean.parseBoolean(Objects.requireNonNull(documentSnapshot.get("selected")).toString());
        addNewFavoriteRestaurant(uid, restaurantId, restaurantName, isFavorite, isSelected);
    }

    private void addNewFavoriteRestaurant(String uid, String restaurantId, String restaurantName, boolean isFavorite, boolean isSelected) {
        mApiService.getFavoriteRestaurant().add(new FavoriteRestaurant(
                uid,
                restaurantId,
                restaurantName,
                isFavorite,
                isSelected
        ));
        if (restaurantId.equals(mRestaurant.getId()) && isFavorite) {
            setFavoriteImage(true);
        }
        if (restaurantId.equals(mRestaurant.getId()) && isSelected) {
            setSelectedImage(true);
        }
    }

    private void findRestaurantById(Intent mainActivityIntent) {
        String restaurantId = mainActivityIntent.getStringExtra("restaurantId");
        for (Restaurant restaurant : mApiService.getRestaurant()) {
            if (restaurantId.equals(restaurant.getId())){
                mRestaurant = restaurant;
            }
        }
    }

    private void configureView() {
        Glide.with(this)
                .load(mRestaurant.getPictureUrl())
                .centerCrop()
                .into(mBinding.activityDetailImageHeader);
        mBinding.activityDetailRestaurantNameTv.setText(mRestaurant.getName());
        //TODO
        mBinding.activityDetailRestaurantTypeAndAdressTv.setText(String.format("Restaurant - %s", mRestaurant.getAdress()));
        for (int index = 0; index < ratingStarsArray.length; index++) {
            ratingStarsArray[index].setImageResource(mApiService.setRatingStars(index, mRestaurant.getRating()));
        }
    }

    private void configureListeners() {
        // Call Restaurant Button
        mBinding.activityDetailCallBtn.setOnClickListener(view ->{
            //TODO call btn
            });
        // Favorite Button
        mBinding.activityDetailLikeBtn.setOnClickListener(this::setFavoriteOrSelectedRestaurant);
        mBinding.activityDetailWebsiteBtn.setOnClickListener(view ->{
            //TODO website btn
        });
        // Select Restaurant Button
        mBinding.activityDetailFab.setOnClickListener(this::setFavoriteOrSelectedRestaurant);
    }

    /**
     * Check if the FavoriteRestaurant exist in the DB
     * if exist, update the corresponding boolean
     * if not exist, create it.
     * @param view button id.
     */
    private void setFavoriteOrSelectedRestaurant(View view) {
        int buttonId = view.getId();

        for (FavoriteRestaurant favoriteRestaurant : mApiService.getFavoriteRestaurant()) {
            if (favoriteRestaurant.getRestaurantId().equals(mRestaurant.getId()) && favoriteRestaurant.getUid().equals(mCurrentUserManager.getCurrentUser().getUid())) {
                mFavoriteRestaurant = favoriteRestaurant;
            }
            if (buttonId == mBinding.activityDetailFab.getId()){

                if (favoriteRestaurant.isSelected() && !mRestaurant.getId().equals(favoriteRestaurant.getRestaurantId())){
                    favoriteRestaurant.setSelected(false);
                    mCurrentUserManager.updateSelectedRestaurant(mCurrentUserManager.getCurrentUser().getUid() + favoriteRestaurant.getRestaurantId(), false);
                }
            }
        }
            if (mFavoriteRestaurant != null) {
                updateFavoriteRestaurant(buttonId);
            } else {
                createFavoriteRestaurant(buttonId);
            }

    }

    private void updateFavoriteRestaurant(int buttonId) {
        if (buttonId == LIKE_BTN_TAG) {
            mCurrentUserManager.updateFavoriteRestaurant(
                    mCurrentUserManager.getCurrentUser().getUid() + mRestaurant.getId(),
                    !mFavoriteRestaurant.isFavorite()
            );
            mFavoriteRestaurant.setFavorite(!mFavoriteRestaurant.isFavorite());
            setFavoriteImage(mFavoriteRestaurant.isFavorite());
        } else {
            mCurrentUserManager.updateSelectedRestaurant(
                    mCurrentUserManager.getCurrentUser().getUid() + mRestaurant.getId(),
                    !mFavoriteRestaurant.isSelected()
            );
            mFavoriteRestaurant.setSelected(!mFavoriteRestaurant.isSelected());
            setSelectedImage(mFavoriteRestaurant.isSelected());
        }
        mFavoriteRestaurant = null;
    }

    private void createFavoriteRestaurant(int buttonId) {
        boolean favorite = false, selected = false;
        if (buttonId == LIKE_BTN_TAG) {
            favorite = true;
        } else {
            selected = true;
        }
        mCurrentUserManager.createFavoriteRestaurant(
                mCurrentUserManager.getCurrentUser(),
                mRestaurant,
                favorite,
                selected);
        addNewFavoriteRestaurant(
                mCurrentUserManager.getCurrentUser().getUid(),
                mRestaurant.getId(),
                mRestaurant.getName(),
                favorite,
                selected
        );

        setFavoriteImage(favorite);
        setSelectedImage(selected);
    }

    private void setFavoriteImage(boolean favorite) {
        mBinding.activityDetailLikeImg.setImageResource(mApiService.setFavoriteImage(favorite));
    }

    private void setSelectedImage(boolean selected) {
        mBinding.activityDetailFab.setImageResource(mApiService.setSelectedImage(selected));
    }
}