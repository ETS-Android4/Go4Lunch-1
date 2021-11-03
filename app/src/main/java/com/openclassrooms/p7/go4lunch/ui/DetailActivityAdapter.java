package com.openclassrooms.p7.go4lunch.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.openclassrooms.p7.go4lunch.R;
import com.openclassrooms.p7.go4lunch.databinding.WorkmatesListRowBinding;
import com.openclassrooms.p7.go4lunch.injector.DI;
import com.openclassrooms.p7.go4lunch.manager.CurrentUserManager;
import com.openclassrooms.p7.go4lunch.model.FavoriteRestaurant;
import com.openclassrooms.p7.go4lunch.model.Restaurant;
import com.openclassrooms.p7.go4lunch.model.User;
import com.openclassrooms.p7.go4lunch.service.RestaurantApiService;

import java.util.ArrayList;
import java.util.List;

public class DetailActivityAdapter extends RecyclerView.Adapter<DetailActivityAdapter.DetailActivityViewHolder> {

    private List<User> mUsers;
    private final List<FavoriteRestaurant> mFavoriteRestaurantList;

    public DetailActivityAdapter(List<User> users, List<FavoriteRestaurant> favoriteRestaurant, Restaurant mCurrentRestaurant) {
        this.mFavoriteRestaurantList = favoriteRestaurant;
        CurrentUserManager mCurrentUserManager = CurrentUserManager.getInstance();
        RestaurantApiService apiService = DI.getRestaurantApiService();
        List<User> userList = new ArrayList<>();
        for (FavoriteRestaurant favoriteRestaurants : mFavoriteRestaurantList) {
            // Les restos qui sont selectionnés  &                              ne doit pas être le User actuel                         &           doit être dans le resto actuel
            if (favoriteRestaurants.isSelected() && !mCurrentUserManager.getCurrentUser().getUid().equals(favoriteRestaurants.getUid()) && mCurrentRestaurant.getId().equals(favoriteRestaurants.getRestaurantId())) {
                User user = apiService.searchUserById(favoriteRestaurants.getUid());
                userList.add(user);
            }
            this.mUsers = userList;
        }

    }

    @NonNull
    @Override
    public DetailActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.workmates_list_row, parent, false);
        return new DetailActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailActivityViewHolder holder, int position) {
                holder.bind(mUsers.get(position));
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public static class DetailActivityViewHolder extends RecyclerView.ViewHolder {

        private final WorkmatesListRowBinding mBinding;


        public DetailActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            mBinding = WorkmatesListRowBinding.bind(itemView);
        }

        public void bind(User user) {
            Glide.with(itemView.getContext())
                    .load(user.getPhotoUrl())
                    .circleCrop()
                    .into(mBinding.workmatesListRowProfileImg);
            mBinding.workmatesListRowEatingTypeTv.setText(String.format("%s is joining", user.getUserName()));
        }
    }
}