package com.openclassrooms.p7.go4lunch.ui.fragment.workmates_view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.openclassrooms.p7.go4lunch.R;
import com.openclassrooms.p7.go4lunch.databinding.WorkmatesListRowBinding;
import com.openclassrooms.p7.go4lunch.injector.DI;
import com.openclassrooms.p7.go4lunch.model.UserAndRestaurant;
import com.openclassrooms.p7.go4lunch.model.User;
import com.openclassrooms.p7.go4lunch.service.ApiService;
import com.openclassrooms.p7.go4lunch.ui.UserStateItem;

import java.util.List;

public class WorkmatesAdapter extends ListAdapter<UserStateItem, WorkmatesAdapter.WorkmatesViewHolder> {


    private ApiService mApiService;

    public WorkmatesAdapter() {
        super(new ListNeighbourItemCallback());
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
        holder.bind(getItem(position));
    }

    public class WorkmatesViewHolder extends RecyclerView.ViewHolder {

        private WorkmatesListRowBinding mBinding;

        public WorkmatesViewHolder(@NonNull View itemView) {
            super(itemView);
            mBinding = WorkmatesListRowBinding.bind(itemView);
        }

        public void bind(UserStateItem user){
            UserAndRestaurant userAndRestaurant = mApiService.searchSelectedRestaurant(user);
            Glide.with(itemView)
                    .load(user.getPhotoUrl())
                    .circleCrop()
                    .into(mBinding.workmatesListRowProfileImg);
            if (userAndRestaurant != null) {
                mBinding.workmatesListRowEatingTypeTv.setText(String.format("%s %s %s", mApiService.makeUserFirstName(user.getUserName()), itemView.getResources().getString(R.string.workmates_list_view_holder_is_eating_at), mApiService.removeUselessWords(userAndRestaurant.getRestaurantName())));
            } else {
                mBinding.workmatesListRowEatingTypeTv.setHint(String.format("%s %s", mApiService.makeUserFirstName(user.getUserName()), itemView.getResources().getString(R.string.workmates_list_view_holder_not_decided)));
                mBinding.workmatesListRowEatingTypeTv.setHintTextColor(itemView.getResources().getColor(R.color.grey));
            }
        }
    }

    private static class ListNeighbourItemCallback extends DiffUtil.ItemCallback<UserStateItem> {

        @Override
        public boolean areItemsTheSame(@NonNull UserStateItem oldItem, @NonNull UserStateItem newItem) {
            return oldItem.getUid().equals(newItem.getUid());
        }

        @Override
        public boolean areContentsTheSame(@NonNull UserStateItem oldItem, @NonNull UserStateItem newItem) {
            return oldItem.equals(newItem);
        }
    }
}
