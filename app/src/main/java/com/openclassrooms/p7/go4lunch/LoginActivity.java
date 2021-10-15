package com.openclassrooms.p7.go4lunch;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.openclassrooms.p7.go4lunch.databinding.ActivityLoginBinding;
import com.openclassrooms.p7.go4lunch.manager.UserManager;

/**
 * Created by lleotraas on 15.
 */
public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding mBinding;
    private UserManager mUserManager = UserManager.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.configureBinding();
        this.configureListeners();
        this.currentUserLogged();
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
}
