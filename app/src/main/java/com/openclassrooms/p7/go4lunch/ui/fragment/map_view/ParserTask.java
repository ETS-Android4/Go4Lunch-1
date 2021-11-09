package com.openclassrooms.p7.go4lunch.ui.fragment.map_view;

import android.os.AsyncTask;

import com.openclassrooms.p7.go4lunch.injector.DI;
import com.openclassrooms.p7.go4lunch.model.Restaurant;
import com.openclassrooms.p7.go4lunch.service.RestaurantApiService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {
    // Return a list contains id, name, adress, latitude and longitude of the restaurants.
    @Override
    protected List<HashMap<String, String>> doInBackground(String... strings) {
        JsonParser jsonParser = new JsonParser();
        List<HashMap<String, String>> mapList = null;
        JSONObject object;
        try {
            object = new JSONObject(strings[0]);
            mapList = jsonParser.parseResult(object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mapList;
    }
    // Put a marker on each restaurant found,
    @Override
    protected void onPostExecute(List<HashMap<String, String>> hashMaps) {
        RestaurantApiService apiService = DI.getRestaurantApiService();
        for (int i = 0; i < hashMaps.size(); i++) {
            apiService.getNearbyRestaurantsInfos(hashMaps, i);
        }
    }
}