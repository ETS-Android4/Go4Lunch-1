package com.openclassrooms.p7.go4lunch.repository;

import android.content.Context;

import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPhotoResponse;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class GoogleMapsHelper {

    private static GoogleMapsHelper googleMapsHelper;

    public static GoogleMapsHelper getInstance() {
        if (googleMapsHelper == null) {
            googleMapsHelper = new GoogleMapsHelper();
        }
        return googleMapsHelper;
    }

    /**
     * Call to get a request for data
     * @param context context of the fragment.
     * @param placeId id of the place.
     * @return fetch place task.
     */
    public Task<FetchPlaceResponse> getPlaceData(Context context, String placeId) {
        PlacesClient placesClient = Places.createClient(context);
        List<Place.Field> placeFields = Arrays.asList(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.RATING,
                Place.Field.PHONE_NUMBER,
                Place.Field.WEBSITE_URI,
                Place.Field.OPENING_HOURS,
                Place.Field.LAT_LNG,
                Place.Field.PHOTO_METADATAS,
                Place.Field.TYPES
        );
        FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields)
                .build();
        return placesClient.fetchPlace(request);
    }

    public Task<FetchPhotoResponse> getPhotoData(Place place, Context context) {
        PlacesClient placesClient = null;
        FetchPhotoRequest photoRequest = null;
        if (place.getPhotoMetadatas() != null) {
            PhotoMetadata photoMetadata = place.getPhotoMetadatas().get(0);
            photoRequest = FetchPhotoRequest.builder(photoMetadata)
                    .build();
            placesClient = Places.createClient(context);
        }
        if (placesClient != null) {
            return placesClient.fetchPhoto(photoRequest);
        } else {
            return null;
        }
    }

    public String downloadUrl (String string) throws IOException {
        URL url = new URL(string);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.connect();
        InputStream stream = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }
        String data = builder.toString();
        reader.close();
        return data;
    }
}
