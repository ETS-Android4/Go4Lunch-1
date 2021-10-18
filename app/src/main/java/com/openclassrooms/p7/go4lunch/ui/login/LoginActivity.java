package com.openclassrooms.p7.go4lunch.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.openclassrooms.p7.go4lunch.R;
import com.openclassrooms.p7.go4lunch.databinding.ActivityLoginBinding;
import com.openclassrooms.p7.go4lunch.manager.UserManager;

/**
 * Created by lleotraas on 15.
 */
public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;
    private ActivityLoginBinding mBinding;
    private final UserManager mUserManager = UserManager.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.configureBinding();
        this.configureListeners();
        this.currentUserLogged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.handleResponseAfterSignIn(requestCode, resultCode, data);
    }

    private void configureBinding() {
        mBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = mBinding.getRoot();
        setContentView(view);
    }

    private void configureListeners() {
        mBinding.activityLoginFacebookBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, FacebookSignInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        });

        mBinding.activityLoginGoogleBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, GoogleSignInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        });
    }

    private void currentUserLogged() {
        if (mUserManager.isCurrentUserLogged()) {
            finish();
        }
    }

    private void showToastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data) {
        IdpResponse response = IdpResponse.fromResultIntent(data);

        if (requestCode == RC_SIGN_IN) {
            // SUCCESS
            if (resultCode == RESULT_OK) {
//                userManager.createUser();
                showToastMessage(getString(R.string.connection_succeed));
            }else {
                // ERRORS
                if (response == null) {
                    showToastMessage(getString(R.string.error_authentication_canceled));
                } else if (response.getError() != null) {
                    if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                        showToastMessage(getString(R.string.error_no_internet));
                    } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                        showToastMessage(getString(R.string.error_unknow_error));
                    }
                }
            }
        }
    }
}
