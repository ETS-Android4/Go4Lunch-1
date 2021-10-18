package com.openclassrooms.p7.go4lunch.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.openclassrooms.p7.go4lunch.R;

/**
 * Created by lleotraas on 14.
 */
public class MapViewFragment extends Fragment {

    public MapViewFragment(){}

    public static  MapViewFragment newInstance() { return new MapViewFragment(); }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_map_view, container, false);
        ConstraintLayout rootView = result.findViewById(R.id.map_view_container);
        TextView yolo = result.findViewById(R.id.test_tv);
        yolo.setText("ici c'est la map");


        return result;
    }
}
