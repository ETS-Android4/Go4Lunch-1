package com.openclassrooms.p7.go4lunch.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseUser;
import com.openclassrooms.p7.go4lunch.R;
import com.openclassrooms.p7.go4lunch.ViewModelFactory;
import com.openclassrooms.p7.go4lunch.databinding.ActivityMainBinding;
import com.openclassrooms.p7.go4lunch.dialog.CustomChoiceDialogPopup;
import com.openclassrooms.p7.go4lunch.dialog.RestaurantChoiceDialogPopup;
import com.openclassrooms.p7.go4lunch.injector.DI;
import com.openclassrooms.p7.go4lunch.model.User;
import com.openclassrooms.p7.go4lunch.service.ApiService;
import com.openclassrooms.p7.go4lunch.ui.PageAdapter;
import com.openclassrooms.p7.go4lunch.ui.UserAndRestaurantViewModel;
import com.openclassrooms.p7.go4lunch.ui.login.LoginActivity;

import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    private TextView email;
    private TextView username;
    private ImageView userPicture;
    private ActivityMainBinding mBinding;
    private UserAndRestaurantViewModel mViewModel;
    private User mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.configureViewBinding();
        this.configureToolbar();
        this.configureNavigationDrawer();
        this.initViewModelAndService();
        this.startSignActivity();
        this.configureViewPager();
        this.configureListeners();
        this.updateHeader();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.updateHeader();
    }

    @Override
    public void onBackPressed() {
        if (mBinding.activityMainDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mBinding.activityMainDrawerLayout.closeDrawer(GravityCompat.START);
        }
        if (
                !mBinding.activityMainDrawerLayout.isDrawerOpen(GravityCompat.START) &&
                Objects.requireNonNull(mBinding.activityMainTabs.getTabAt(0)).isSelected()
        ) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.main_activity_alert_dialog_title)
                    .setPositiveButton(R.string.main_activity_signout_confirmation_positive_btn, (dialog, which) -> this.finishAffinity()).setNegativeButton(R.string.main_activity_signout_confirmation_negative_btn, null)
                    .show();
        }
        if (Objects.requireNonNull(mBinding.activityMainTabs.getTabAt(1)).isSelected()) {
            mBinding.activityMainViewpager.setCurrentItem(0);
            Objects.requireNonNull(Objects.requireNonNull(mBinding.activityMainTabs.getTabAt(0)).getIcon()).setColorFilter(getResources().getColor(R.color.light_icon_color), PorterDuff.Mode.SRC_IN);
        }
        if (Objects.requireNonNull(mBinding.activityMainTabs.getTabAt(2)).isSelected()) {
            mBinding.activityMainViewpager.setCurrentItem(0);
            Objects.requireNonNull(Objects.requireNonNull(mBinding.activityMainTabs.getTabAt(0)).getIcon()).setColorFilter(getResources().getColor(R.color.light_icon_color), PorterDuff.Mode.SRC_IN);
        }
    }

    private void configureViewBinding() {
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        View root = mBinding.getRoot();
        setContentView(root);
    }

    private void configureToolbar() {
        setSupportActionBar(mBinding.activityMainToolbar.toolbar);

    }

    private void configureNavigationDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mBinding.activityMainDrawerLayout, mBinding.activityMainToolbar.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mBinding.activityMainDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        final View parentView = mBinding.activityMainNavigationView.getHeaderView(0);
        email = parentView.findViewById(R.id.header_email_adress_tv);
        username = parentView.findViewById(R.id.header_username_tv);
        userPicture = parentView.findViewById(R.id.header_user_image_img);
    }

    private void initViewModelAndService() {
        mViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(UserAndRestaurantViewModel.class);
    }

    private void startSignActivity() {
        if (!mViewModel.isCurrentUserLogged()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else {
            mViewModel.createUser();
        }
    }

    private void configureViewPager() {
        mBinding.activityMainViewpager.setUserInputEnabled(false);
        FragmentManager fragmentManager = getSupportFragmentManager();
        PageAdapter mAdapter = new PageAdapter(fragmentManager, getLifecycle());
        mBinding.activityMainViewpager.setAdapter(mAdapter);
        this.setTabLayoutName();
    }

    @SuppressLint("NonConstantResourceId")
    private void configureListeners() {
        mBinding.activityMainNavigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.your_lunch:
                    showRestaurantSelected();
                    break;

                case R.id.settings:
                    startActivity(new Intent(this, PreferenceActivity.class));
                    break;

                case R.id.logout:
                    signOutAlertPopup();
                    break;
            }
            return true;
        });
    }

    private void showRestaurantSelected() {
        ApiService apiService = DI.getRestaurantApiService();
        mViewModel.getCurrentFirestoreUser().observe(this, user -> mCurrentUser = user);
        String restaurantName = apiService.formatRestaurantName(mCurrentUser.getRestaurantName());
        if (restaurantName.equals("")) {
            restaurantName = getString(R.string.main_activity_no_restaurant_selected);
        }
        RestaurantChoiceDialogPopup restaurantChoiceDialogPopup = new RestaurantChoiceDialogPopup(this);
        restaurantChoiceDialogPopup.setTitle(getString(R.string.main_activity_selected_restaurant_dialog));
        restaurantChoiceDialogPopup.setMessage(restaurantName);
        restaurantChoiceDialogPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        restaurantChoiceDialogPopup.build();
    }

    private void signOutAlertPopup() {
        CustomChoiceDialogPopup customChoiceDialogPopup = new CustomChoiceDialogPopup(this);
        customChoiceDialogPopup.setTitle(getString(R.string.main_activity_signout_confirmation_title));
        customChoiceDialogPopup.setPositiveBtnText(getResources().getString(R.string.main_activity_signout_confirmation_positive_btn));
        customChoiceDialogPopup.setNegativeBtnText(getResources().getString(R.string.main_activity_signout_confirmation_negative_btn));
        customChoiceDialogPopup.getPositiveBtn().setOnClickListener(view -> mViewModel.signOut(this).addOnSuccessListener(aVoid -> this.startSignActivity()));
        customChoiceDialogPopup.getNegativeBtn().setOnClickListener(view -> customChoiceDialogPopup.close());
        customChoiceDialogPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        customChoiceDialogPopup.build();
    }

    private void updateHeader() {
        if (mViewModel.isCurrentUserLogged()) {
            FirebaseUser user = mViewModel.getCurrentFirebaseUser();
            if (user.getPhotoUrl() != null) {
                setUserPicture(user.getPhotoUrl());
            }
            this.setTextUserData(user);
        }
    }

    private void setTabLayoutName() {
        mBinding.activityMainTabs.addTab(mBinding.activityMainTabs.newTab().setText(getString(R.string.main_activity_map_view_page)));
        mBinding.activityMainTabs.addTab(mBinding.activityMainTabs.newTab().setText(getString(R.string.main_activity_list_view_page)));
        mBinding.activityMainTabs.addTab(mBinding.activityMainTabs.newTab().setText(getString(R.string.main_activity_workmates_page)));
        Objects.requireNonNull(mBinding.activityMainTabs.getTabAt(0)).setIcon(R.drawable.ic_map);
        Objects.requireNonNull(mBinding.activityMainTabs.getTabAt(1)).setIcon(R.drawable.ic_list);
        Objects.requireNonNull(mBinding.activityMainTabs.getTabAt(2)).setIcon(R.drawable.ic_people_alt);
        Objects.requireNonNull(Objects.requireNonNull(mBinding.activityMainTabs.getTabAt(0)).getIcon()).setColorFilter(getApplicationContext().getResources().getColor(R.color.light_icon_color), PorterDuff.Mode.SRC_IN);
        this.setTabLayoutListener();
    }

    private void setTabLayoutListener() {
        mBinding.activityMainTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mBinding.activityMainViewpager.setCurrentItem(tab.getPosition());
                Objects.requireNonNull(tab.getIcon()).setColorFilter(getResources().getColor(R.color.light_icon_color), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Objects.requireNonNull(tab.getIcon()).setColorFilter(getResources().getColor(R.color.light_text_color), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });

        mBinding.activityMainViewpager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                mBinding.activityMainTabs.selectTab(mBinding.activityMainTabs.getTabAt(position));
            }
        });
    }

    private void setUserPicture(Uri photoUrl) {
        Glide.with(this)
                .load(photoUrl)
                .apply(RequestOptions.circleCropTransform())
                .into(userPicture);
    }

    private void setTextUserData(FirebaseUser user) {
        username.setText(user.getDisplayName());
        email.setText(user.getEmail());
    }

    public static void hideKeyboard(Activity activity, View view) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }
}