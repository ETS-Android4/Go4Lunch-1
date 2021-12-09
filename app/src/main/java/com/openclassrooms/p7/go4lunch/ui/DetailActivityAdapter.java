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
import com.openclassrooms.p7.go4lunch.model.Restaurant;
import com.openclassrooms.p7.go4lunch.model.User;
import com.openclassrooms.p7.go4lunch.service.ApiService;

import java.util.List;

public class DetailActivityAdapter extends RecyclerView.Adapter<DetailActivityAdapter.DetailActivityViewHolder> {

    private List<User> mUsers = null;
    ApiService mApiService;

    public DetailActivityAdapter(Restaurant restaurant) {
        mApiService = DI.getRestaurantApiService();
        String CURRENT_USER_ID = MainActivity.CURRENT_USER_ID;
        this.mUsers = mApiService.getUsersInterestedAtCurrentRestaurants(CURRENT_USER_ID, restaurant);
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

    public class DetailActivityViewHolder extends RecyclerView.ViewHolder {

        private final WorkmatesListRowBinding mBinding;
        private final View view;

        public DetailActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            mBinding = WorkmatesListRowBinding.bind(itemView);
            view = itemView;
        }

        public void bind(User user) {
            Glide.with(itemView.getContext())
                    .load(user.getPhotoUrl())
                    .circleCrop()
                    .into(mBinding.workmatesListRowProfileImg);
            mBinding.workmatesListRowEatingTypeTv.setText(String.format("%s %s", mApiService.makeUserFirstName(user.getUserName()), view.getResources().getString(R.string.detail_viewHolder_is_joining)));
        }
    }
}