package com.example.parkAround.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.example.examplehttpurlconnection.R;
import com.example.parkAround.network.SendGetRequest;

import org.json.JSONException;

import java.net.MalformedURLException;
import java.net.URL;
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
        if(act != null)
            act.finish();

        this.buttonRequest = (Button) findViewById(R.id.buttonRequest);
        this.listView = (ListView) findViewById(R.id.listView1);
    }

    public void callRequest(View v) {

    }


}
