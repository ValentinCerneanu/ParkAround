package com.example.parkAround.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;


import com.example.examplehttpurlconnection.R;

import static com.example.parkAround.activities.LoginActivity.act;

public class DrawerHeader extends AppCompatActivity {
    public String user;
    public EditText userEditText;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_header);
        userEditText = (EditText) findViewById(R.id.nav_view);
        SharedPreferences sharedPreferences = getSharedPreferences("Login", MODE_PRIVATE);
        String user = sharedPreferences.getString("name", null);
        if(user != null) {
            userEditText.setText(user);
        }
    }
}
