package com.openclassrooms.p7.go4lunch.ui.sign_in;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.OAuthProvider;
import com.openclassrooms.p7.go4lunch.ui.login.LoginActivity;

public class TwitterSignInActivity extends LoginActivity {

    private OAuthProvider.Builder provider;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        provider = OAuthProvider.newBuilder("twitter.com");
        firebaseAuth = FirebaseAuth.getInstance();

        Task<AuthResult> pendingResultTask = firebaseAuth.getPendingAuthResult();
        if (pendingResultTask != null) {
            pendingResultTask.addOnSuccessListener(authResult -> {

            }).addOnFailureListener(e -> {
                Log.e(TAG, "onCreate: identification failure " + e.getMessage());
            });
        } else {
            firebaseAuth.startActivityForSignInWithProvider(this, provider.build()).addOnSuccessListener(authResult -> {

            }).addOnFailureListener(e -> {
                Log.e(TAG, "onCreate: identification failure " + e.getMessage());
            });
        }
    }


}
