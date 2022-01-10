package com.openclassrooms.p7.go4lunch.ui.fragment.preference;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.openclassrooms.p7.go4lunch.R;
import com.openclassrooms.p7.go4lunch.ViewModelFactory;
import com.openclassrooms.p7.go4lunch.databinding.FragmentPreferenceSettingsBinding;
import com.openclassrooms.p7.go4lunch.ui.MainActivity;
import com.openclassrooms.p7.go4lunch.ui.UserAndRestaurantViewModel;
import com.openclassrooms.p7.go4lunch.ui.fragment.map_view.MapViewFragment;
import com.openclassrooms.p7.go4lunch.ui.login.LoginActivity;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class PreferenceFragment extends Fragment {

    private static final String TAG = PreferenceFragment.class.getSimpleName();
    private FragmentPreferenceSettingsBinding mBinding;
    private UserAndRestaurantViewModel mViewModel;
    private String notification;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentPreferenceSettingsBinding.inflate(inflater, container, false);
        View view = mBinding.getRoot();
        initViewModel();
        loadSharedPreferences();
        initListener();
        setHasOptionsMenu(true);
        return view;
    }

    private void initViewModel() {
        mViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(UserAndRestaurantViewModel.class);
    }

    private void loadSharedPreferences() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        notification  = sharedPreferences.getString("notification", "notification_enabled");
        mBinding.preferenceSettingNotificationSwitch.setChecked(notification.equals("notification_enabled"));
    }

    private void initListener() {
        mBinding.preferenceSettingNotificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
               notification = "notification_enabled";
            } else {
                notification = "notification_disabled";
            }
            saveSharedPreferences("notification", notification);
        });

        mBinding.preferenceSettingDeleteAccountBtn.setOnClickListener(view -> {
            deleteAccountAlertPopup();
        });
    }

    private void saveSharedPreferences(String category, String userSettingsGetter) {
        SharedPreferences.Editor editor = requireActivity().getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE).edit();
        editor.putString(category, userSettingsGetter);
        editor.apply();
    }

    private void deleteAccountAlertPopup() {
        AlertDialog.Builder signOutPopup = new AlertDialog.Builder(requireContext());
        signOutPopup
                .setTitle(R.string.preference_popup_title)
                .setPositiveButton(R.string.main_activity_signout_confirmation_positive_btn, (dialog, which) -> {
                    mViewModel.deleteUserFromFirestore();
                    mViewModel.deleteFirebaseUser(requireContext()).addOnCompleteListener(task -> {
                       if (task.isComplete()) {
                           FragmentManager fragmentManager = FragmentManager.findFragment(this.requireView()).getParentFragmentManager();
                           fragmentManager
                                   .beginTransaction()
                                   .replace(R.id.linear_layout, new MapViewFragment())
                                   .commit();
                           Toast.makeText(requireContext(), requireContext().getResources().getString(R.string.preference_popup_account_deleted), Toast.LENGTH_SHORT).show();
                       }
                    });
                    Log.e(TAG, "deleteAccountAlertPopup: TASK COMPLETE" );
                }).setNegativeButton(R.string.main_activity_signout_confirmation_negative_btn, null)
                .show();
    }
}
