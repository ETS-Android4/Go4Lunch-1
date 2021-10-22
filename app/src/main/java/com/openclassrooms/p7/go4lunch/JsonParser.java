package com.openclassrooms.p7.go4lunch;

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
    private HashMap<String, String> parseJsonObject(JSONObject object){
        HashMap<String,String> dataList = new HashMap<>();
        try {
            String name = object.getString("name");
            String placeId = object.getString("place_id");
            String latitude = object.getJSONObject("geometry").getJSONObject("location").getString("lat");
            String longitude = object.getJSONObject("geometry").getJSONObject("location").getString("lng");
            String adress = object.getString("vicinity");
            dataList.put("name", name);
            dataList.put("placeId", placeId);
            dataList.put("adress", adress);
            dataList.put("lat", latitude);
            dataList.put("lng", longitude);
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return dataList;
    }

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
