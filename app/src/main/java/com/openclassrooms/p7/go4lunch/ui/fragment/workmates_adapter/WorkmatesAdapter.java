package com.openclassrooms.p7.go4lunch.ui.fragment.workmates_adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.openclassrooms.p7.go4lunch.R;
import com.openclassrooms.p7.go4lunch.databinding.WorkmatesListRowBinding;
import com.openclassrooms.p7.go4lunch.injector.DI;
import com.openclassrooms.p7.go4lunch.model.FavoriteOrSelectedRestaurant;
import com.openclassrooms.p7.go4lunch.model.User;
import com.openclassrooms.p7.go4lunch.service.RestaurantApiService;

import java.util.List;

public class WorkmatesAdapter extends RecyclerView.Adapter<WorkmatesAdapter.WorkmatesViewolder> {

    private List<User> mUsersList;
    private RestaurantApiService mApiService;

    public WorkmatesAdapter(List<User> usersList) {
        mUsersList = usersList;
    }

    @NonNull
    @Override
    public WorkmatesViewolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.workmates_list_row, parent, false);
        mApiService = DI.getRestaurantApiService();
        return new WorkmatesViewolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkmatesViewolder holder, int position) {
        holder.bind(mUsersList.get(position));
    }

    @Override
    public int getItemCount() {
        return mUsersList.size();
    }

    public class WorkmatesViewolder extends RecyclerView.ViewHolder {

        private WorkmatesListRowBinding mBinding;

        public WorkmatesViewolder(@NonNull View itemView) {
            super(itemView);
            mBinding = WorkmatesListRowBinding.bind(itemView);
        }

        public void bind(User user){
            //TODO repaired
            FavoriteOrSelectedRestaurant favoriteOrSelectedRestaurant = mApiService.searchSelectedRestaurant(user);
            Glide.with(itemView)
                    .load(user.getPhotoUrl())
                    .circleCrop()
                    .into(mBinding.workmatesListRowProfileImg);
            if (favoriteOrSelectedRestaurant != null) {
                mBinding.workmatesListRowEatingTypeTv.setText(String.format("%s is eating at %s", user.getUserName(), favoriteOrSelectedRestaurant.getRestaurantName()));
            } else {
                mBinding.workmatesListRowEatingTypeTv.setHint(String.format("%s hasn't decided yet", user.getUserName()));
            }
        }
    }
}
