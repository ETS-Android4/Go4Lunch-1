package com.openclassrooms.p7.go4lunch.ui.fragment.listview_adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.openclassrooms.p7.go4lunch.R;
import com.openclassrooms.p7.go4lunch.injector.DI;
import com.openclassrooms.p7.go4lunch.model.Restaurant;
import com.openclassrooms.p7.go4lunch.service.RestaurantApiService;
import com.openclassrooms.p7.go4lunch.ui.DetailActivity;

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

        private final TextView nameTv;
        private final TextView addressTv;
        private final TextView openingHoursTv;
        private final ImageView restaurantPicture;
        private final TextView distanceTv;
        private final ImageView[] ratingStarsArray = new ImageView[3];
        private RestaurantApiService mApiService;


        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.list_view_row_restaurant_name_tv);
            addressTv = itemView.findViewById(R.id.list_view_row_restaurant_adress_tv);
            openingHoursTv = itemView.findViewById(R.id.list_view_row_restaurant_is_open_tv);
            restaurantPicture = itemView.findViewById(R.id.list_view_row_restaurant_picture_img);
            distanceTv = itemView.findViewById(R.id.list_view_row_distance_tv);
            ratingStarsArray[0] = itemView.findViewById(R.id.list_view_row_rating_first_star_img);
            ratingStarsArray[1] = itemView.findViewById(R.id.list_view_row_rating_second_star_img);
            ratingStarsArray[2] = itemView.findViewById(R.id.list_view_row_rating_third_star_img);
            mApiService = DI.getRestaurantApiService();
        }

        public void bind(Restaurant restaurant){
            nameTv.setText(restaurant.getName());
            addressTv.setText(restaurant.getAdress());
            openingHoursTv.setText(restaurant.getOpenningHours());
            distanceTv.setText(String.format(Locale.ENGLISH,"%4.0fm",restaurant.getDistance()));

            if (restaurant.getPictureUrl() != null) {
                Glide.with(itemView)
                        .load(restaurant.getPictureUrl())
                        .centerCrop()
                        .into(restaurantPicture);
            }

            for (int index = 0; index < ratingStarsArray.length; index++) {
                ratingStarsArray[index].setImageResource(mApiService.setRatingStars(index, restaurant.getRating()));
            }
        }
    }
}
