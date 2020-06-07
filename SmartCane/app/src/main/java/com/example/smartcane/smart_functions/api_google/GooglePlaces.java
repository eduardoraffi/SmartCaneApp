package com.example.smartcane.smart_functions.api_google;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.smartcane.smart_functions.api_google.model.GooglePlace;
import com.example.smartcane.smart_functions.location_preferences.AsyncResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class GooglePlaces extends AsyncTask<Context, String, ArrayList> {

    private AsyncResponse mAsyncResponse;
    private ArrayList<GooglePlace> mArrayList;
    private ArrayList<String> mUrlString;


    public GooglePlaces(ArrayList<String> urlString, AsyncResponse response) {
        mAsyncResponse = response;
        mUrlString = urlString;
        mArrayList = new ArrayList<>();
    }

    @Override
    protected ArrayList doInBackground(Context... context) {
        for (String url : mUrlString) {
            makeCall(url);
        }
        return mArrayList;
    }

    @Override
    protected void onPreExecute() {
        mAsyncResponse.startLoading();
    }

    @Override
    protected void onPostExecute(ArrayList result) {
        mAsyncResponse.processFinish(result);
    }

    private void makeCall(String stringUrl) {
        URL url;
        StringBuilder jsonResults = new StringBuilder();
        HttpURLConnection urlConnection = null;
        try {
            url = new URL(stringUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(urlConnection.getInputStream());

            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(GooglePlaces.class.getName(), "Error processing Places API URL", e);
        } catch (IOException e) {
            Log.e(GooglePlaces.class.getName(), "Error connecting to Places API", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        try {
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("results");
            parseGoogleParse(predsJsonArray);

        } catch (JSONException e) {
            Log.e(GooglePlaces.class.getName(), "Error processing JSON results", e);
        }
    }

    private void parseGoogleParse(JSONArray jsonArray) {
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                GooglePlace poi = new GooglePlace();
                if (jsonArray.getJSONObject(i).has("name")) {
                    poi.setName(jsonArray.getJSONObject(i).optString("name"));
                    poi.setRating(jsonArray.getJSONObject(i).optString("rating", " "));

                    if (jsonArray.getJSONObject(i).has("opening_hours")) {
                        if (jsonArray.getJSONObject(i).getJSONObject("opening_hours").has("open_now")) {
                            if (jsonArray.getJSONObject(i).getJSONObject("opening_hours").getString("open_now").equals("true")) {
                                poi.setOpenNow("YES");
                            } else {
                                poi.setOpenNow("NO");
                            }
                        }
                    } else {
                        poi.setOpenNow("Not Known");
                    }
                    if (jsonArray.getJSONObject(i).has("types")) {
                        JSONArray typesArray = jsonArray.getJSONObject(i).getJSONArray("types");

                        for (int j = 0; j < typesArray.length(); j++) {
                            poi.setCategory(typesArray.getString(j) + ", " + poi.getCategory());
                        }
                    }
                    if (jsonArray.getJSONObject(i).has("vicinity")) {
                        poi.setVicinity(jsonArray.getJSONObject(i).optString("vicinity"));
                    }
                }
                mArrayList.add(poi);
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }
}
