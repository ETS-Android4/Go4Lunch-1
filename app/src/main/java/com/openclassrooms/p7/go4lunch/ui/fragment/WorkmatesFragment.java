package com.openclassrooms.p7.go4lunch.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.openclassrooms.p7.go4lunch.R;
import com.openclassrooms.p7.go4lunch.manager.CurrentUserManager;
import com.openclassrooms.p7.go4lunch.manager.UsersManager;
import com.openclassrooms.p7.go4lunch.model.User;
import com.openclassrooms.p7.go4lunch.ui.fragment.workmates_adapter.WorkmatesAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lleotraas on 14.
 */
public class WorkmatesFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private final CurrentUserManager mCurrentUserManager = CurrentUserManager.getInstance();
    private List<User> users;
    private WorkmatesAdapter workmatesAdapter;

    public WorkmatesFragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_workmates, container, false);
        users = new ArrayList<>();
        mRecyclerView = root.findViewById(R.id.workmates_fragment_recycler_view);
        this.initList();
        return root;
    }

    private void initList() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity().getApplicationContext()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(requireActivity().getApplicationContext(), DividerItemDecoration.VERTICAL));
        workmatesAdapter = new WorkmatesAdapter(users);
        mRecyclerView.setAdapter(workmatesAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        mCurrentUserManager.getDatabaseReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();

                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    users.add(childSnapshot.getValue(User.class));
                }
                workmatesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
