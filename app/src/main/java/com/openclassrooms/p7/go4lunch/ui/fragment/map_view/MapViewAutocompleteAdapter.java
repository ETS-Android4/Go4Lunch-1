package com.openclassrooms.p7.go4lunch.ui.fragment.map_view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.openclassrooms.p7.go4lunch.R;
import com.openclassrooms.p7.go4lunch.injector.DI;
import com.openclassrooms.p7.go4lunch.model.PlaceAutocomplete;
import com.openclassrooms.p7.go4lunch.service.ApiService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MapViewAutocompleteAdapter extends RecyclerView.Adapter<MapViewAutocompleteAdapter.PredictionHolder> implements Filterable {

    private static final String TAG = "PlacesAutoAdapter";
    private ArrayList<PlaceAutocomplete> mResultList = new ArrayList<>();

    private final Context mContext;
    private final CharacterStyle STYLE_BOLD;
    private final CharacterStyle STYLE_NORMAL;
    private final PlacesClient placesClient;
    private ClickListener clickListener;
    private final ApiService mApiService;

    public MapViewAutocompleteAdapter(Context context) {
        mContext = context;
        STYLE_BOLD = new StyleSpan(Typeface.BOLD);
        STYLE_NORMAL = new StyleSpan(Typeface.NORMAL);
        placesClient = com.google.android.libraries.places.api.Places.createClient(context);
        mApiService = DI.getRestaurantApiService();
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface ClickListener {
        void click(List<String> place);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                // Skip the autocomplete query if no constraints are given.
                if (constraint != null) {
                    // Query the autocomplete API for the (constraint) search string.
                    mResultList = getPredictions(constraint);
                    // The API successfully returned results.
                    results.values = mResultList;
                    results.count = mResultList.size();
                }
                return results;
    }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    // The API returned at least one result, update the data.
                    notifyDataSetChanged();
                } else {
                    // The API did not return any results, invalidate the data set.
                    Log.d(TAG, "publishResults: Result invalidate.");
                }
            }
        };
            }

    public ArrayList<PlaceAutocomplete> getPredictions(CharSequence constraint) {

        final ArrayList<PlaceAutocomplete> resultList = new ArrayList<>();

        // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
        // and once again when the user makes a selection (for example when calling fetchPlace()).
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

        // Use the builder to create a FindAutocompletePredictionsRequest.
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setCountry("FR")
                .setTypeFilter(TypeFilter.ESTABLISHMENT)
                .setSessionToken(token)
                .setQuery(constraint.toString())
                .build();

        Task<FindAutocompletePredictionsResponse> autocompletePredictions = placesClient.findAutocompletePredictions(request);

        // This method should have been called off the main UI thread. Block and wait for at most
        // 60s for a result from the API.
        try {
            Tasks.await(autocompletePredictions, 60, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            e.printStackTrace();
        }

        if (autocompletePredictions.isSuccessful()) {
            FindAutocompletePredictionsResponse findAutocompletePredictionsResponse = autocompletePredictions.getResult();
            if (findAutocompletePredictionsResponse != null)
                for (AutocompletePrediction prediction : findAutocompletePredictionsResponse.getAutocompletePredictions()) {
                    if (prediction.getPlaceTypes().toString().contains("RESTAURANT")) {
                        Log.i(TAG, prediction.getPlaceId());
                        resultList.add(new PlaceAutocomplete(prediction.getPlaceId(), prediction.getPrimaryText(STYLE_NORMAL).toString(), prediction.getFullText(STYLE_BOLD).toString()));
                    }
                }
        }
        return resultList;
    }

            @NonNull
    @Override
    public PredictionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View convertView = layoutInflater.inflate(R.layout.place_autocomplete_row, parent, false);
                return new PredictionHolder(convertView);
    }

    @Override
    public void onBindViewHolder(@NonNull PredictionHolder mPredictionHolder, int position) {
        mPredictionHolder.address.setText(mResultList.get(position).address);
    }

    @Override
    public int getItemCount() {
        return mResultList.size();
    }

    public class PredictionHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView address;

        public PredictionHolder(@NonNull View itemView) {
            super(itemView);
            address = itemView.findViewById(R.id.place_address);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            PlaceAutocomplete item = mResultList.get(getAdapterPosition());
            if (view.getId() == R.id.place_item_view) {
                String placeId = String.valueOf(item.placeId);
                List<String> listOfPlaceId = new ArrayList<>();
                listOfPlaceId.add(placeId);
                clickListener.click(listOfPlaceId);
            }
        }
    }
    }
