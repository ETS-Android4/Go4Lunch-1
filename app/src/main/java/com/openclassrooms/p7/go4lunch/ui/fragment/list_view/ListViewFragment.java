package com.openclassrooms.p7.go4lunch.ui.fragment.list_view;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.openclassrooms.p7.go4lunch.R;
import com.openclassrooms.p7.go4lunch.ViewModelFactory;
import com.openclassrooms.p7.go4lunch.injector.DI;
import com.openclassrooms.p7.go4lunch.model.Restaurant;
import com.openclassrooms.p7.go4lunch.model.RestaurantFavorite;
import com.openclassrooms.p7.go4lunch.model.User;
import com.openclassrooms.p7.go4lunch.service.ApiService;
import com.openclassrooms.p7.go4lunch.ui.UserAndRestaurantViewModel;
import com.openclassrooms.p7.go4lunch.ui.fragment.map_view.MapViewFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Created by lleotraas on 14.
 */
public class ListViewFragment extends Fragment {

    private static final int AUTOCOMPLETE_REQUEST_CODE = 23;
    private RecyclerView mRecyclerView;
    private ListViewAdapter listViewAdapter;
    private UserAndRestaurantViewModel mViewModel;
    private ApiService mApiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View root = inflater.inflate(R.layout.fragment_list_view, container, false);
        mRecyclerView = root.findViewById(R.id.list_view_recycler_view);
        this.configureServiceAndViewModel();
        setHasOptionsMenu(true);
        this.initList();
        return root;
    }

    private void configureServiceAndViewModel() {
        mViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(UserAndRestaurantViewModel.class);
        mApiService = DI.getRestaurantApiService();
//        List<Restaurant> restaurants = new ArrayList<>();
//        mViewModel.getAllRestaurants().observe(getViewLifecycleOwner(), restaurants::addAll);

    }

    private void initList() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity().getApplicationContext()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(requireActivity().getApplicationContext(), DividerItemDecoration.VERTICAL));
    }

    @Override
    public void onResume() {
        super.onResume();
        List<User> userInterestedList = new ArrayList<>();
        List<RestaurantFavorite> restaurantFavoriteList = new ArrayList<>();
        List<Restaurant> restaurantList = new ArrayList<>();

        mViewModel.getAllInterestedUsers().observe(getViewLifecycleOwner(), users -> {
            if (!userInterestedList.isEmpty()) {
                userInterestedList.clear();
            }
            userInterestedList.addAll(users);
                });

        mViewModel.getAllRestaurantFavorite().observe(getViewLifecycleOwner(), users -> {
            if (!restaurantFavoriteList.isEmpty()) {
                restaurantFavoriteList.clear();
            }
            restaurantFavoriteList.addAll(users);
            listViewAdapter = new ListViewAdapter(restaurantList, restaurantFavoriteList);
            mRecyclerView.setAdapter(listViewAdapter);
            mApiService.listViewComparator(restaurantList);
        });

        mViewModel.getAllRestaurants().observe(getViewLifecycleOwner(), restaurants -> {
            if (!userInterestedList.isEmpty()) {
                mViewModel.setNumberOfFriendInterested(userInterestedList, restaurants);
            }
            if (!restaurantList.isEmpty()) {
                restaurantList.clear();
            }
            restaurantList.addAll(restaurants);
            listViewAdapter = new ListViewAdapter(restaurants, restaurantFavoriteList);
            mRecyclerView.setAdapter(listViewAdapter);
            mApiService.listViewComparator(restaurants);
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        List<Place.Field> fields = Arrays.asList(Place.Field.ID);
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                .setTypeFilter(TypeFilter.ESTABLISHMENT)
                .setLocationBias(mApiService.getRectangularBound(MapViewFragment.CURRENT_LOCATION))
                .setCountry("FR")
                .setHint("Search restaurant")
                .build(requireContext());
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(Objects.requireNonNull(data));
                List<String> placesId = new ArrayList<>();
                placesId.add(place.getId());
                mViewModel.requestForPlaceDetails(placesId, requireContext(), true);
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(requireContext(), requireContext().getResources().getString(R.string.map_view_fragment_search_canceled), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
