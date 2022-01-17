package com.openclassrooms.p7.go4lunch.ui.fragment.workmates_view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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

import com.openclassrooms.p7.go4lunch.R;
import com.openclassrooms.p7.go4lunch.ViewModelFactory;
import com.openclassrooms.p7.go4lunch.injector.DI;
import com.openclassrooms.p7.go4lunch.model.User;
import com.openclassrooms.p7.go4lunch.service.ApiService;
import com.openclassrooms.p7.go4lunch.ui.UserAndRestaurantViewModel;

import java.util.List;

/**
 * Created by lleotraas on 14.
 */
public class WorkmatesFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private UserAndRestaurantViewModel mViewModel;
    private WorkmatesAdapter workmatesAdapter;
    private ApiService mApiService;

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
        mViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(UserAndRestaurantViewModel.class);
        mApiService = DI.getRestaurantApiService();
    }

    private void initList() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity().getApplicationContext()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(requireActivity().getApplicationContext(), DividerItemDecoration.VERTICAL));
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewModel.getAllUsers().observe(getViewLifecycleOwner(), userList -> {
            workmatesAdapter = new WorkmatesAdapter(userList);
            mRecyclerView.setAdapter(workmatesAdapter);
            mApiService.workmatesViewComparator(userList);
        });
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.preference_toolbar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
}
