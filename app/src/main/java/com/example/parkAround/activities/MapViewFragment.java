package com.example.parkAround.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.examplehttpurlconnection.R;
import com.example.parkAround.network.SendPostRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.Iterator;

import static android.content.Context.MODE_PRIVATE;

public class MapViewFragment extends Fragment {

    MapView mMapView;
    NavigationView navigationView;
    private GoogleMap googleMap;
    EditText mAddress;
    ImageButton imgBtn;
    ImageButton burgerBtn;
    DrawerLayout drawerLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.location_fragment, container, false);
        final View v = rootView;

        FloatingActionButton fab = rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nextActivity;
                System.out.println("go to mainActivity");
                nextActivity = new Intent(getContext(), AddParkingSpaceActivity.class);
                startActivity(nextActivity);
            }
        });

        mAddress = (EditText) rootView.findViewById(R.id.search);
        drawerLayout = rootView.findViewById(R.id.drawerlayout);

        burgerBtn = (ImageButton) rootView.findViewById(R.id.hamburger_btn);
        burgerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getActivity().getApplicationContext();
                hideKeyboardFrom(context, v);
                drawerLayout .openDrawer(Gravity.LEFT);
            }
        });

        imgBtn = (ImageButton) rootView.findViewById(R.id.search_button);
        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendPostRequest request = null;
                try {
                    googleMap.clear();
                    request = new SendPostRequest(new URL("http://parkaround.herokuapp.com/api/getParkingSpacesByAddress"));

                    String address = mAddress.getText().toString();
                    if(!address.equals("")) {
                        JSONObject response = request.execute("?address=" + address).get();
                        System.out.println("raspuns :" + response);

                        Iterator<String> keys = response.keys();
                        while (keys.hasNext()) {
                            String key = keys.next();
                            JSONObject row = new JSONObject(response.get(key).toString());
                            addMarker(row);
                            LatLng point = new LatLng(Float.parseFloat(row.get("latitude").toString()), Float.parseFloat(row.get("longitude").toString()));
                            zoom(point);
                            System.out.println("key: " + row.get("address"));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }
        });

        navigationView = (NavigationView) rootView.findViewById(R.id.nav_view);
        navigationView.bringToFront();

        View headerLayout = navigationView.getHeaderView(0);
        TextView userEditText = (TextView) headerLayout.findViewById(R.id.user);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Login", MODE_PRIVATE);
        String userName = sharedPreferences.getString("name", null);
        if (userName != null) {
            userEditText.setText("Hello, " + userName +"!");
        }
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                Context context = getActivity().getApplicationContext();
                hideKeyboardFrom(context, v);
                System.out.println("da");
                if (menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                //Closing drawer on item click
                drawerLayout.closeDrawers();
                System.out.println("1");
                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {

                    //Replacing the main content with ContentFragment

                    case R.id.nav_logout:
                    {
                        System.out.println("da");
                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Login", MODE_PRIVATE);
                        SharedPreferences.Editor Ed = sharedPreferences.edit();
                        Ed.putString("name", null);
                        Ed.putString("id", null);
                        Ed.putString("email", null);
                        Ed.putString("phone", null);
                        Ed.putString("ecryptedPassword", null);
                        Ed.commit();
                        getActivity().finishAffinity();
                    }
                        return true;
                }
                return false;
            }
        });

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                if (checkLocationPermission()) {
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        googleMap.setMyLocationEnabled(true);
                    }
                }

                if (mMapView != null &&
                        mMapView.findViewById(Integer.parseInt("1")) != null) {
                    // Get the button view
                    View locationButton = ((View) mMapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
                    // and next place it, on bottom right (as Google Maps app)
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                            locationButton.getLayoutParams();
                    // position on right bottom
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                    layoutParams.setMargins(0, 0, 30, 200);
                }



                googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                googleMap.getUiSettings().setCompassEnabled(true);
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                googleMap.getUiSettings().setRotateGesturesEnabled(true);

                SendPostRequest request = null;
                try {
                    request = new SendPostRequest(new URL("http://parkaround.herokuapp.com/api/getParkingSpacesByDirectLocation"));

                    double lat = getLatitude();
                    double lng = getLongitude();
                    JSONObject response = request.execute("?latitude=" + lat + "&longitude=" + lng).get();
                    LatLng point = new LatLng(lat, lng);
                    zoom(point);

                    System.out.println("raspuns :" + response);
                    Iterator<String> keys = response.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        JSONObject row = new JSONObject(response.get(key).toString());
                        addMarker(row);
                        System.out.println("key: " + row.get("address"));

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return rootView;
    }

    public double getLatitude() {
        Context context = getActivity().getApplicationContext();
        LocationTracker locationTracker = new LocationTracker(context);
        return locationTracker.getLocation().getLatitude();
    }

    public double  getLongitude() {
        Context context = getActivity().getApplicationContext();
        LocationTracker locationTracker = new LocationTracker(context);
        return locationTracker.getLocation().getLongitude();
    }

    public void addMarker(JSONObject row) throws JSONException {
        LatLng point = new LatLng(Float.parseFloat(row.get("latitude").toString()), Float.parseFloat(row.get("longitude").toString()));
        googleMap.addMarker(new MarkerOptions().position(point).
                title(row.get("address").toString()).snippet(row.get("details").toString()));
    }
    public void zoom (LatLng point) {
        CameraPosition cameraPosition = new CameraPosition.Builder().target(point).zoom(12).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition
                (cameraPosition));
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    public void hideKeyboardFrom(Context context, View view) {
        System.out.println("inchid tastatura");
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(getActivity())
                        .setTitle("")
                        .setMessage("")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(), new String[]
                                        {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        googleMap.setMyLocationEnabled(true);
                    }

                } else {

                }
                return;
            }
        }
    }
}