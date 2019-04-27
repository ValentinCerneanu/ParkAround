package com.example.parkAround.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.examplehttpurlconnection.R;

import static com.example.parkAround.activities.LoginActivity.act;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //finish the login activity
        if(act != null)
            act.finish();
    }

}
