package com.openclassrooms.p7.go4lunch.ui.fragment.detail;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.openclassrooms.p7.go4lunch.R;
import com.openclassrooms.p7.go4lunch.ViewModelFactory;
import com.openclassrooms.p7.go4lunch.databinding.FragmentDetailBinding;
import com.openclassrooms.p7.go4lunch.injector.DI;
import com.openclassrooms.p7.go4lunch.model.Restaurant;
import com.openclassrooms.p7.go4lunch.model.RestaurantFavorite;
import com.openclassrooms.p7.go4lunch.model.User;
import com.openclassrooms.p7.go4lunch.service.ApiService;
import com.openclassrooms.p7.go4lunch.ui.UserAndRestaurantViewModel;

import java.util.ArrayList;
import java.util.List;

public class DetailFragment extends Fragment {

    private static final int PERMISSION_CODE = 100;
    public static int LIKE_BTN_TAG;
    private FragmentDetailBinding mBinding;
    private User mCurrentUser;
    private Restaurant mCurrentRestaurant;
    private RestaurantFavorite mCurrentRestaurantFavorite;
    private ApiService mApiService;
    private UserAndRestaurantViewModel mViewModel;
    private final ImageView[] ratingStarsArray = new ImageView[3];
    private DetailActivityAdapter mAdapter;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentDetailBinding.inflate(inflater, container, false);
        View root = mBinding.getRoot();
        this.configureViewBinding();
        this.initServiceAndViewModel();
        this.searchById();
        this.initRecyclerView();
        this.configureListeners();
        return root;
    }

    private void configureViewBinding() {
        ratingStarsArray[0] = mBinding.activityDetailFirstRatingImg;
        ratingStarsArray[1] = mBinding.activityDetailSecondRatingImg;
        ratingStarsArray[2] = mBinding.activityDetailThirdRatingImg;
        LIKE_BTN_TAG = mBinding.activityDetailLikeBtn.getId();
    }

    private void initServiceAndViewModel() {
        mApiService = DI.getRestaurantApiService();
        mViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(UserAndRestaurantViewModel.class);
    }

    private void searchById() {
        Intent mainActivityIntent = requireActivity().getIntent();
        String CURRENT_RESTAURANT_ID = mainActivityIntent.getStringExtra("restaurantId");
        mViewModel.getAllRestaurants().observe(getViewLifecycleOwner(), restaurantList -> {
            mCurrentRestaurant = mViewModel.getCurrentRestaurant(CURRENT_RESTAURANT_ID, restaurantList);
            configureView();
        });
        mViewModel.getCurrentFirestoreUser().observe(getViewLifecycleOwner(), user -> {
            mCurrentUser = user;
            if (mCurrentUser.isRestaurantSelected() && mCurrentUser.getRestaurantId().equals(mCurrentRestaurant.getId())) {
                setSelectedImage(true);
            }
        });
        mViewModel.getCurrentRestaurantFavorite(CURRENT_RESTAURANT_ID).observe(getViewLifecycleOwner(), restaurantFavorite -> {
            mCurrentRestaurantFavorite = null;
            if (restaurantFavorite != null) {
                if (restaurantFavorite.getRestaurantId().equals(CURRENT_RESTAURANT_ID)) {
                    mCurrentRestaurantFavorite = restaurantFavorite;
                }
            }
            setFavoriteImage(mCurrentRestaurantFavorite != null);
        });

    }

    private void configureView() {
        if (mCurrentRestaurant.getPictureUrl() != null) {
            Glide.with(this)
                    .load(mCurrentRestaurant.getPictureUrl())
                    .centerCrop()
                    .into(mBinding.activityDetailImageHeader);
        } else {
            Glide.with(this)
                    .load(R.drawable.no_image)
                    .centerCrop()
                    .into(mBinding.activityDetailImageHeader);
        }
        mBinding.activityDetailRestaurantNameTv.setText(mApiService.formatRestaurantName(mCurrentRestaurant.getName()));
        mBinding.activityDetailRestaurantTypeAndAdressTv.setText(mCurrentRestaurant.getAddress());
        for (int index = 0; index < ratingStarsArray.length; index++) {
            ratingStarsArray[index].setImageResource(mApiService.setRatingStars(index, mCurrentRestaurant.getRating()));
        }
    }

    private void initRecyclerView() {
        mBinding.activityDetailRecyclerview.setLayoutManager(new LinearLayoutManager(requireActivity().getApplicationContext()));
        mBinding.activityDetailRecyclerview.addItemDecoration(new DividerItemDecoration(requireActivity().getApplicationContext(), DividerItemDecoration.VERTICAL));
        List<Restaurant> restaurants = new ArrayList<>();
        mViewModel.getAllRestaurants().observe(getViewLifecycleOwner(), restaurants::addAll);
        mViewModel.getAllInterestedUsers().observe(getViewLifecycleOwner(), users -> {
            List<User> userInterestedList = mViewModel.getAllInterestedUsersAtCurrentRestaurant(mCurrentRestaurant.getId(), users);
            if (userInterestedList.isEmpty()) {
                mBinding.activityDetailNoFriendImg.setVisibility(View.VISIBLE);
                mBinding.activityDetailNoFriendTv.setVisibility(View.VISIBLE);
                mBinding.activityDetailRecyclerview.setVisibility(View.GONE);
                mBinding.activityDetailNoFriendTv.setText(requireContext().getResources().getString(R.string.detail_activity_no_friend_tv));
                if (mCurrentUser.isRestaurantSelected()) {
                    if (mCurrentUser.getRestaurantId().equals(mCurrentRestaurant.getId())) {
                        mBinding.activityDetailNoFriendTv.setText(requireContext().getResources().getString(R.string.push_notification_service_nobody_come));
                    }
                }
            } else {
                mBinding.activityDetailNoFriendImg.setVisibility(View.GONE);
                mBinding.activityDetailNoFriendTv.setVisibility(View.GONE);
                mBinding.activityDetailRecyclerview.setVisibility(View.VISIBLE);
                mViewModel.setNumberOfFriendInterested(users, restaurants);
                mAdapter = new DetailActivityAdapter(userInterestedList);
                mBinding.activityDetailRecyclerview.setAdapter(mAdapter);
            }
        });
    }

    private void configureListeners() {
        // Call Restaurant Button
        mBinding.activityDetailCallBtn.setOnClickListener(view -> permissionToCall());
        // Go to the website of the restaurant
        mBinding.activityDetailWebsiteBtn.setOnClickListener(view -> goToWebsite());
        // Make Restaurant as Favorite Button
        mBinding.activityDetailLikeBtn.setOnClickListener(view -> updateRestaurantFavorite());
        // Select Restaurant to lunch Button
        mBinding.activityDetailFab.setOnClickListener(view -> updateRestaurantSelected());
    }

    private void permissionToCall() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_CODE);
        }
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            callTheRestaurant();
        }
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
            Toast toast = Toast.makeText(requireContext(),"No website found",Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    /**
     * Check if the FavoriteRestaurant exist in the DB
     * if exist, update the corresponding boolean
     * if not exist, create it.
     */
    private void updateRestaurantSelected() {
        if (mCurrentUser.getRestaurantId().equals(mCurrentRestaurant.getId())) {
            mCurrentUser.setRestaurantSelected(false);
            mCurrentUser.setRestaurantName("");
            mCurrentUser.setRestaurantId("");
        } else {
            mCurrentUser.setRestaurantSelected(true);
            mCurrentUser.setRestaurantName(mCurrentRestaurant.getName());
            mCurrentUser.setRestaurantId(mCurrentRestaurant.getId());
        }
        setSelectedImage(mCurrentUser.isRestaurantSelected());
        mViewModel.updateUser(mCurrentUser);
    }

    private void updateRestaurantFavorite() {
        if (mCurrentRestaurantFavorite != null) {
            mViewModel.deleteRestaurantFavorite(mCurrentRestaurantFavorite);
            mCurrentRestaurantFavorite = null;
            setFavoriteImage(false);
        } else {
            mViewModel.createRestaurantFavorite(createRestaurantFavorite());
        }
    }

    private RestaurantFavorite createRestaurantFavorite() {
        setFavoriteImage(true);
        RestaurantFavorite restaurantFavorite = new RestaurantFavorite(
                mCurrentRestaurant.getId()
        );
        mCurrentRestaurantFavorite = restaurantFavorite;
        return restaurantFavorite;
    }

    private void setFavoriteImage(boolean favorite) {
        mBinding.activityDetailLikeBtn.setCompoundDrawablesWithIntrinsicBounds(0, mApiService.setFavoriteImage(favorite), 0, 0);
    }

    private void setSelectedImage(boolean selected) {
        mBinding.activityDetailFab.setImageResource(mApiService.setSelectedImage(selected));
        if (selected) {
            mBinding.activityDetailFab.setColorFilter(ContextCompat.getColor(requireContext(), R.color.map_marker_favorite));
        }
    }
}
