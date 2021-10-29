package com.openclassrooms.p7.go4lunch.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.openclassrooms.p7.go4lunch.databinding.ActivityDetailBinding;
import com.openclassrooms.p7.go4lunch.injector.DI;
import com.openclassrooms.p7.go4lunch.model.Restaurant;
import com.openclassrooms.p7.go4lunch.service.RestaurantApiService;

public class DetailActivity extends AppCompatActivity {

    private ActivityDetailBinding mBinding;
    private Restaurant mRestaurant;
    private RestaurantApiService mApiService;
    private final ImageView[] ratingStarsArray = new ImageView[3];

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

    private void searchRestaurantFromId(Intent mainActivityIntent) {
        String restaurantId = mainActivityIntent.getStringExtra("restaurantId");
        for (int i = 0;i < mApiService.getRestaurant().size();i++) {
            if (restaurantId.equals(mApiService.getRestaurant().get(i).getId())){
                mRestaurant = mApiService.getRestaurant().get(i);
            }
        }
    }

    private void configureView() {
        Glide.with(this)
                .load(mRestaurant.getPictureUrl())
                .centerCrop()
                .into(mBinding.activityDetailImageHeader);
        mBinding.activityDetailRestaurantNameTv.setText(mRestaurant.getName());
        mBinding.activityDetailRestaurantTypeAndAdressTv.setText(String.format("Je sais pas encore - %s", mRestaurant.getAdress()));
        //
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
        mBinding.activityDetailCallBtn.setOnClickListener(view ->{ });
        mBinding.activityDetailLikeBtn.setOnClickListener(view ->{ });
        mBinding.activityDetailWebsiteBtn.setOnClickListener(view ->{ });
    }
}