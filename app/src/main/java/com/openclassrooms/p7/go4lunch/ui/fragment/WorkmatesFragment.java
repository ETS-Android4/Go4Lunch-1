package com.openclassrooms.p7.go4lunch.ui.fragment;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.openclassrooms.p7.go4lunch.R;
import com.openclassrooms.p7.go4lunch.manager.CurrentUserManager;
import com.openclassrooms.p7.go4lunch.manager.UsersManager;
import com.openclassrooms.p7.go4lunch.model.User;
import com.openclassrooms.p7.go4lunch.ui.fragment.workmates_adapter.WorkmatesAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            users.clear();
                            int index = 0;
                            HashMap<String,Boolean> hashMap = new HashMap<>();
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                String photoUrl = documentSnapshot.get("photoUrl").toString();
                                String username = documentSnapshot.get("userName").toString();
                                String uid = documentSnapshot.get("uid").toString();
                                List<String> favoriteRestaurantList = new ArrayList<>();
                                Object object = Objects.requireNonNull(documentSnapshot.get("favoriteRestaurantId")).hashCode();
                                favoriteRestaurantList.add(object.toString());
                                Log.i(TAG, "pseudoList restoId: " + favoriteRestaurantList.get(0));


                                users.add(new User(uid, username, hashMap, photoUrl));
                                workmatesAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });
    }
}
