package com.example.parkAround.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.example.examplehttpurlconnection.R;
import com.example.parkAround.network.BCrypt;
import com.example.parkAround.network.SendPostRequest;

import org.json.JSONObject;

import java.net.URL;

public class AddParkingSpaceActivity extends AppCompatActivity {
    AppCompatButton btn;
    EditText text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_parking_space_activity);

        text = findViewById(R.id.address);

        btn = findViewById(R.id.btn_register_parking);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendPostRequest request = null;
                try {
                    request = new SendPostRequest(new URL("http://parkaround.herokuapp.com/api/addParkingSpotByDirectLocation"));

                    String address = text.getText().toString();

                    JSONObject response = request.execute("?id=" + getId()
                            + "&latitude=" + getLatitude()
                            + "&longitude=" + getLongitude()
                            + "&address=" + address).get();

                    System.out.println("raspuns :" + response);

                    System.out.println("raspuns :" + response);
                    Intent nextActivity;
                    System.out.println("go to mainActivity");
                    nextActivity = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(nextActivity);
                    finish();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }
    public String getId(){
        SharedPreferences sharedPreferences = getSharedPreferences("Login", MODE_PRIVATE);
        return sharedPreferences.getString("id", null);
    }

    public double getLatitude(){
        Context context = getApplicationContext();
        LocationTracker locationTracker = new LocationTracker(context);
        return locationTracker.getLocation().getLatitude();
    }

    public double getLongitude(){
        Context context = getApplicationContext();
        LocationTracker locationTracker = new LocationTracker(context);
        return locationTracker.getLocation().getLongitude();
    }


}
