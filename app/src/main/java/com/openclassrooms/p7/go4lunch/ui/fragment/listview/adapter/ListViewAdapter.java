package com.openclassrooms.p7.go4lunch.ui.fragment.listview.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;


import com.openclassrooms.p7.go4lunch.R;
import com.openclassrooms.p7.go4lunch.model.Restaurant;
import com.openclassrooms.p7.go4lunch.service.RestaurantApiService;

import java.util.List;


public class ListViewAdapter extends RecyclerView.Adapter<ListViewAdapter.ListViewHolder> {

    private List<Restaurant> mRestaurantList;

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
    }

    @Override
    public int getItemCount() {
        return mRestaurantList.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {

        private TextView nameTv;
        private TextView adressTv;
        private TextView openningHoursTv;
        private ImageView restaurantPicture;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.list_view_row_restaurant_name_tv);
            adressTv = itemView.findViewById(R.id.list_view_row_restaurant_adress_tv);
            openningHoursTv = itemView.findViewById(R.id.list_view_row_restaurant_is_open_tv);
            restaurantPicture = itemView.findViewById(R.id.list_view_row_restaurant_picture_img);
        }

        public void bind(Restaurant restaurant){
            nameTv.setText(restaurant.getName());
            adressTv.setText(restaurant.getAdress());
            openningHoursTv.setText(restaurant.getOpenningHours());
            Glide.with(this.itemView)
                    .load(restaurant.getPictureUrl())
                    .into(restaurantPicture);

        }
    }
}
