package com.openclassrooms.p7.go4lunch.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.openclassrooms.p7.go4lunch.databinding.RestaurantChoiceDialogBoxBinding;

public class RestaurantChoiceDialogPopup extends Dialog {

    private RestaurantChoiceDialogBoxBinding mBinding;

    public RestaurantChoiceDialogPopup(@NonNull Context context) {
        super(context);
        mBinding = RestaurantChoiceDialogBoxBinding.inflate(getLayoutInflater());
        View root = mBinding.getRoot();
        setContentView(root);
    }

    public void setTitle(String title) {
        mBinding.restaurantChoiceDialogBoxTitle.setText(title);
    }

    public void setMessage(String message) {
        mBinding.restaurantChoiceDialogBoxMessage.setText(message);
    }

    public void build() {
        show();
    }
}
