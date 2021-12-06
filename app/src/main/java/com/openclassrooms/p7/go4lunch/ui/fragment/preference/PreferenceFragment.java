package com.openclassrooms.p7.go4lunch.ui.fragment.preference;

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

import com.openclassrooms.p7.go4lunch.R;
import com.openclassrooms.p7.go4lunch.databinding.FragmentPreferenceSettingsBinding;
import com.openclassrooms.p7.go4lunch.injector.DI;
import com.openclassrooms.p7.go4lunch.model.UserSettings;
import com.openclassrooms.p7.go4lunch.service.ApiService;

public class PreferenceFragment extends Fragment {

    private FragmentPreferenceSettingsBinding mBinding;
    private SharedPreferences sharedPreferences;
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
        sharedPreferences = requireActivity().getSharedPreferences(UserSettings.PREFERENCES, Context.MODE_PRIVATE);
        String theme = sharedPreferences.getString(UserSettings.CUSTOM_THEME, UserSettings.LIGHT_THEME);
        settings.setCustomTheme(theme);
        if (theme.equals("darkTheme")) {
            mBinding.preferenceSettingNightModeSwitch.setChecked(true);
        } else {
            mBinding.preferenceSettingNightModeSwitch.setChecked(false);
        }
    }

    private void initSwitchListener() {
        mBinding.preferenceSettingNightModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    settings.setCustomTheme(UserSettings.DARK_THEME);
                } else {
                    settings.setCustomTheme(UserSettings.LIGHT_THEME);
                }
                SharedPreferences.Editor editor = requireActivity().getSharedPreferences(UserSettings.PREFERENCES, Context.MODE_PRIVATE).edit();
                editor.putString(UserSettings.CUSTOM_THEME, settings.getCustomTheme());
                editor.apply();
                updateView();
            }
        });
        mBinding.preferenceSettingNotificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                } else {

                }
                SharedPreferences.Editor editor = requireActivity().getSharedPreferences(UserSettings.PREFERENCES, Context.MODE_PRIVATE).edit();
                editor.putString(UserSettings.CUSTOM_THEME, settings.getCustomTheme());
                editor.apply();
            }
        });
    }

    private void updateView() {
        ApiService apiService = DI.getRestaurantApiService();
        if (settings.getCustomTheme().equals(UserSettings.DARK_THEME)) {
            mBinding.preferenceSettingContainer.setBackgroundColor(getResources().getColor(R.color.light_text_color));
            mBinding.preferenceSettingNightModeTv.setTextColor(getResources().getColor(R.color.white));
            mBinding.preferenceSettingTitleTv.setTextColor(getResources().getColor(R.color.white));
        } else {
            mBinding.preferenceSettingContainer.setBackgroundColor(getResources().getColor(R.color.white));
            mBinding.preferenceSettingNightModeTv.setTextColor(getResources().getColor(R.color.black));
            mBinding.preferenceSettingTitleTv.setTextColor(getResources().getColor(R.color.black));
        }
    }


}
