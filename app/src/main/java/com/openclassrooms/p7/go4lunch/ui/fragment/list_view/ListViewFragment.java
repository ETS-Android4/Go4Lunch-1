package com.openclassrooms.p7.go4lunch.ui.fragment.list_view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.openclassrooms.p7.go4lunch.R;
import com.openclassrooms.p7.go4lunch.injector.DI;
import com.openclassrooms.p7.go4lunch.service.RestaurantApiService;
import com.openclassrooms.p7.go4lunch.ui.fragment.list_view.ListViewAdapter;

/**
 * Created by lleotraas on 14.
 */
public class ListViewFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RestaurantApiService mApiService;
    private ListViewAdapter listViewAdapter;

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
        listViewAdapter = new ListViewAdapter(mApiService.getRestaurant());
        mRecyclerView.setAdapter(listViewAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        mRecyclerView.setAdapter(listViewAdapter);
    }
}
