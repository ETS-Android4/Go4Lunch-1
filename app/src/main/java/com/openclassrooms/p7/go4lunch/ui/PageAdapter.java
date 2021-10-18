package com.openclassrooms.p7.go4lunch.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.openclassrooms.p7.go4lunch.ui.fragment.ListViewFragment;
import com.openclassrooms.p7.go4lunch.ui.fragment.MapViewFragment;
import com.openclassrooms.p7.go4lunch.ui.fragment.WorkmatesFragment;

/**
 * Created by lleotraas on 14.
 */
public class PageAdapter extends FragmentPagerAdapter {

    public PageAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return MapViewFragment.newInstance();
            case 1:
                return ListViewFragment.newInstance();
            case 2:
                return WorkmatesFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Map View";
            case 1:
                return "List View";
            case 2:
                return "Workmates View";
            default:
                return null;
        }
    }
}
