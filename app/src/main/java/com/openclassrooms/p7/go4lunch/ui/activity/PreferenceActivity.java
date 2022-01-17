package com.openclassrooms.p7.go4lunch.ui.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.openclassrooms.p7.go4lunch.R;
import com.openclassrooms.p7.go4lunch.databinding.ActivityPreferenceBinding;
import com.openclassrooms.p7.go4lunch.ui.fragment.preference.PreferenceFragment;

import java.util.Objects;

public class PreferenceActivity extends AppCompatActivity {
    private ActivityPreferenceBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityPreferenceBinding.inflate(getLayoutInflater());
        View root = mBinding.getRoot();
        setContentView(root);
        setSupportActionBar(mBinding.activityPreferenceToolbar.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mBinding.activityPreferenceToolbar.getRoot().setTitle(getResources().getString(R.string.preference_setting_title));
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.activity_preference_fragment_container, PreferenceFragment.class, null)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
