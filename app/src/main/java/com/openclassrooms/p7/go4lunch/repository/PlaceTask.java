package com.openclassrooms.p7.go4lunch.repository;

import android.os.AsyncTask;

import androidx.lifecycle.MutableLiveData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("deprecation")
public class PlaceTask extends AsyncTask<String, Integer, List<String>> {

    private final GoogleMapsHelper mGoogleMapsHelper = GoogleMapsHelper.getInstance();

    private final MutableLiveData<List<String>> listOfPlaceId = new MutableLiveData<>();

    public MutableLiveData<List<String>> getListOfPlaceId() {
        return listOfPlaceId;
    }

    // Get the data from the url
    @Override
    protected List<String> doInBackground(String... strings) {
        String data = null;
        try {
            data = mGoogleMapsHelper.downloadUrl(strings[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        JsonParser jsonParser = new JsonParser();
        List<String> listOfId = null;
        JSONObject object;
        try {
            object = new JSONObject(Objects.requireNonNull(data));
            listOfId = jsonParser.parseResult(object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        while (Objects.requireNonNull(listOfId).size() > 5) {
            listOfId.remove(listOfId.size() - 1);
        }
        return listOfId;

    }

    // Execute the task to handle the data.
    @Override
    protected void onPostExecute(List<String> listOfId) {
        getListOfPlaceId().setValue(listOfId);
    }
}
