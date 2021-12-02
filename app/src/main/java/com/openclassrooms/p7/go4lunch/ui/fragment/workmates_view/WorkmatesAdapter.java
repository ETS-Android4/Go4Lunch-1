package com.openclassrooms.p7.go4lunch.ui.fragment.workmates_view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.openclassrooms.p7.go4lunch.R;
import com.openclassrooms.p7.go4lunch.databinding.WorkmatesListRowBinding;
import com.openclassrooms.p7.go4lunch.injector.DI;
import com.openclassrooms.p7.go4lunch.model.UserAndRestaurant;
import com.openclassrooms.p7.go4lunch.model.User;
import com.openclassrooms.p7.go4lunch.service.ApiService;

import java.util.List;

public class WorkmatesAdapter extends RecyclerView.Adapter<WorkmatesAdapter.WorkmatesViewHolder> {

    private List<User> mUsersList;
    private ApiService mApiService;

    public WorkmatesAdapter(List<User> usersList) {
        mUsersList = usersList;
    }

    @NonNull
    @Override
    public WorkmatesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.workmates_list_row, parent, false);
        mApiService = DI.getRestaurantApiService();
        return new WorkmatesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkmatesViewHolder holder, int position) {
        holder.bind(mUsersList.get(position));
    }

    @Override
    public int getItemCount() {
        return mUsersList.size();
    }

    public class WorkmatesViewHolder extends RecyclerView.ViewHolder {

        private WorkmatesListRowBinding mBinding;

        public WorkmatesViewHolder(@NonNull View itemView) {
            super(itemView);
            mBinding = WorkmatesListRowBinding.bind(itemView);
        }

        public void bind(User user){
            UserAndRestaurant userAndRestaurant = mApiService.searchSelectedRestaurant(user);
            Glide.with(itemView)
                    .load(user.getPhotoUrl())
                    .circleCrop()
                    .into(mBinding.workmatesListRowProfileImg);
            if (userAndRestaurant != null) {
                mBinding.workmatesListRowEatingTypeTv.setText(String.format("%s %s %s", mApiService.makeUserFirstName(user.getUserName()), itemView.getResources().getString(R.string.workmates_list_view_holder_is_eating_at), mApiService.removeRestaurantWord(userAndRestaurant.getRestaurantName())));
            } else {
                mBinding.workmatesListRowEatingTypeTv.setHint(String.format("%s %s", mApiService.makeUserFirstName(user.getUserName()), itemView.getResources().getString(R.string.workmates_list_view_holder_not_decided)));
            }
        }
    }
}
