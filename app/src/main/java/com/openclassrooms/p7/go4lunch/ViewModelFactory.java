package com.openclassrooms.p7.go4lunch;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.openclassrooms.p7.go4lunch.ui.UserAndRestaurantViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private static ViewModelFactory factory;

    public static ViewModelFactory getInstance() {
        if (factory == null) {
            synchronized (ViewModelFactory.class) {
                if (factory == null) {
                    factory = new ViewModelFactory();
                }
            }

        }
        return factory;
    }

    public ViewModelFactory() {
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(UserAndRestaurantViewModel.class)) {
            return (T) new UserAndRestaurantViewModel();
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
