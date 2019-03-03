package com.example.parkAround.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.example.examplehttpurlconnection.R;
import com.example.parkAround.network.SendPostRequest;


import org.json.JSONObject;

import java.net.URL;
import java.util.Iterator;

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
    }

}
