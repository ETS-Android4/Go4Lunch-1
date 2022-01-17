package com.openclassrooms.p7.go4lunch.ui.fragment.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.openclassrooms.p7.go4lunch.R;
import com.openclassrooms.p7.go4lunch.ViewModelFactory;
import com.openclassrooms.p7.go4lunch.databinding.FragmentPreferenceSettingsBinding;
import com.openclassrooms.p7.go4lunch.dialog.CustomChoiceDialogPopup;
import com.openclassrooms.p7.go4lunch.ui.UserAndRestaurantViewModel;

public class PreferenceFragment extends Fragment {

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
            saveSharedPreferences(notification);
        });

        mBinding.preferenceSettingDeleteAccountBtn.setOnClickListener(view -> deleteAccountAlertPopup());
    }

    private void saveSharedPreferences(String userSettingsGetter) {
        SharedPreferences.Editor editor = requireActivity().getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE).edit();
        editor.putString("notification", userSettingsGetter);
        editor.apply();
    }

    private void deleteAccountAlertPopup() {
        CustomChoiceDialogPopup customChoiceDialogPopup = new CustomChoiceDialogPopup(requireContext());
        customChoiceDialogPopup.setTitle(getResources().getString(R.string.preference_popup_title_delete_account));
        customChoiceDialogPopup.setPositiveBtnText(getResources().getString(R.string.main_activity_signout_confirmation_positive_btn));
        customChoiceDialogPopup.setNegativeBtnText(getResources().getString(R.string.main_activity_signout_confirmation_negative_btn));
        customChoiceDialogPopup.getPositiveBtn().setOnClickListener(view -> {
            mViewModel.deleteUserAccount(requireContext());
            customChoiceDialogPopup.dismiss();
        });
        customChoiceDialogPopup.getNegativeBtn().setOnClickListener(view -> customChoiceDialogPopup.close());
        customChoiceDialogPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        customChoiceDialogPopup.build();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.preference_toolbar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
}
