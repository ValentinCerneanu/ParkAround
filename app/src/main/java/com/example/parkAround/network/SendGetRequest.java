package com.example.parkAround.network;

import android.os.AsyncTask;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class SendGetRequest extends AsyncTask<String, Void, String> {
    private URL url;
    private JSONArray response;

    public SendGetRequest(URL url) {
        this.url = url;
    }

    protected void onPreExecute() {
    }

    protected String doInBackground(String... arg0) {

        try {

            System.out.println("ARG0:" + arg0.toString());
            url = new URL(url.toString() + arg0[0]);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            String responseBody = convertStreamToString(conn.getInputStream());
            System.out.println("responseBody :" + responseBody);

            int responseCode = conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                return responseBody;
            } else {
                return new String("false : " + responseCode);
            }
        } catch (Exception e) {
            return new String("Exception: " + e.getMessage());
        }
    }

    @Override
    protected void onPostExecute(String result) {
        //Toast.makeText(getApplicationContext(), result,
        //Toast.LENGTH_LONG).show();
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
        } finally {
            try {
                is.close();
            } catch (IOException e) {
            }
        }
        return sb.toString();
    }
}