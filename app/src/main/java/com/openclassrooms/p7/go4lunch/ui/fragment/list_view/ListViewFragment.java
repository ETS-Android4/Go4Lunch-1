package com.openclassrooms.p7.go4lunch.ui.fragment.list_view;

import static com.openclassrooms.p7.go4lunch.ui.activity.MainActivity.hideKeyboard;
import static com.openclassrooms.p7.go4lunch.ui.activity.MainActivity.showKeyboard;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.openclassrooms.p7.go4lunch.R;
import com.openclassrooms.p7.go4lunch.ViewModelFactory;
import com.openclassrooms.p7.go4lunch.databinding.FragmentListViewBinding;
import com.openclassrooms.p7.go4lunch.injector.DI;
import com.openclassrooms.p7.go4lunch.model.Restaurant;
import com.openclassrooms.p7.go4lunch.model.RestaurantFavorite;
import com.openclassrooms.p7.go4lunch.model.SortMethod;
import com.openclassrooms.p7.go4lunch.model.User;
import com.openclassrooms.p7.go4lunch.service.ApiService;
import com.openclassrooms.p7.go4lunch.ui.UserAndRestaurantViewModel;
import com.openclassrooms.p7.go4lunch.ui.fragment.map_view.MapViewAutocompleteAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lleotraas on 14.
 */
public class ListViewFragment extends Fragment implements MapViewAutocompleteAdapter.ClickListener{

    private RecyclerView mRecyclerView;
    private ListViewAdapter listViewAdapter;
    private UserAndRestaurantViewModel mViewModel;
    private ApiService mApiService;
    private final List<Restaurant> mRestaurantList = new ArrayList<>();
    private final List<RestaurantFavorite> mRestaurantFavoriteList = new ArrayList<>();
    private SortMethod mSortMethod;
    private FragmentListViewBinding mBinding;
    private MapViewAutocompleteAdapter mMapViewAutocompleteAdapter;
    private RecyclerView mRecyclerViewSearchResult;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentListViewBinding.inflate(inflater, container, false);
        View root = mBinding.getRoot();
        mRecyclerView = mBinding.listViewRecyclerView;
        listViewAdapter = new ListViewAdapter(mRestaurantList);
        mRecyclerView.setAdapter(listViewAdapter);
        this.configureServiceAndViewModel();
        this.createAutocomplete();
        this.setHasOptionsMenu(true);
        this.initList();
        this.configureListeners();
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
            mApiService.restaurantComparator(mRestaurantList, mSortMethod);
            listViewAdapter.updateRestaurantListOrder(mRestaurantList);
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
            mApiService.restaurantComparator(restaurants, mSortMethod);
            listViewAdapter.updateRestaurantListOrder(restaurants);
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.sort_menu_toolbar_search_btn) {
            if (mBinding.placeSearch.getVisibility() == View.GONE) {
                mBinding.placeSearch.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.VISIBLE);
                mBinding.placeSearch.requestFocus();
                showKeyboard(requireActivity());
            } else {
                mBinding.placeSearch.setVisibility(View.GONE);
                mBinding.placesRecyclerView.setVisibility(View.GONE);
                hideKeyboard(requireActivity(), mBinding.placeSearch);
            }

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
        this.getRestaurantList();
        return super.onOptionsItemSelected(item);
    }

    private void getRestaurantList() {
        mViewModel.getAllRestaurants().observe(getViewLifecycleOwner(), this::updateRestaurants);
    }

    private void updateRestaurants(List<Restaurant> restaurantList) {
        mApiService.restaurantComparator(restaurantList, mSortMethod);
        listViewAdapter.updateRestaurantListOrder(restaurantList);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void createAutocomplete() {
        mRecyclerViewSearchResult = mBinding.placesRecyclerView;
        mBinding.placeSearch.addTextChangedListener(filterTextWatcher);
        mMapViewAutocompleteAdapter = new MapViewAutocompleteAdapter(requireContext());
        mRecyclerViewSearchResult.setLayoutManager(new LinearLayoutManager(requireContext()));
        mMapViewAutocompleteAdapter.setClickListener(this);
        mRecyclerViewSearchResult.setAdapter(mMapViewAutocompleteAdapter);
        mMapViewAutocompleteAdapter.notifyDataSetChanged();
    }

    private final TextWatcher filterTextWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable editable) {
            if (!editable.toString().equals("")) {
                mMapViewAutocompleteAdapter.getFilter().filter(editable.toString());
                if (mRecyclerViewSearchResult.getVisibility() == View.GONE) {mRecyclerViewSearchResult.setVisibility(View.VISIBLE);}
            } else {
                if (mRecyclerViewSearchResult.getVisibility() == View.VISIBLE) {mRecyclerViewSearchResult.setVisibility(View.GONE);}
            }
        }
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }
    };

    @Override
    public void click(List<String> listOfPlaceId) {
        mViewModel.requestForPlaceDetails(listOfPlaceId, requireContext(), true);
        mBinding.placeSearch.setVisibility(View.GONE);
        mBinding.placesRecyclerView.setVisibility(View.GONE);
        hideKeyboard(requireActivity(), mBinding.placeSearch);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void configureListeners() {
        mBinding.placeSearch.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                mBinding.placeSearch.setVisibility(View.GONE);
                mBinding.placesRecyclerView.setVisibility(View.GONE);
                hideKeyboard(requireActivity(), mBinding.placeSearch);
            }
            return false;
        });
    }
}
