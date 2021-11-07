package com.openclassrooms.p7.go4lunch.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseUser;
import com.openclassrooms.p7.go4lunch.R;
import com.openclassrooms.p7.go4lunch.injector.DI;
import com.openclassrooms.p7.go4lunch.service.RestaurantApiService;
import com.openclassrooms.p7.go4lunch.ui.login.LoginActivity;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 mViewPager;
    private TabLayout mTabLayout;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private UserAndRestaurantViewModel mUserAndRestaurantViewModel;
    private TextView email;
    private TextView username;
    private ImageView userPicture;
    private RestaurantApiService mApiService;
    public static String CURRENT_USER_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mApiService = DI.getRestaurantApiService();
        this.configureNavigationDrawer();
        this.initViewModel();
        this.startSignActivity();
        this.configureViewPager();
        this.configureListeners();
        this.updateHeader();
        CURRENT_USER_ID = mUserAndRestaurantViewModel.getCurrentUser().getUid();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.updateHeader();
        this.initLists();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void initViewModel() {
        mUserAndRestaurantViewModel = new ViewModelProvider(this).get(UserAndRestaurantViewModel.class);

    }

    private void configureNavigationDrawer() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        mDrawerLayout = findViewById(R.id.activity_main_drawer_layout);
        mNavigationView = findViewById(R.id.activity_main_navigation_view);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        final View parentView = mNavigationView.getHeaderView(0);
        email = parentView.findViewById(R.id.header_email_adress_tv);
        username = parentView.findViewById(R.id.header_username_tv);
        userPicture = parentView.findViewById(R.id.header_user_image_img);
    }

    private void configureViewPager() {
        mTabLayout = findViewById(R.id.activity_main_tabs);
        mViewPager = findViewById(R.id.activity_main_viewpager);
        mViewPager.setUserInputEnabled(false);
        FragmentManager fragmentManager = getSupportFragmentManager();
        PageAdapter mAdapter = new PageAdapter(fragmentManager, getLifecycle());
        mViewPager.setAdapter(mAdapter);
        this.setTabLayoutName();
    }

    private void setTabLayoutName() {
        mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.map_view_page)));
        mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.list_view_page)));
        mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.workmates_page)));
        this.setTabLayoutListener();
    }

    private void setTabLayoutListener() {
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                mTabLayout.selectTab(mTabLayout.getTabAt(position));
            }
        });
    }

    private void configureListeners() {
        mNavigationView.setNavigationItemSelectedListener(item -> {

            switch (item.getItemId()) {
                case R.id.your_lunch:

                case R.id.settings:

                case R.id.logout:
                    mUserAndRestaurantViewModel.signOut(this).addOnSuccessListener(aVoid -> this.startSignActivity());

                default: return true;
            }

        });
    }

    // TODO change the startActivityForResult deprecated method
    private void startSignActivity() {
        if (!mUserAndRestaurantViewModel.isCurrentUserLogged()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    private void updateHeader() {
        if (mUserAndRestaurantViewModel.isCurrentUserLogged()) {
            FirebaseUser user = mUserAndRestaurantViewModel.getCurrentUser();

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
        mUserAndRestaurantViewModel.getUsersDataList();
    }
}