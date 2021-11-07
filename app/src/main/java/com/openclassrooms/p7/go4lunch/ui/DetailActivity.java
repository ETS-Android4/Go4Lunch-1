package com.openclassrooms.p7.go4lunch.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.openclassrooms.p7.go4lunch.databinding.ActivityDetailBinding;
import com.openclassrooms.p7.go4lunch.injector.DI;
import com.openclassrooms.p7.go4lunch.model.FavoriteOrSelectedRestaurant;
import com.openclassrooms.p7.go4lunch.model.Restaurant;
import com.openclassrooms.p7.go4lunch.model.User;
import com.openclassrooms.p7.go4lunch.service.RestaurantApiService;

public class DetailActivity extends AppCompatActivity {

    private static final int PERMISSION_CODE = 100;
    private ActivityDetailBinding mBinding;
    private Restaurant mCurrentRestaurant;
    private RestaurantApiService mApiService;
    private UserAndRestaurantViewModel mUserAndRestaurantViewModel;
    private final ImageView[] ratingStarsArray = new ImageView[3];
    private FavoriteOrSelectedRestaurant mCurrentFavoriteOrSelectedRestaurant;
    private String CURRENT_USER_UID;
    private String CURRENT_RESTAURANT_ID;
    private String CURRENT_FAVORITE_RESTAURANT_ID;
    private static int LIKE_BTN_TAG;
    private User mCurrentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.configureViewBinding();
        this.initViewModel();
        this.searchById();
        this.configureView();
        this.initRecyclerView();
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
    }

    private void initViewModel() {
        mUserAndRestaurantViewModel = new ViewModelProvider(this).get(UserAndRestaurantViewModel.class);
        mUserAndRestaurantViewModel.getFavoriteOrSelectedRestaurantList();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void searchById() {
        mApiService = DI.getRestaurantApiService();
        Intent mainActivityIntent = getIntent();
        CURRENT_USER_UID = mUserAndRestaurantViewModel.getCurrentUser().getUid();
        CURRENT_RESTAURANT_ID = mainActivityIntent.getStringExtra("restaurantId");
        this.searchRestaurantById();
        this.searchUserById();
        this.searchFavoriteRestaurantById();
    }

    private void searchRestaurantById() {
        mCurrentRestaurant = mApiService.searchRestaurantById(CURRENT_RESTAURANT_ID);
    }

    private void searchUserById() {
        mCurrentUser = mApiService.searchUserById(CURRENT_USER_UID);
    }

    private void searchFavoriteRestaurantById() {
        //TODO ça fonctionne pas
        mCurrentFavoriteOrSelectedRestaurant = mApiService.searchFavoriteRestaurantById(CURRENT_USER_UID, CURRENT_RESTAURANT_ID);
        if (mCurrentFavoriteOrSelectedRestaurant != null) {

            this.setImageAtStart();
        }
    }

    private void setImageAtStart() {
        if (mCurrentFavoriteOrSelectedRestaurant.isFavorite()) {
            this.setFavoriteImage(true);
        }
        if (mCurrentFavoriteOrSelectedRestaurant.isSelected()) {
            this.setSelectedImage(true);
        }
    }

    private void configureView() {
        Glide.with(this)
                .load(mCurrentRestaurant.getPictureUrl())
                .centerCrop()
                .into(mBinding.activityDetailImageHeader);
        mBinding.activityDetailRestaurantNameTv.setText(mCurrentRestaurant.getName());
        mBinding.activityDetailRestaurantTypeAndAdressTv.setText(mCurrentRestaurant.getAdress());
        for (int index = 0; index < ratingStarsArray.length; index++) {
            ratingStarsArray[index].setImageResource(mApiService.setRatingStars(index, mCurrentRestaurant.getRating()));
        }
    }

    private void initRecyclerView() {
        mBinding.activityDetailRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mBinding.activityDetailRecyclerview.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        DetailActivityAdapter mAdapter = new DetailActivityAdapter(mCurrentRestaurant);
        mBinding.activityDetailRecyclerview.setAdapter(mAdapter);
    }

    private void configureListeners() {
        // Call Restaurant Button
        mBinding.activityDetailCallBtn.setOnClickListener(view -> permissionToCall());
        // Go to the website of the restaurant
        mBinding.activityDetailWebsiteBtn.setOnClickListener(view -> goToWebsite());
        // Make Restaurant as Favorite Button
        mBinding.activityDetailLikeBtn.setOnClickListener(this::setFavoriteOrSelectedRestaurant);
        // Select Restaurant to lunch Button
        mBinding.activityDetailFab.setOnClickListener(this::setFavoriteOrSelectedRestaurant);
    }

    private void permissionToCall() {
        if (ContextCompat.checkSelfPermission(DetailActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DetailActivity.this, new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_CODE);
        }
        callTheRestaurant();
    }
    private void callTheRestaurant() {

        String phoneNumber = mCurrentRestaurant.getPhoneNumber();
        Intent phoneIntent = new Intent(Intent.ACTION_CALL);
        phoneIntent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(phoneIntent);
    }

    private void goToWebsite() {
        String url =  mCurrentRestaurant.getUriWebsite();
        if (!url.equals("")) {
            Intent browse = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browse);
        } else {
            Toast toast = Toast.makeText(this,"No website found",Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    /**
     * Check if the FavoriteRestaurant exist in the DB
     * if exist, update the corresponding boolean
     * if not exist, create it.
     * @param view button id.
     */
    private void setFavoriteOrSelectedRestaurant(View view) {
        int buttonId = view.getId();

            if (buttonId == mBinding.activityDetailFab.getId()){
                FavoriteOrSelectedRestaurant favoriteOrSelectedRestaurantToDeselect =  mApiService.searchSelectedRestaurantToDeselect(CURRENT_USER_UID, CURRENT_RESTAURANT_ID);
                if (favoriteOrSelectedRestaurantToDeselect != null)  {
                    favoriteOrSelectedRestaurantToDeselect.setSelected(false);
                    mUserAndRestaurantViewModel.updateSelectedRestaurant(CURRENT_USER_UID, favoriteOrSelectedRestaurantToDeselect.getRestaurantId(), false);                }
                }
            if (mCurrentFavoriteOrSelectedRestaurant != null) {
                updateFavoriteOrSelectedRestaurant(buttonId);
            } else {
                createFavoriteOrSelectedRestaurant(buttonId);
            }
    }

    private void updateFavoriteOrSelectedRestaurant(int buttonId) {
        if (buttonId == LIKE_BTN_TAG) {
            mUserAndRestaurantViewModel.updateFavoriteRestaurant(CURRENT_USER_UID, CURRENT_RESTAURANT_ID, !mCurrentFavoriteOrSelectedRestaurant.isFavorite());
            mCurrentFavoriteOrSelectedRestaurant.setFavorite(!mCurrentFavoriteOrSelectedRestaurant.isFavorite());
            setFavoriteImage(mCurrentFavoriteOrSelectedRestaurant.isFavorite());
        } else {
            mUserAndRestaurantViewModel.updateSelectedRestaurant(CURRENT_USER_UID, CURRENT_RESTAURANT_ID, !mCurrentFavoriteOrSelectedRestaurant.isSelected());
            mCurrentFavoriteOrSelectedRestaurant.setSelected(!mCurrentFavoriteOrSelectedRestaurant.isSelected());
            setSelectedImage(mCurrentFavoriteOrSelectedRestaurant.isSelected());
        }
    }

    private void createFavoriteOrSelectedRestaurant(int buttonId) {
        boolean favorite = false, selected = false;
        if (buttonId == LIKE_BTN_TAG) {
            favorite = true;
        } else {
            selected = true;
        }
        FavoriteOrSelectedRestaurant favoriteOrSelectedRestaurant = new FavoriteOrSelectedRestaurant(
                CURRENT_USER_UID,
                CURRENT_RESTAURANT_ID,
                mCurrentRestaurant.getName(),
                favorite,
                selected
        );
        mApiService.getFavoriteRestaurant().add(favoriteOrSelectedRestaurant);
        mUserAndRestaurantViewModel.createFavoriteRestaurant(CURRENT_USER_UID, favoriteOrSelectedRestaurant);
        mCurrentFavoriteOrSelectedRestaurant = favoriteOrSelectedRestaurant;
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