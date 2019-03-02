package com.example.parkAround.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.examplehttpurlconnection.R;
import com.example.parkAround.network.SendGetRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.example.parkAround.activities.LoginActivity.act;

public class MainActivity extends AppCompatActivity {
    public Button buttonRequest;
    public ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //finish the login activity
        act.finish();

        this.buttonRequest = (Button) findViewById(R.id.buttonRequest);
        this.listView = (ListView) findViewById(R.id.listView1);

    }

    public void callRequest(View v) throws JSONException, ExecutionException, InterruptedException {
        SendGetRequest request = null;
        try {
            request = new SendGetRequest(new URL("http://parkaround.herokuapp.com/api/post"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        String response = request.execute("?name=name&email=coste51&password=123123").get();
        System.out.println("raspuns :" + response);
/*        JSONArray jsonArray = new JSONArray(response);
        System.out.println(response);
        List<String> your_array_list = new ArrayList<String>();

        for (int i = 0; i < jsonArray.length(); i++) {
            your_array_list.add(jsonArray.getJSONObject(i).get("body").toString());
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                your_array_list);
        listView.setAdapter(arrayAdapter);*/
    }


}
