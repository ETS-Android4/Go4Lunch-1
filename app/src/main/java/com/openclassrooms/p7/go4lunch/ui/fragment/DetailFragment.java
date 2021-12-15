package com.openclassrooms.p7.go4lunch.ui.fragment;

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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.openclassrooms.p7.go4lunch.R;
import com.openclassrooms.p7.go4lunch.databinding.FragmentDetailBinding;
import com.openclassrooms.p7.go4lunch.injector.DI;
import com.openclassrooms.p7.go4lunch.model.Restaurant;
import com.openclassrooms.p7.go4lunch.model.User;
import com.openclassrooms.p7.go4lunch.model.UserAndRestaurant;
import com.openclassrooms.p7.go4lunch.service.ApiService;
import com.openclassrooms.p7.go4lunch.ui.DetailActivityAdapter;
import com.openclassrooms.p7.go4lunch.ui.MainActivity;
import com.openclassrooms.p7.go4lunch.ui.UserAndRestaurantViewModel;

import java.util.Objects;

public class DetailFragment extends Fragment {

    private FragmentDetailBinding mBinding;
    private static final int PERMISSION_CODE = 100;
    private Restaurant mCurrentRestaurant;
    private ApiService mApiService;
    private UserAndRestaurantViewModel mViewModel;
    private final ImageView[] ratingStarsArray = new ImageView[3];
    private UserAndRestaurant mCurrentUserAndRestaurant;
    private String CURRENT_USER_UID;
    private String CURRENT_RESTAURANT_ID;
    public static int LIKE_BTN_TAG;
    private User mCurrentUser;
    private DetailActivityAdapter mAdapter;
    private Observer<User> userObserver;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentDetailBinding.inflate(inflater, container, false);
        View root = mBinding.getRoot();
        this.configureViewBinding();
        this.initServiceAndViewModel();
        this.searchById();
        this.configureView();
        this.initRecyclerView();
        this.configureListeners();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewModel.getUser().observe(this, userObserver);
    }

    private void configureViewBinding() {
        ratingStarsArray[0] = mBinding.activityDetailFirstRatingImg;
        ratingStarsArray[1] = mBinding.activityDetailSecondRatingImg;
        ratingStarsArray[2] = mBinding.activityDetailThirdRatingImg;
        LIKE_BTN_TAG = mBinding.activityDetailLikeBtn.getId();
    }

    private void initServiceAndViewModel() {
        mApiService = DI.getRestaurantApiService();
        mViewModel = new ViewModelProvider(this).get(UserAndRestaurantViewModel.class);
        userObserver = new Observer<User>() {
            @Override
            public void onChanged(User user) {
                mCurrentUser = user;
            }
        };
    }

    private void searchById() {
        Intent mainActivityIntent = requireActivity().getIntent();
        CURRENT_USER_UID = MainActivity.CURRENT_USER_ID;
        CURRENT_RESTAURANT_ID = mainActivityIntent.getStringExtra("restaurantId");
        this.searchRestaurantById();
        this.searchUserById();
        this.searchFavoriteRestaurantById();
    }

    private void searchRestaurantById() { mCurrentRestaurant = mViewModel.getCurrentRestaurant(CURRENT_RESTAURANT_ID); }

    private void searchUserById() {
        String currentUserId = mViewModel.getCurrentUser().getUid();
        mViewModel.getUser().setValue(mViewModel.getCurrentFirestoreUser(currentUserId));
//        mCurrentUser = mViewModel.getCurrentFirestoreUser(currentUserId);
    }

    private void searchFavoriteRestaurantById() {
        if (mCurrentUser.getRestaurantDataMap() != null) {
            mCurrentUserAndRestaurant = mCurrentUser.getRestaurantDataMap().get(CURRENT_RESTAURANT_ID);
            if (mCurrentUserAndRestaurant != null) {
                this.setImageAtStart();
            }
        }
    }

    private void setImageAtStart() {
        if (mCurrentUserAndRestaurant.isFavorite()) {
            this.setFavoriteImage(true);
        }
        if (mCurrentUserAndRestaurant.isSelected()) {
            this.setSelectedImage(true);
        }
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
        mBinding.activityDetailRestaurantNameTv.setText(mApiService.removeUselessWords(mCurrentRestaurant.getName()));
        mBinding.activityDetailRestaurantTypeAndAdressTv.setText(mCurrentRestaurant.getAdress());
        for (int index = 0; index < ratingStarsArray.length; index++) {
            ratingStarsArray[index].setImageResource(mApiService.setRatingStars(index, mCurrentRestaurant.getRating()));
        }
    }

    private void initRecyclerView() {
        mBinding.activityDetailRecyclerview.setLayoutManager(new LinearLayoutManager(requireActivity().getApplicationContext()));
        mBinding.activityDetailRecyclerview.addItemDecoration(new DividerItemDecoration(requireActivity().getApplicationContext(), DividerItemDecoration.VERTICAL));
        mAdapter = new DetailActivityAdapter();
        mBinding.activityDetailRecyclerview.setAdapter(mAdapter);
        mViewModel.getAllInterestedUsers().observe(getViewLifecycleOwner(), mAdapter::submitList);
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
     * @param view button id.
     */
    private void setFavoriteOrSelectedRestaurant(View view) {
        int buttonId = view.getId();
        if (mCurrentUserAndRestaurant != null) {
            if (buttonId == mBinding.activityDetailFab.getId()){
                UserAndRestaurant userAndRestaurantToUpdate = mApiService.searchSelectedRestaurant(mCurrentUser);
                if (userAndRestaurantToUpdate != null) {
                    if (!userAndRestaurantToUpdate.getRestaurantId().equals(mCurrentUserAndRestaurant.getRestaurantId())) {
                        Objects.requireNonNull(mCurrentUser.getRestaurantDataMap().get(userAndRestaurantToUpdate.getRestaurantId())).setSelected(false);
                    }
                }
            }
            updateFavoriteOrSelectedRestaurant(buttonId);
        } else {
            createFavoriteOrSelectedRestaurant(buttonId);
        }
    }

    private void updateFavoriteOrSelectedRestaurant(int buttonId) {
        if (buttonId == LIKE_BTN_TAG) {
            setFavoriteImage(!mCurrentUserAndRestaurant.isFavorite());
            mCurrentUserAndRestaurant.setFavorite(!mCurrentUserAndRestaurant.isFavorite());
        } else {
            mCurrentUserAndRestaurant.setSelected(!mCurrentUserAndRestaurant.isSelected());
            setSelectedImage(mCurrentUserAndRestaurant.isSelected());
        }
//        mCurrentUser.getRestaurantDataMap().put(mCurrentRestaurant.getId(), mCurrentUserAndRestaurant);
        mViewModel.updateUser(CURRENT_USER_UID, mCurrentUser.getRestaurantDataMap());
    }

    private void createFavoriteOrSelectedRestaurant(int buttonId) {
        boolean favorite = false, selected = false;
        if (buttonId == LIKE_BTN_TAG) {
            favorite = true;
        } else {
            selected = true;
        }
        UserAndRestaurant userAndRestaurant = new UserAndRestaurant(
                CURRENT_RESTAURANT_ID,
                mCurrentRestaurant.getName(),
                favorite,
                selected
        );
        mCurrentUser.getRestaurantDataMap().put(CURRENT_RESTAURANT_ID, userAndRestaurant);
        mViewModel.updateUser(CURRENT_USER_UID, mCurrentUser.getRestaurantDataMap());
        mCurrentUserAndRestaurant = userAndRestaurant;
        setFavoriteImage(favorite);
        setSelectedImage(selected);
    }

    private void setFavoriteImage(boolean favorite) {
        mBinding.activityDetailLikeImg.setImageResource(mApiService.setFavoriteImage(favorite));
    }

    private void setSelectedImage(boolean selected) {
        mBinding.activityDetailFab.setImageResource(mApiService.setSelectedImage(selected));
        if (selected) {
            mBinding.activityDetailFab.setColorFilter(ContextCompat.getColor(requireContext(), R.color.map_marker_favorite));
        }
    }
}
