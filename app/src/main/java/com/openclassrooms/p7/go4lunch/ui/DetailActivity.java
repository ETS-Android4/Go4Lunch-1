package com.openclassrooms.p7.go4lunch.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.openclassrooms.p7.go4lunch.R;
import com.openclassrooms.p7.go4lunch.databinding.ActivityDetailBinding;
import com.openclassrooms.p7.go4lunch.injector.DI;
import com.openclassrooms.p7.go4lunch.model.Restaurant;
import com.openclassrooms.p7.go4lunch.service.RestaurantApiService;
import com.openclassrooms.p7.go4lunch.ui.fragment.listview.adapter.ListViewAdapter;

import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private ActivityDetailBinding mBinding;
    private Restaurant mRestaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.configureViewBinding();
        RestaurantApiService mApiService = DI.getRestaurantApiService();
        Intent mainActivityIntent = getIntent();
        this.searchRestaurantFromId(mApiService.getRestaurant(), mainActivityIntent);
        this.configureView();
    }

    private void searchRestaurantFromId(List<Restaurant> restaurant, Intent mainActivityIntent) {

        String restaurantId = mainActivityIntent.getStringExtra("restaurantId");
        for (int i = 0;i < restaurant.size();i++) {
            if (restaurantId.equals(restaurant.get(i).getId())){
                mRestaurant = restaurant.get(i);
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
    }

    private void configureViewBinding() {
        mBinding = ActivityDetailBinding.inflate(getLayoutInflater());
        View view = mBinding.getRoot();
        setContentView(view);
    }
}