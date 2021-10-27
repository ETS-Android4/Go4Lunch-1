package com.openclassrooms.p7.go4lunch.ui;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.openclassrooms.p7.go4lunch.ui.fragment.ListViewFragment;
import com.openclassrooms.p7.go4lunch.ui.fragment.MapViewFragment;
import com.openclassrooms.p7.go4lunch.ui.fragment.WorkmatesFragment;

/**
 * Created by lleotraas on 14.
 */
public class PageAdapter extends FragmentStateAdapter {

    public PageAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position) {
            case 1:
                return new ListViewFragment();
            case 2:
                return new WorkmatesFragment();
            default:
                return new MapViewFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
