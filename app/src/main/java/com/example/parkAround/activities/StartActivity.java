package com.example.parkAround.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;

import com.example.examplehttpurlconnection.R;


public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        new CountDownTimer(2000,1000){
            @Override
            public void onTick(long millisUntilFinished){}

            @Override
            public void onFinish(){
                //set the new Content of your activity
                SharedPreferences sharedPreferences = getSharedPreferences("Login", MODE_PRIVATE);
                String email = sharedPreferences.getString("email", null);
                String password = sharedPreferences.getString("ecryptedPassword", null);
                Intent nextActivity;
                if(email == null || password == null) {
                    System.out.println("go to login");
                    nextActivity = new Intent(getBaseContext(), LoginActivity.class);
                    startActivity(nextActivity);
                    finish();
                }
                else {
                    System.out.println("automaticalyy loggin");
                    Intent intentToMainActivity = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(intentToMainActivity);
                    finish();
                }
            }
        }.start();
    }

}
