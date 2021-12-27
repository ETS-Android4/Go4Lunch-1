package com.openclassrooms.p7.go4lunch.ui.fragment.list_view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.libraries.places.api.model.Place;
import com.openclassrooms.p7.go4lunch.R;
import com.openclassrooms.p7.go4lunch.ViewModelFactory;
import com.openclassrooms.p7.go4lunch.injector.DI;
import com.openclassrooms.p7.go4lunch.model.Restaurant;
import com.openclassrooms.p7.go4lunch.service.ApiService;
import com.openclassrooms.p7.go4lunch.ui.MainActivity;
import com.openclassrooms.p7.go4lunch.ui.UserAndRestaurantViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lleotraas on 14.
 */
public class ListViewFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private ListViewAdapter listViewAdapter;
    private UserAndRestaurantViewModel mViewModel;
    private ApiService mApiService;

    public ListViewFragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View root = inflater.inflate(R.layout.fragment_list_view, container, false);
        mRecyclerView = root.findViewById(R.id.list_view_recycler_view);
        this.configureServiceAndViewModel();
        this.initList();
        return root;
    }

    private void configureServiceAndViewModel() {
        mViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(UserAndRestaurantViewModel.class);
        mApiService = DI.getRestaurantApiService();
        mViewModel.getAllInterestedUsers().observe(getViewLifecycleOwner(), users -> {
            mViewModel.setNumberOfFriendInterested(users);
        });
    }

    private void initList() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity().getApplicationContext()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(requireActivity().getApplicationContext(), DividerItemDecoration.VERTICAL));
    }

    @Override
    public void onResume() {
        super.onResume();
        List<Restaurant> restaurants = new ArrayList<>();
        mViewModel.getAllRestaurants().observe(getViewLifecycleOwner(), restaurantList -> {
            restaurants.addAll(restaurantList);
            listViewAdapter = new ListViewAdapter(restaurantList);
            mRecyclerView.setAdapter(listViewAdapter);
            mApiService.listViewComparator(restaurantList);
        });
        mViewModel.getRestaurantListSearched().observe(getViewLifecycleOwner(), restaurantList -> {
            restaurants.add(restaurantList.get(0));
            listViewAdapter = new ListViewAdapter(restaurants);
            mRecyclerView.setAdapter(listViewAdapter);
            mApiService.listViewComparator(restaurants);
        });
    }
}
