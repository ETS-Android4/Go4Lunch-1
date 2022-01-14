package com.openclassrooms.p7.go4lunch.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.openclassrooms.p7.go4lunch.databinding.CustomChoiceDialogBoxBinding;

public class CustomChoiceDialogPopup extends Dialog {

    private CustomChoiceDialogBoxBinding mBinding;

    public CustomChoiceDialogPopup(@NonNull Context context) {
        super(context);
        mBinding = CustomChoiceDialogBoxBinding.inflate(getLayoutInflater());
        View root = mBinding.getRoot();
        setContentView(root);
    }

    public void setTitle(String title) {
        mBinding.customChoiceDialogBoxTitle.setText(title);
    }

    public void setPositiveBtnText(String positiveBtnText) {
        mBinding.customChoiceDialogBoxPositiveBtn.setText(positiveBtnText);
    }

    public void setNegativeBtnText(String negativeBtnText) {
        mBinding.customChoiceDialogBoxNegativeBtn.setText(negativeBtnText);
    }

    public Button getPositiveBtn() {
        return mBinding.customChoiceDialogBoxPositiveBtn;
    }

    public Button getNegativeBtn() {
        return mBinding.customChoiceDialogBoxNegativeBtn;
    }

    public void build() {
        show();
    }
    public void close() {dismiss();}
}
