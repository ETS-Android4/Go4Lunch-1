package com.openclassrooms.p7.go4lunch.ui.fragment.map_view;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lleotraas on 18.
 */
public class JsonParser {

    // Create list for each parameters get (placeId, latitude and longitude).
    private HashMap<String, String> parseJsonObject(JSONObject object){
        HashMap<String,String> dataList = new HashMap<>();
        try {
            String placeName = object.getString("name");
            String placeAdress = object.getString("vicinity");
            String placeId = object.getString("place_id");
            String latitude = object.getJSONObject("geometry").getJSONObject("location").getString("lat");
            String longitude = object.getJSONObject("geometry").getJSONObject("location").getString("lng");
            dataList.put("placeName", placeName);
            dataList.put("placeAdress", placeAdress);
            dataList.put("placeId", placeId);
            dataList.put("lat", latitude);
            dataList.put("lng", longitude);
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return dataList;
    }

    // Create a list where each elements are composed with placeId, latitude and longitude
    private List<HashMap<String,String>> parseJsonArray(JSONArray jsonArray) {
        List<HashMap<String, String>> dataList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++){
            try {
                HashMap<String,String> data = parseJsonObject((JSONObject) jsonArray.get(i));
                dataList.add(data);
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return dataList;
    }

    // The list previously created can be handled.
    public List<HashMap<String, String>> parseResult(JSONObject object) {
        JSONArray jsonArray = null;
        try {
            jsonArray = object.getJSONArray("results");
        }catch (JSONException e) {
            e.printStackTrace();
        }
        assert jsonArray != null;
        return parseJsonArray(jsonArray);
    }
}
