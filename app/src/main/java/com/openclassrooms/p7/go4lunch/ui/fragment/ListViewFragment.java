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
public class ListViewFragment extends Fragment {

    public static  ListViewFragment newInstance(){
        return new ListViewFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_view, container, false);
        TextView yali = view.findViewById(R.id.list_view_tv);
        yali.setText("Ici c'est la liste des restos");
        return view;
    }
}
