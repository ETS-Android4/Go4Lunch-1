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
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.openclassrooms.p7.go4lunch.R;
import com.openclassrooms.p7.go4lunch.injector.DI;
import com.openclassrooms.p7.go4lunch.manager.CurrentUserManager;
import com.openclassrooms.p7.go4lunch.model.FavoriteRestaurant;
import com.openclassrooms.p7.go4lunch.model.User;
import com.openclassrooms.p7.go4lunch.service.RestaurantApiService;
import com.openclassrooms.p7.go4lunch.ui.login.LoginActivity;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 mViewPager;
    private TabLayout mTabLayout;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private final CurrentUserManager mCurrentUserManager = CurrentUserManager.getInstance();
    private TextView email;
    private TextView username;
    private ImageView userPicture;
    private RestaurantApiService mApiService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mApiService = DI.getRestaurantApiService();
        this.configureNavigationDrawer();
        this.startSignActivity();
        this.configureViewPager();
        this.configureListeners();
        this.updateHeader();
        this.initLists();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateHeader();
        initLists();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
        setTabLayoutName();
    }

    private void setTabLayoutName() {
        mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.map_view_page)));
        mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.list_view_page)));
        mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.workmates_page)));
        setTabLayoutListener();
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
                    mCurrentUserManager.signOut(this).addOnSuccessListener(aVoid -> startSignActivity());

                default: return true;
            }

        });
    }

    // TODO change the startActivityForResult deprecated method
    private void startSignActivity() {
        if (!mCurrentUserManager.isCurrentUserLogged()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    private void updateHeader() {
        if (mCurrentUserManager.isCurrentUserLogged()) {
            FirebaseUser user = mCurrentUserManager.getCurrentUser();

            if (user.getPhotoUrl() != null) {
                setUserPicture(user.getPhotoUrl());
            }
            setTextUserData(user);
        }
    }

    /**
     * Initialize User List and Favorite Restaurant List
     */
    private void initLists() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        mApiService.getUsers().clear();
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            createUserList(documentSnapshot);
                        }
                    }
                });
        db.collection("favorite")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        mApiService.getFavoriteRestaurant().clear();
//                        mApiService.getFavoriteRestaurant().clear();
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            createFavoriteRestaurantList(documentSnapshot);
                        }
                    }
                });
    }

    /**
     * Add user in the User List
     * @param documentSnapshot User
     */
    private void createUserList(QueryDocumentSnapshot documentSnapshot) {
        String photoUrl = Objects.requireNonNull(documentSnapshot.get("photoUrl")).toString();
        String username = Objects.requireNonNull(documentSnapshot.get("userName")).toString();
        String uid = Objects.requireNonNull(documentSnapshot.get("uid")).toString();
        mApiService.getUsers().add(new User(uid, username, photoUrl));
    }

    private void createFavoriteRestaurantList(QueryDocumentSnapshot documentSnapshot) {
        String uid = Objects.requireNonNull(documentSnapshot.get("uid")).toString();
        String restaurantId = Objects.requireNonNull(documentSnapshot.get("restaurantId")).toString();
        String restaurantName = Objects.requireNonNull(documentSnapshot.get("restaurantName")).toString();
        boolean isFavorite = Boolean.parseBoolean(Objects.requireNonNull(documentSnapshot.get("favorite")).toString());
        boolean isSelected = Boolean.parseBoolean(Objects.requireNonNull(documentSnapshot.get("selected")).toString());
        mApiService.addFavoriteRestaurant(new FavoriteRestaurant(
                uid,
                restaurantId,
                restaurantName,
                isFavorite,
                isSelected
        ));
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