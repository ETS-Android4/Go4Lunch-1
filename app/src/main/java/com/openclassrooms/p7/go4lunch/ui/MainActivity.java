package com.openclassrooms.p7.go4lunch;

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
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseUser;
import com.openclassrooms.p7.go4lunch.databinding.ActivityMainBinding;
import com.openclassrooms.p7.go4lunch.databinding.NavigationDrawerHeaderBinding;
import com.openclassrooms.p7.go4lunch.manager.UserManager;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding mBinding;
    private NavigationDrawerHeaderBinding mHeaderBinding;
    private final UserManager mUserManager = UserManager.getInstance();
    private TextView email;
    private TextView username;
    private ImageView userPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.ConfigureBinding();
        this.configureNavigationDrawer();
        this.startSignActivity();
        this.configureViewPager();
        this.configureListeners();
        this.updateHeader();
    }

    @Override
    public void onBackPressed() {
        if (mBinding.activityMainDrawerLayout.isDrawerOpen(GravityCompat.START)){
            mBinding.activityMainDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void ConfigureBinding() {
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = mBinding.getRoot();
        setContentView(view);
    }

    private void configureNavigationDrawer() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mBinding.activityMainDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mBinding.activityMainDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        final View parentView = mBinding.activityMainNavigationView.getHeaderView(0);
        email = parentView.findViewById(R.id.header_email_adress_tv);
        username = parentView.findViewById(R.id.header_username_tv);
        userPicture = parentView.findViewById(R.id.header_user_image_img);
    }

    private void configureViewPager() {
        ViewPager pager = findViewById(R.id.activity_main_viewpager);
        pager.setAdapter(new PageAdapter(getSupportFragmentManager()));
        TabLayout tabs = findViewById(R.id.activity_main_tabs);
        tabs.setupWithViewPager(pager);
        tabs.setTabMode(TabLayout.MODE_FIXED);
    }

    private void configureListeners() {
        mBinding.activityMainNavigationView.setNavigationItemSelectedListener(item -> {

            switch (item.getItemId()) {
                case R.id.your_lunch:

                case R.id.settings:

                case R.id.logout:
                    mUserManager.signOut(this).addOnSuccessListener(aVoid -> {
                        startSignActivity();
                    });

                default: return true;
            }

        });
    }

    // TODO change the startActivityForResult deprecated method
    private void startSignActivity() {
        if (!mUserManager.isCurrentUserLogged()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    private void updateHeader() {
        if (mUserManager.isCurrentUserLogged()) {
            FirebaseUser user = mUserManager.getCurrentUser();

            if (user.getPhotoUrl() != null) {
                setUserPicture(user.getPhotoUrl());
            }
            setTextUserData(user);
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
}