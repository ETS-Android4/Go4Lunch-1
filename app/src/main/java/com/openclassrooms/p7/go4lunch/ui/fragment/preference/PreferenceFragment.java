package com.openclassrooms.p7.go4lunch.ui.fragment.preference;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.openclassrooms.p7.go4lunch.R;
import com.openclassrooms.p7.go4lunch.ViewModelFactory;
import com.openclassrooms.p7.go4lunch.databinding.FragmentPreferenceSettingsBinding;
import com.openclassrooms.p7.go4lunch.ui.UserAndRestaurantViewModel;

import java.util.Objects;

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
                .setTitle(R.string.preference_popup_title_delete_account)
                .setPositiveButton(R.string.main_activity_signout_confirmation_positive_btn, (dialog, which) -> {
                    mViewModel.deleteUserFromFirestore(requireContext());
                    quitApplicationAlertPopup();
                }).setNegativeButton(R.string.main_activity_signout_confirmation_negative_btn, null)
                .show();
    }

    private void quitApplicationAlertPopup() {
        AlertDialog.Builder signOutPopup = new AlertDialog.Builder(requireContext());
        signOutPopup
                .setTitle(R.string.preference_popup_title_quit_app)
                .setPositiveButton(R.string.main_activity_signout_confirmation_positive_btn, (dialog, which) -> {
                    requireActivity().finishAffinity();
                }).setNegativeButton(R.string.main_activity_signout_confirmation_negative_btn, null)
                .show();
    }
}
