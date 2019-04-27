package com.example.parkAround.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.example.examplehttpurlconnection.R;
import com.example.parkAround.network.BCrypt;
import com.example.parkAround.network.SendGetRequest;
import com.example.parkAround.network.SendPostRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
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

    public void createAccount(View v) throws ExecutionException, InterruptedException, JSONException {
        String name = mName.getText().toString();
        String email = mEmail.getText().toString();
        String phone = mPhone.getText().toString();
        String password = mPassword.getText().toString();
        String passwordConfirmed = mConfirmPassword.getText().toString();

        View focusView = null;
        boolean cancel = false;
        //name
        if (TextUtils.isEmpty(name)) {
            mName.setError(getString(R.string.error_field_required));
            focusView = mName;
            cancel = true;
        }
        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmail.setError(getString(R.string.error_field_required));
            focusView = mEmail;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmail.setError(getString(R.string.error_invalid_email));
            focusView = mEmail;
            cancel = true;
        }
        //phone
        if (TextUtils.isEmpty(phone)) {
            mPhone.setError(getString(R.string.error_field_required));
            focusView = mPhone;
            cancel = true;
        }
        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPassword.setError(getString(R.string.error_invalid_password));
            focusView = mPassword;
            cancel = true;
        }
        if(!password.equals(passwordConfirmed)) {
            mConfirmPassword.setError(getString(R.string.error_password_not_match));
            focusView = mConfirmPassword;
            cancel = true;
        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            SendPostRequest request = null;
            try {
                request = new SendPostRequest(new URL("http://parkaround.herokuapp.com/api/addNewUser"));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            JSONObject response = request.execute("?name=" + name
                    + "&phone=" + phone
                    + "&email=" + email + "&password=" + password).get();
            System.out.println("raspuns :" + response);

            if (response.has("id")) {
                SharedPreferences sharedPreferences = getSharedPreferences("Login", MODE_PRIVATE);
                SharedPreferences.Editor ed = sharedPreferences.edit();
                ed.putString("name", name);
                ed.putString("id", response.get("id").toString());
                ed.putString("email", email);
                ed.putString("phone", phone);
                ed.putString("password", password);
                ed.commit();

                System.out.println("raspuns :" + response);
                Intent nextActivity;
                System.out.println("go to mainActivity");
                nextActivity = new Intent(getBaseContext(), MainActivity.class);
                startActivity(nextActivity);
                finish();
            } else if (response.has("phone")) {
                mPhone.setError(response.get("phone").toString());
            } else if (response.has("email")) {
                mEmail.setError(response.get("email").toString());
            }
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    public void goBackToLogin(View v){
        finish();
    }

}
