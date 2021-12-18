package com.openclassrooms.p7.go4lunch.ui.fragment.list_view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.libraries.places.api.model.Place;
import com.openclassrooms.p7.go4lunch.R;
import com.openclassrooms.p7.go4lunch.injector.DI;
import com.openclassrooms.p7.go4lunch.service.ApiService;
import com.openclassrooms.p7.go4lunch.ui.MainActivity;
import com.openclassrooms.p7.go4lunch.ui.UserAndRestaurantViewModel;

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
        mViewModel = new ViewModelProvider(this).get(UserAndRestaurantViewModel.class);
        mViewModel.setNumberOfFriendInterested(mViewModel.getAllInterestedUsers().getValue());
        mApiService = DI.getRestaurantApiService();
    }

    private void configureListener() {
        ((MainActivity) requireActivity()).setOnDataSelected(new MainActivity.HandleData() {
            @Override
            public void onDataSelect(Place place) {

            }
        });
    }

    private void initList() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity().getApplicationContext()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(requireActivity().getApplicationContext(), DividerItemDecoration.VERTICAL));
        listViewAdapter = new ListViewAdapter();
        mRecyclerView.setAdapter(listViewAdapter);
        mApiService.listViewComparator(mViewModel.getAllRestaurants().getValue());
        mViewModel.getAllRestaurants().observe(getViewLifecycleOwner(), listViewAdapter::submitList);

    }

    @Override
    public void onResume() {
        super.onResume();
        mRecyclerView.setAdapter(listViewAdapter);
        mViewModel.getAllRestaurants().observe(getViewLifecycleOwner(), listViewAdapter::submitList);
    }
}
