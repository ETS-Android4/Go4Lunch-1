package com.openclassrooms.p7.go4lunch.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.openclassrooms.p7.go4lunch.R;

/**
 * Created by lleotraas on 14.
 */
public class WorkmatesFragment extends Fragment {

    public static WorkmatesFragment newInstance() {
        return new WorkmatesFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workmates, container, false);
        TextView yala = view.findViewById(R.id.workmates_fragment_tv);
        yala.setText("Ici c'est les bro");
        return view;
    }
}
