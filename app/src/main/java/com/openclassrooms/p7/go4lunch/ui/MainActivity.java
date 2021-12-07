package com.openclassrooms.p7.go4lunch.ui;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseUser;
import com.openclassrooms.p7.go4lunch.R;
import com.openclassrooms.p7.go4lunch.databinding.ActivityMainBinding;
import com.openclassrooms.p7.go4lunch.injector.DI;
import com.openclassrooms.p7.go4lunch.model.User;
import com.openclassrooms.p7.go4lunch.notification.PushNotificationService;
import com.openclassrooms.p7.go4lunch.service.ApiService;
import com.openclassrooms.p7.go4lunch.ui.fragment.map_view.MapViewFragment;
import com.openclassrooms.p7.go4lunch.ui.fragment.preference.PreferenceFragment;
import com.openclassrooms.p7.go4lunch.ui.login.LoginActivity;

import java.util.Arrays;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    public static final int AUTOCOMPLETE_REQUEST_CODE = 23;
    private TextView email;
    private TextView username;
    private ImageView userPicture;
    public static String CURRENT_USER_ID;
    private ActivityMainBinding mBinding;
    private UserAndRestaurantViewModel mViewModel;
    private HandleData mHandleData;

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
        this.initNotification();

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

    private void configureViewBinding() {
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        View root = mBinding.getRoot();
        setContentView(root);
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

    private void startSignActivity() {
        if (!mViewModel.isCurrentUserLogged()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    private void configureViewPager() {
        mBinding.activityMainViewpager.setUserInputEnabled(false);
        FragmentManager fragmentManager = getSupportFragmentManager();
        PageAdapter mAdapter = new PageAdapter(fragmentManager, getLifecycle());
        mBinding.activityMainViewpager.setAdapter(mAdapter);
        this.setTabLayoutName();
    }

    //TODO refactor this
    private void configureListeners() {
        mBinding.activityMainNavigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.your_lunch:
                    showRestaurantSelected();
                    break;
                case R.id.settings:
                    clearToolbarAndTabs();
                    mBinding.activityMainToolbar.getRoot().setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            configureViewBinding();
                            configureToolbar();
                            configureNavigationDrawer();
                            configureListeners();
                            configureViewPager();
                            updateHeader();
                        }
                    });

                    break;
                case R.id.logout:
                    mViewModel.signOut(this).addOnSuccessListener(aVoid -> this.startSignActivity());
                    break;
            }
            return true;
        });
    }

    private void clearToolbarAndTabs() {
        mBinding.activityMainTabs.setVisibility(View.GONE);
        mBinding.activityMainToolbar.getRoot().setTitle(getString(R.string.main_activity_settings_title));
        mBinding.activityMainToolbar.getRoot().getMenu().clear();
        mBinding.activityMainToolbar.toolbar.setBackgroundColor(getResources().getColor(R.color.teal_700));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.linear_layout, new PreferenceFragment())
                .commit();
        mBinding.activityMainDrawerLayout.closeDrawer(GravityCompat.START);
        mBinding.activityMainDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    private void showRestaurantSelected() {
        AlertDialog.Builder restaurantSelectedPopup = new AlertDialog.Builder(this);
        ApiService apiService = DI.getRestaurantApiService();
        User currentUser = apiService.searchUserById(CURRENT_USER_ID);
        restaurantSelectedPopup
                .setTitle(getString(R.string.main_activity_selected_restaurant_dialog))
                .setMessage(apiService.removeUselessWords(apiService.getCurrentUserSelectedRestaurant(currentUser).getRestaurantName()))
                .show();
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

    private void initNotification() {
        PushNotificationService.periodicTimeRequest(getApplicationContext());
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

    /**
     * Initialize User List and Favorite Restaurant List
     */
    private void initLists() {
        mViewModel.getUsersDataList();
    }

    public void startAutocompleteActivity(MenuItem item) {
        ApiService apiService = DI.getRestaurantApiService();
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY,
                Arrays.asList(Place.Field.ID))
                .setTypeFilter(TypeFilter.ESTABLISHMENT)
                .setLocationBias(apiService.getRectangularBound(MapViewFragment.currentLocation))
                .setCountry("FR")
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MainActivity.AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                mHandleData.onDataSelect(place);
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {

            }
        }
    }

    public void setOnDataSelected(HandleData handleData) {
        mHandleData = handleData;
    }

    public interface HandleData {
        void onDataSelect(Place place);
    }

}