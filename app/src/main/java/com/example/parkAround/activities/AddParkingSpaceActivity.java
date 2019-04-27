package com.example.parkAround.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
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
import java.util.List;

import ca.antonious.materialdaypicker.MaterialDayPicker;

public class AddParkingSpaceActivity extends AppCompatActivity {
    AppCompatButton btn;
    EditText addressEditText;
    EditText detailsEditText;
    EditText priceEditText;
    TimePickerFragment startTimePicker = new TimePickerFragment();
    TimePickerFragment endTimePicker = new TimePickerFragment();
    MaterialDayPicker materialDayPicker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_parking_space_activity);

        addressEditText = findViewById(R.id.address);
        detailsEditText = findViewById(R.id.details);
        priceEditText = findViewById(R.id.price);
        materialDayPicker = findViewById(R.id.day_picker);

        btn = findViewById(R.id.btn_register_parking);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendPostRequest request = null;
                try {
                    request = new SendPostRequest(new URL("http://parkaround.herokuapp.com/api/addParkingSpotByDirectLocation"));

                    String address = addressEditText.getText().toString();
                    String details = detailsEditText.getText().toString();
                    String price = priceEditText.getText().toString();
                    String startTime = startTimePicker.getHourOfDay() + ":" + startTimePicker.getMinute();
                    String endTime = endTimePicker.getHourOfDay() + ":" + endTimePicker.getMinute();
                    List<MaterialDayPicker.Weekday> daysSelected = materialDayPicker.getSelectedDays();
                    JSONObject response = request.execute("?id=" + getId()
                            + "&latitude=" + getLatitude()
                            + "&longitude=" + getLongitude()
                            + "&address=" + address
                            + "&details=" + details
                            + "&start_time=" + startTime
                            + "&end_time=" + endTime
                            + "&price=" + price
                            + "&daysSelected=" + daysSelected.toString()).get();

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

    public void showTimePickerStartTime(View v) {
        startTimePicker.show(getSupportFragmentManager(), "timePicker");
    }

    public void showTimePickerEndTime(View v) {
        endTimePicker.show(getSupportFragmentManager(), "timePicker");
    }

    public String getId(){
        SharedPreferences sharedPreferences = getSharedPreferences("Login", MODE_PRIVATE);
        return sharedPreferences.getString("id", null);
    }

    public double getLatitude(){
        Context context = getApplicationContext();
        LocationTracker locationTracker = new LocationTracker(context);
        locationTracker.getLocation();
        return locationTracker.getLatitude();
    }

    public double getLongitude(){
        Context context = getApplicationContext();
        LocationTracker locationTracker = new LocationTracker(context);
        locationTracker.getLocation();
        return locationTracker.getLongitude();
    }
}
