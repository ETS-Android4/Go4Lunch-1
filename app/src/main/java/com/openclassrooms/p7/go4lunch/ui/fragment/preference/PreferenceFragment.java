package com.openclassrooms.p7.go4lunch.ui.fragment.preference;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

import com.openclassrooms.p7.go4lunch.R;

import java.util.Objects;

public class PreferenceFragment extends PreferenceFragmentCompat {

    private static final String TAG = "PreferenceFragment";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preference, rootKey);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        boolean nightMode = sharedPreferences.getBoolean("night_mode", true);
        if (nightMode) {
            Log.i(TAG, "onViewCreated: il fait nuit");
        } else {
            Log.i(TAG, "onViewCreated: il fait jour");
        }
    }
}
