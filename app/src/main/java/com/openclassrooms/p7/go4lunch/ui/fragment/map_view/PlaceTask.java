package com.openclassrooms.p7.go4lunch.ui.fragment.map_view;

import android.os.AsyncTask;

import java.io.IOException;

public class PlaceTask extends AsyncTask<String, Integer, String> {
    @Override
    protected String doInBackground(String... strings) {
        String data = null;
        try {
            data = downloadUrl(strings[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
    // Execute the task to handle the data.
    @Override
    protected void onPostExecute(String s) {
        new ParserTask().execute(s);
    }
}
