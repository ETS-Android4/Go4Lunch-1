package com.openclassrooms.p7.go4lunch.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.openclassrooms.p7.go4lunch.R;
import com.openclassrooms.p7.go4lunch.injector.DI;
import com.openclassrooms.p7.go4lunch.model.Restaurant;
import com.openclassrooms.p7.go4lunch.service.RestaurantApiService;
import com.openclassrooms.p7.go4lunch.ui.fragment.listview.adapter.ListViewAdapter;

import java.util.List;
import java.util.Objects;

/**
 * Created by lleotraas on 14.
 */
public class    ListViewFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RestaurantApiService mApiService;

    public ListViewFragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mApiService = DI.getRestaurantApiService();
        View root = inflater.inflate(R.layout.fragment_list_view, container, false);
        mRecyclerView = root.findViewById(R.id.list_view_recycler_view);
        this.initList();
        return root;
    }

    private void initList() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity().getApplicationContext()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(requireActivity().getApplicationContext(), DividerItemDecoration.VERTICAL));
        ListViewAdapter listViewAdapter = new ListViewAdapter(mApiService.getRestaurant());
        mRecyclerView.setAdapter(listViewAdapter);
    }


}
