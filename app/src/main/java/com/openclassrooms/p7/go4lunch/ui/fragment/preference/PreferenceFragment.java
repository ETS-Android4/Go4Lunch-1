package com.openclassrooms.p7.go4lunch.ui.fragment.preference;

import static com.openclassrooms.p7.go4lunch.model.UserSettings.NOTIFICATION;
import static com.openclassrooms.p7.go4lunch.model.UserSettings.NOTIFICATION_DISABLED;
import static com.openclassrooms.p7.go4lunch.model.UserSettings.NOTIFICATION_ENABLED;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.openclassrooms.p7.go4lunch.databinding.FragmentPreferenceSettingsBinding;
import com.openclassrooms.p7.go4lunch.model.UserSettings;

public class PreferenceFragment extends Fragment {

    private FragmentPreferenceSettingsBinding mBinding;
    private UserSettings settings;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentPreferenceSettingsBinding.inflate(inflater, container, false);
        View view = mBinding.getRoot();
        settings = (UserSettings) requireActivity().getApplication();
        loadSharedPreferences();
        initSwitchListener();
        return view;
    }

    private void loadSharedPreferences() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(UserSettings.PREFERENCES, Context.MODE_PRIVATE);
        String notification  = sharedPreferences.getString(UserSettings.NOTIFICATION, UserSettings.NOTIFICATION_ENABLED);
        settings.setNotification(notification);
        mBinding.preferenceSettingNotificationSwitch.setChecked(notification.equals(NOTIFICATION_ENABLED));
    }

    private void initSwitchListener() {
        mBinding.preferenceSettingNotificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    settings.setNotification(NOTIFICATION_ENABLED);
                } else {
                    settings.setNotification(NOTIFICATION_DISABLED);
                }
                saveSharedPreferences(NOTIFICATION, settings.getNotification());
            }
        });
    }

    private void saveSharedPreferences(String category, String userSettingsGetter) {
        SharedPreferences.Editor editor = requireActivity().getSharedPreferences(UserSettings.PREFERENCES, Context.MODE_PRIVATE).edit();
        editor.putString(category, userSettingsGetter);
        editor.apply();
    }
}
