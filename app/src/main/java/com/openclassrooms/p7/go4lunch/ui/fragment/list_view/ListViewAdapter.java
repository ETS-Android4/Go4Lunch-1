package com.openclassrooms.p7.go4lunch.ui.fragment.list_view;

import android.annotation.SuppressLint;
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
import com.openclassrooms.p7.go4lunch.service.ApiService;
import com.openclassrooms.p7.go4lunch.ui.activity.DetailActivity;

import java.util.List;
import java.util.Locale;


public class ListViewAdapter extends RecyclerView.Adapter<ListViewAdapter.ListViewHolder> {

    private List<Restaurant> mRestaurantList;

    public ListViewAdapter(List<Restaurant> mRestaurantList) {
        this.mRestaurantList = mRestaurantList;
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

    @SuppressLint("NotifyDataSetChanged")
    public void updateRestaurantListOrder(List<Restaurant> restaurantList) {
        mRestaurantList = restaurantList;
        notifyDataSetChanged();
    }


    public static class ListViewHolder extends RecyclerView.ViewHolder {

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

        @SuppressLint("UseCompatLoadingForDrawables")
        public void bind(Restaurant restaurant){
            String openingHourText = makeOpeningHourString(restaurant.getOpeningHours());
            mBinding.listViewRowRestaurantNameTv.setText(mApiService.formatRestaurantName(restaurant.getName()));
            mBinding.listViewRowRestaurantAdressTv.setText(restaurant.getAddress());
            mBinding.listViewRowRestaurantIsOpenTv.setText(openingHourText);
            mBinding.listViewRowDistanceTv.setText(String.format(Locale.ENGLISH,"%4.0fm",restaurant.getDistance()));

            if (restaurant.getPictureUrl() != null) {

                Glide.with(itemView)
                        .load(restaurant.getPictureUrl())
                        .optionalCenterCrop()
                        .into(mBinding.listViewRowRestaurantPictureImg);
            } else {
                Glide.with(itemView)
                        .load(R.drawable.no_image_picture)
                        .centerCrop()
                        .into(mBinding.listViewRowRestaurantPictureImg);
            }

            for (int index = 0; index < ratingStarsArray.length; index++) {
                ratingStarsArray[index].setImageResource(mApiService.setRatingStars(index, restaurant.getRating()));
            }
            mBinding.listViewRowInterestedFriendTv.setText(String.format("(%s)",restaurant.getNumberOfFriendInterested()));
            if (restaurant.isSearched()) {
                mBinding.listViewRowContainer.setBackgroundColor(itemView.getResources().getColor(R.color.grey));
            } else {
                mBinding.listViewRowContainer.setBackground(itemView.getResources().getDrawable(R.color.light_background_color));
            }

            if (restaurant.isFavorite()) {
                mBinding.listViewRowFavoriteImg.setVisibility(View.VISIBLE);
            } else {
                mBinding.listViewRowFavoriteImg.setVisibility(View.GONE);
            }
        }

        private String makeOpeningHourString(String openingHours) {
            char firstCode = openingHours.charAt(0);
            char lastCode = ' ';
            String time = null;
            String lastString = null;
            if (openingHours.length() > 1) {
                lastCode = openingHours.charAt(openingHours.length() - 1);
                int length = openingHours.length() - 1;
                time = openingHours.substring(1, length);
                lastString = itemView.getResources().getString(R.string.list_view_holder_am);
            }

            if (lastCode == '1') {
                lastString = itemView.getResources().getString(R.string.list_view_holder_pm);
            }

            switch (firstCode) {
                case '0':
                    return String.format("%s %s%s", itemView.getResources().getString(R.string.list_view_holder_open_at), time, lastString);
                case '1':
                    return String.format("%s %s%s", itemView.getResources().getString(R.string.list_view_holder_until), time, lastString);
                case '4':
                    return itemView.getResources().getString(R.string.list_view_holder_still_closed);
                default:
                    return itemView.getResources().getString(R.string.workmates_list_view_holder_no_details_here);
            }
        }
    }
}
