package com.openclassrooms.p7.go4lunch.ui.fragment.list_view;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.openclassrooms.p7.go4lunch.R;
import com.openclassrooms.p7.go4lunch.databinding.ListViewRowBinding;
import com.openclassrooms.p7.go4lunch.injector.DI;
import com.openclassrooms.p7.go4lunch.model.Restaurant;
import com.openclassrooms.p7.go4lunch.service.ApiService;
import com.openclassrooms.p7.go4lunch.ui.DetailActivity;
import com.openclassrooms.p7.go4lunch.ui.MainActivity;

import java.util.Locale;


public class ListViewAdapter extends ListAdapter<Restaurant, ListViewAdapter.ListViewHolder> {

    protected ListViewAdapter() {
        super(new ListNeighbourItemCallback());
    }


    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_row, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewAdapter.ListViewHolder holder, int position) {
        holder.bind(getItem(position));
        //TODO number of friend interested
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), DetailActivity.class);
            String id = getItem(position).getId();
            intent.putExtra("restaurantId", id);
            view.getContext().startActivity(intent);
        });
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {

        private final ListViewRowBinding mBinding;
        private final ImageView[] ratingStarsArray = new ImageView[3];
        private final ApiService mApiService;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            mBinding = ListViewRowBinding.bind(itemView);
            ratingStarsArray[0] = mBinding.listViewRowRatingFirstStarImg;
            ratingStarsArray[1] = mBinding.listViewRowRatingSecondStarImg;
            ratingStarsArray[2] = mBinding.listViewRowRatingThirdStarImg;
            mApiService = DI.getRestaurantApiService();

        }

        public void bind(Restaurant restaurant){
            mBinding.listViewRowRestaurantNameTv.setText(mApiService.removeUselessWords(restaurant.getName()));
            mBinding.listViewRowRestaurantAdressTv.setText(restaurant.getAdress());
            mBinding.listViewRowRestaurantIsOpenTv.setText(restaurant.getOpenningHours());
            mBinding.listViewRowDistanceTv.setText(String.format(Locale.ENGLISH,"%4.0fm",restaurant.getDistance()));

            if (restaurant.getPictureUrl() != null) {
                Glide.with(itemView)
                        .load(restaurant.getPictureUrl())
                        .centerCrop()
                        .into(mBinding.listViewRowRestaurantPictureImg);
            }

            for (int index = 0; index < ratingStarsArray.length; index++) {
                ratingStarsArray[index].setImageResource(mApiService.setRatingStars(index, restaurant.getRating()));
            }
            mBinding.listViewRowInterestedFriendTv.setText(String.format("(%s)",restaurant.getNumberOfFriendInterested()));
        }
    }

    private static class ListNeighbourItemCallback extends DiffUtil.ItemCallback<Restaurant> {

        @Override
        public boolean areItemsTheSame(@NonNull Restaurant oldItem, @NonNull Restaurant newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Restaurant oldItem, @NonNull Restaurant newItem) {
            return oldItem.equals(newItem);
        }
    }
}
