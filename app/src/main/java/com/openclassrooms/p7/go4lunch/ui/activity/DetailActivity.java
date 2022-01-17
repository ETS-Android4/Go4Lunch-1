package com.openclassrooms.p7.go4lunch.ui.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.openclassrooms.p7.go4lunch.R;
import com.openclassrooms.p7.go4lunch.ui.fragment.detail.DetailFragment;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.activity_detail_fragment_container, DetailFragment.class, null)
                    .commit();
        }
    }
}