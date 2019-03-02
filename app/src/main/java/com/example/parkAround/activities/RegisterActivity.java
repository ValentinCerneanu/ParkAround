package com.example.parkAround.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.example.examplehttpurlconnection.R;
import com.example.parkAround.network.BCrypt;
import com.example.parkAround.network.SendGetRequest;
import com.example.parkAround.network.SendPostRequest;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class RegisterActivity extends AppCompatActivity {

    private EditText mName;
    private EditText mEmail;
    private EditText mPhone;
    private EditText mPassword;
    private EditText mConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mName = (EditText) findViewById(R.id.input_name_register);
        mEmail = (EditText) findViewById(R.id.input_email_register);
        mPhone = (EditText) findViewById(R.id.input_phone_register);
        mPhone.setTransformationMethod(null);
        mPassword = (EditText) findViewById(R.id.input_password1_register);
        mConfirmPassword = (EditText) findViewById(R.id.input_password2_register);

    }

    public void createAccount(View v) throws ExecutionException, InterruptedException {
        SendPostRequest request = null;
        try {
            request = new SendPostRequest(new URL("http://parkaround.herokuapp.com/api/addNewUser"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        String name = mName.getText().toString();
        String email = mEmail.getText().toString();
        String phone = mPhone.getText().toString();

        BCrypt bCrypt = new BCrypt();
        String password = mPassword.getText().toString();
        String ecryptedPassword = bCrypt.hashpw(password, bCrypt.gensalt());

        String response = request.execute("?name=" + name
                +   "&surname=lele" + "&phone=" + phone
                + "&email=" + email + "&password=" + ecryptedPassword).get();
        System.out.println("raspuns :" + response);
        if(response.trim().equals("true")) {
            SharedPreferences sharedPreferences = getSharedPreferences("Login", MODE_PRIVATE);
            SharedPreferences.Editor Ed=sharedPreferences.edit();
            Ed.putString("name", name);
            Ed.putString("email", email);
            Ed.putString("phone", phone);
            Ed.putString("ecryptedPassword", ecryptedPassword);
            Ed.commit();

            System.out.println("raspuns :" + response);
            Intent nextActivity;
            System.out.println("go to mainActivity");
            nextActivity = new Intent(getBaseContext(), MainActivity.class);
            startActivity(nextActivity);
            finish();
        }// else if (response.equals("NOT OK") )
    }

    public void goBackToLogin(View v){
        finish();
    }

}
