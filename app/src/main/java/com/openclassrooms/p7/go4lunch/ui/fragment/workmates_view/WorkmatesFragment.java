package com.openclassrooms.p7.go4lunch.ui.fragment.workmates_view;

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

import com.openclassrooms.p7.go4lunch.R;
import com.openclassrooms.p7.go4lunch.injector.DI;
import com.openclassrooms.p7.go4lunch.model.User;
import com.openclassrooms.p7.go4lunch.service.ApiService;
import com.openclassrooms.p7.go4lunch.ui.UserAndRestaurantViewModel;

import java.util.Collections;
import java.util.Objects;

/**
 * Created by lleotraas on 14.
 */
public class WorkmatesFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private UserAndRestaurantViewModel mViewModel;
    private WorkmatesAdapter workmatesAdapter;

    public WorkmatesFragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_workmates, container, false);
        mRecyclerView = root.findViewById(R.id.workmates_fragment_recycler_view);
        this.configureServiceAndViewModel();
        this.initList();
        return root;
    }

    private void configureServiceAndViewModel() {
        mViewModel = new ViewModelProvider(this).get(UserAndRestaurantViewModel.class);
    }

    private void initList() {
        ApiService apiService = DI.getRestaurantApiService();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity().getApplicationContext()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(requireActivity().getApplicationContext(), DividerItemDecoration.VERTICAL));
        workmatesAdapter = new WorkmatesAdapter();
        mRecyclerView.setAdapter(workmatesAdapter);
        apiService.workmatesViewComparator(mViewModel.getAllUsers().getValue());
        mViewModel.getAllUsers().observe(getViewLifecycleOwner(), workmatesAdapter::submitList);
    }

    @Override
    public void onResume() {
        super.onResume();
        mRecyclerView.setAdapter(workmatesAdapter);
        mViewModel.getAllUsers().observe(getViewLifecycleOwner(), workmatesAdapter::submitList);
    }
}
