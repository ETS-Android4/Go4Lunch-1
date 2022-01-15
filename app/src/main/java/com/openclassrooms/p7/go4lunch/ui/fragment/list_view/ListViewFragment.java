package com.openclassrooms.p7.go4lunch.ui.fragment.list_view;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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
import com.openclassrooms.p7.go4lunch.model.SortMethod;
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
    private final List<Restaurant> mRestaurantList = new ArrayList<>();
    private final List<RestaurantFavorite> mRestaurantFavoriteList = new ArrayList<>();
    private SortMethod mSortMethod;

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

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.list_view_toolbar_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void configureServiceAndViewModel() {
        mViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(UserAndRestaurantViewModel.class);
        mApiService = DI.getRestaurantApiService();
    }

    private void initList() {
        mSortMethod = SortMethod.NONE;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity().getApplicationContext()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(requireActivity().getApplicationContext(), DividerItemDecoration.VERTICAL));
    }

    @Override
    public void onResume() {
        super.onResume();
        List<User> userInterestedList = new ArrayList<>();
        mViewModel.getAllInterestedUsers().observe(getViewLifecycleOwner(), users -> {
            if (!userInterestedList.isEmpty()) {
                userInterestedList.clear();
            }
            userInterestedList.addAll(users);
                });

        mViewModel.getAllRestaurantFavorite().observe(getViewLifecycleOwner(), restaurantFavoriteList -> {
            if (!mRestaurantFavoriteList.isEmpty()) {
                mRestaurantFavoriteList.clear();
            }
            mRestaurantFavoriteList.addAll(restaurantFavoriteList);
            mViewModel.setRestaurantFavorite(restaurantFavoriteList, mRestaurantList);
            listViewAdapter = new ListViewAdapter(mRestaurantList);
            mRecyclerView.setAdapter(listViewAdapter);
        });

        mViewModel.getAllRestaurants().observe(getViewLifecycleOwner(), restaurants -> {
            if (!userInterestedList.isEmpty()) {
                mViewModel.setNumberOfFriendInterested(userInterestedList, restaurants);
            }
            if (!mRestaurantList.isEmpty()) {
                mRestaurantList.clear();
            }
            mRestaurantList.addAll(restaurants);
            mViewModel.setRestaurantFavorite(mRestaurantFavoriteList, restaurants);
            listViewAdapter = new ListViewAdapter(restaurants);
            mRecyclerView.setAdapter(listViewAdapter);
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.sort_menu_toolbar_search_btn) {
            List<Place.Field> fields = Arrays.asList(Place.Field.ID);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                    .setTypeFilter(TypeFilter.ESTABLISHMENT)
                    .setLocationBias(mApiService.getRectangularBound(MapViewFragment.CURRENT_LOCATION))
                    .setCountry("FR")
                    .setHint("Search restaurant")
                    .build(requireContext());
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);

        } else if (id == R.id.sort_menu_interested_ascending) {
            mSortMethod = SortMethod.INTERESTED_ASCENDING;

        } else if (id == R.id.sort_menu_interested_descending) {
            mSortMethod = SortMethod.INTERESTED_DESCENDING;

        } else if (id == R.id.sort_menu_rating_ascending) {
            mSortMethod = SortMethod.RATING_ASCENDING;

        } else if (id == R.id.sort_menu_rating_descending) {
            mSortMethod = SortMethod.RATING_DESCENDING;

        } else if (id == R.id.sort_menu_distance_ascending) {
            mSortMethod = SortMethod.DISTANCE_ASCENDING;

        } else if (id == R.id.sort_menu_distance_descending) {
            mSortMethod = SortMethod.DISTANCE_DESCENDING;

        } else if (id == R.id.sort_menu_favorite_ascending) {
            mSortMethod = SortMethod.FAVORITE_ASCENDING;

        } else if (id == R.id.sort_menu_favorite_descending) {
            mSortMethod = SortMethod.FAVORITE_DESCENDING;

        } else if (id == R.id.sort_menu_searched_ascending) {
            mSortMethod = SortMethod.SEARCHED_ASCENDING;

        } else if (id == R.id.sort_menu_searched_descending) {
            mSortMethod = SortMethod.SEARCHED_DESCENDING;
        }
        mApiService.restaurantComparator(mRestaurantList, mSortMethod);
        listViewAdapter.updateRestaurantListOrder(mRestaurantList);
        return false;
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
