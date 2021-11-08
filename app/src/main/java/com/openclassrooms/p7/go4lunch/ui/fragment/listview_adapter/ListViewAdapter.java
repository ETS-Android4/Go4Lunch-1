package com.openclassrooms.p7.go4lunch.ui.fragment.listview_adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.openclassrooms.p7.go4lunch.R;
import com.openclassrooms.p7.go4lunch.databinding.ListViewRowBinding;
import com.openclassrooms.p7.go4lunch.injector.DI;
import com.openclassrooms.p7.go4lunch.model.Restaurant;
import com.openclassrooms.p7.go4lunch.service.RestaurantApiService;
import com.openclassrooms.p7.go4lunch.ui.DetailActivity;
import com.openclassrooms.p7.go4lunch.ui.MainActivity;
import com.openclassrooms.p7.go4lunch.ui.UserAndRestaurantViewModel;

import java.util.List;
import java.util.Locale;


public class ListViewAdapter extends RecyclerView.Adapter<ListViewAdapter.ListViewHolder> {

    private final List<Restaurant> mRestaurantList;

    public ListViewAdapter(List<Restaurant> restaurantList) {
        mRestaurantList = restaurantList;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_row, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewAdapter.ListViewHolder holder, int position) {
        holder.bind(mRestaurantList.get(position));
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), DetailActivity.class);
            String id = mRestaurantList.get(position).getId();
            intent.putExtra("restaurantId", id);
            view.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mRestaurantList.size();
    }

    public static class ListViewHolder extends RecyclerView.ViewHolder {
        private ListViewRowBinding mBinding;
        private final ImageView[] ratingStarsArray = new ImageView[3];
        private final RestaurantApiService mApiService;
        private UserAndRestaurantViewModel viewModel;
        private String CURRENT_USER_ID = MainActivity.CURRENT_USER_ID;


        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            mBinding = ListViewRowBinding.bind(itemView);
            ratingStarsArray[0] = mBinding.listViewRowRatingFirstStarImg;
            ratingStarsArray[1] = mBinding.listViewRowRatingSecondStarImg;
            ratingStarsArray[2] = mBinding.listViewRowRatingThirdStarImg;
            mApiService = DI.getRestaurantApiService();

        }

        public void bind(Restaurant restaurant){
            mBinding.listViewRowRestaurantNameTv.setText(restaurant.getName());
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
            mBinding.listViewRowInterestedFriendTv.setText(String.format("(%s)",mApiService.getUsersInterestedAtCurrentRestaurant(CURRENT_USER_ID, restaurant).size()));
        }
    }
}
