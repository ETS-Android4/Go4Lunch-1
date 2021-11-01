package com.openclassrooms.p7.go4lunch.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.openclassrooms.p7.go4lunch.R;
import com.openclassrooms.p7.go4lunch.databinding.ActivityDetailBinding;
import com.openclassrooms.p7.go4lunch.injector.DI;
import com.openclassrooms.p7.go4lunch.manager.CurrentUserManager;
import com.openclassrooms.p7.go4lunch.model.FavoriteRestaurant;
import com.openclassrooms.p7.go4lunch.model.Restaurant;
import com.openclassrooms.p7.go4lunch.model.User;
import com.openclassrooms.p7.go4lunch.service.RestaurantApiService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DetailActivity extends AppCompatActivity {

    private ActivityDetailBinding mBinding;
    private Restaurant mRestaurant;
    private RestaurantApiService mApiService;
    private final ImageView[] ratingStarsArray = new ImageView[3];
    private final CurrentUserManager mCurrentUserManager = CurrentUserManager.getInstance();
    private FavoriteRestaurant mFavoriteRestaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.configureViewBinding();
        mApiService = DI.getRestaurantApiService();
        Intent mainActivityIntent = getIntent();
        this.searchRestaurantFromId(mainActivityIntent);
        this.configureView();
        this.configureListeners();
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
                            String uid = Objects.requireNonNull(documentSnapshot.get("uid")).toString();
                            String restaurantId = Objects.requireNonNull(documentSnapshot.get("restaurantId")).toString();
                            String restaurantName = Objects.requireNonNull(documentSnapshot.get("restaurantName")).toString();
                            boolean isFavorite = Boolean.parseBoolean(Objects.requireNonNull(documentSnapshot.get("favorite")).toString());
                            boolean isSelected = Boolean.parseBoolean(Objects.requireNonNull(documentSnapshot.get("isSelected")).toString());
                            mApiService.getFavoriteRestaurant().add(new FavoriteRestaurant(uid, restaurantId, restaurantName, isFavorite, isSelected));
                            if (restaurantId.equals(mRestaurant.getId())) {
                                mBinding.activityDetailLikeImg.setImageResource(R.drawable.baseline_star_rate_black_24);
                            }
                        }
                    }
                });
    }

    private void searchRestaurantFromId(Intent mainActivityIntent) {
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

    private void configureViewBinding() {
        mBinding = ActivityDetailBinding.inflate(getLayoutInflater());
        View view = mBinding.getRoot();
        setContentView(view);
        ratingStarsArray[0] = mBinding.activityDetailFirstRatingImg;
        ratingStarsArray[1] = mBinding.activityDetailSecondRatingImg;
        ratingStarsArray[2] = mBinding.activityDetailThirdRatingImg;
    }

    private void configureListeners() {
        mBinding.activityDetailCallBtn.setOnClickListener(view ->{
            //TODO call btn
            });
        mBinding.activityDetailLikeBtn.setOnClickListener(view ->{
            //TODO change the method logic
             if (mApiService.getFavoriteRestaurant().isEmpty()) {
                mApiService.addFavoriteRestaurant(new FavoriteRestaurant(mCurrentUserManager.getCurrentUser().getUid(), mRestaurant.getId(), mRestaurant.getName(), true, false));
                mCurrentUserManager.createUserFavoriteRestaurantList(mCurrentUserManager.getCurrentUser(), mRestaurant, true, false);
                 mBinding.activityDetailLikeImg.setImageResource(R.drawable.baseline_star_rate_black_24);
            } else {
                for (FavoriteRestaurant favoriteRestaurant : mApiService.getFavoriteRestaurant()) {
                    if (favoriteRestaurant.getRestaurantId().equals(mRestaurant.getId()) && mCurrentUserManager.getCurrentUser().getUid().equals(favoriteRestaurant.getUid())) {
                        mFavoriteRestaurant = favoriteRestaurant;
                    }
                }
                if (mFavoriteRestaurant != null) {
//                    mApiService.deleteFavoriteRestaurant(mFavoriteRestaurant);
//                    mCurrentUserManager.deleteFavoriteRestaurant(mFavoriteRestaurant.getUid() + mFavoriteRestaurant.getRestaurantId());
                    mFavoriteRestaurant = null;
                    mBinding.activityDetailLikeImg.setImageResource(R.drawable.baseline_star_border_24);
                } else {
                    mApiService.addFavoriteRestaurant(new FavoriteRestaurant(mCurrentUserManager.getCurrentUser().getUid(), mRestaurant.getId(), mRestaurant.getName(), true, false));
                    mCurrentUserManager.createUserFavoriteRestaurantList(mCurrentUserManager.getCurrentUser(), mRestaurant, true, false);
                    mBinding.activityDetailLikeImg.setImageResource(R.drawable.baseline_star_rate_black_24);
                }
            }
             mFavoriteRestaurant.setFavorite(!mFavoriteRestaurant.isFavorite());
             mCurrentUserManager.updateFavoriteRestaurant(mFavoriteRestaurant.getUid(), mFavoriteRestaurant.isFavorite());
        });
        mBinding.activityDetailWebsiteBtn.setOnClickListener(view ->{
            //TODO website btn
        });
    }
}