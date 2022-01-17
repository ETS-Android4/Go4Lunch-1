package com.openclassrooms.p7.go4lunch.repository;

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
    private String parseJsonObject(JSONObject object){
        String dataList = null;
        try {
            dataList = object.getString("place_id");
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return dataList;
    }

    // Create a list where each elements are composed with placeId, latitude and longitude
    private List<String> parseJsonArray(JSONArray jsonArray) {
        List<String> dataList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++){
            try {
                String data = parseJsonObject((JSONObject) jsonArray.get(i));
                dataList.add(data);
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return dataList;
    }

    // The list previously created can be handled.
    public List<String> parseResult(JSONObject object) {
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
