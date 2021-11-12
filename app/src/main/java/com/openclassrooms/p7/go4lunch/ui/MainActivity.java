package com.openclassrooms.p7.go4lunch.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
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
import com.openclassrooms.p7.go4lunch.databinding.ActivityMainBinding;
import com.openclassrooms.p7.go4lunch.ui.login.LoginActivity;

public class MainActivity extends AppCompatActivity{

    private TextView email;
    private TextView username;
    private ImageView userPicture;
    public static String CURRENT_USER_ID;
    private ActivityMainBinding mBinding;
    private UserAndRestaurantViewModel mViewModel;

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
        this.initViewModelAndService();
    }

    private void configureViewBinding() {
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        View root = mBinding.getRoot();
        setContentView(root);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.updateHeader();
        this.initLists();
    }

    @Override
    public void onBackPressed() {
        if (mBinding.activityMainDrawerLayout.isDrawerOpen(GravityCompat.START)){
            mBinding.activityMainDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    private void configureToolbar() {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.toolbar, null);
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
        mViewModel = new ViewModelProvider(this).get(UserAndRestaurantViewModel.class);
        if (mViewModel.isCurrentUserLogged()) {
            CURRENT_USER_ID = mViewModel.getCurrentUser().getUid();
        }
    }

    private void configureViewPager() {
        mBinding.activityMainViewpager.setUserInputEnabled(false);
        FragmentManager fragmentManager = getSupportFragmentManager();
        PageAdapter mAdapter = new PageAdapter(fragmentManager, getLifecycle());
        mBinding.activityMainViewpager.setAdapter(mAdapter);
        this.setTabLayoutName();
    }

    private void setTabLayoutName() {
        mBinding.activityMainTabs.addTab(mBinding.activityMainTabs.newTab().setText(getString(R.string.map_view_page)));
        mBinding.activityMainTabs.addTab(mBinding.activityMainTabs.newTab().setText(getString(R.string.list_view_page)));
        mBinding.activityMainTabs.addTab(mBinding.activityMainTabs.newTab().setText(getString(R.string.workmates_page)));
        this.setTabLayoutListener();
    }

    private void setTabLayoutListener() {
        mBinding.activityMainTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mBinding.activityMainViewpager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mBinding.activityMainViewpager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                mBinding.activityMainTabs.selectTab(mBinding.activityMainTabs.getTabAt(position));
            }
        });
    }

    private void configureListeners() {
        mBinding.activityMainNavigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.your_lunch:

                case R.id.settings:

                case R.id.logout:
                    mViewModel.signOut(this).addOnSuccessListener(aVoid -> this.startSignActivity());

                default: return true;
            }

        });
    }

    private void startSignActivity() {
        if (!mViewModel.isCurrentUserLogged()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    private void updateHeader() {
        if (mViewModel.isCurrentUserLogged()) {
            FirebaseUser user = mViewModel.getCurrentUser();
            if (user.getPhotoUrl() != null) {
                setUserPicture(user.getPhotoUrl());
            }
            this.setTextUserData(user);
        }
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

    /**
     * Initialize User List and Favorite Restaurant List
     */
    private void initLists() {
        mViewModel.getUsersDataList();
    }
}