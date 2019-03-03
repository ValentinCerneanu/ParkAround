package com.example.parkAround.network;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class SendPostRequest extends AsyncTask<String, Void, JSONObject> {
    private URL url;

    public SendPostRequest(URL url) {
        this.url = url;
    }

    protected void onPreExecute() {
    }

    protected JSONObject doInBackground(String... arg0)  {

        try {

            url = new URL(url.toString() + arg0[0]);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("POST");

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line+"\n");
            }
            br.close();

            JSONObject responseBody = new JSONObject(sb.toString());

            int responseCode = conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                return responseBody;
            } else {
                String s = "{\"responseCode\":" + "\"" + responseCode +"\"}";
                return new JSONObject(s);
            }
        } catch (Exception e) {
            String s = "{\"Exception\":" + "\"" + e.getMessage() +"\"}";
            try {
                return new JSONObject(s);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        //Toast.makeText(getApplicationContext(), result,
        //Toast.LENGTH_LONG).show();
    }

}