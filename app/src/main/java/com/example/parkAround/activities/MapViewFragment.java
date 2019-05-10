package com.example.parkAround.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import com.google.android.gms.maps.model.Marker;
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
    ImageButton searchBarBtn;
    ImageButton burgerBtn;
    DrawerLayout drawerLayout;
    FloatingActionButton addNewParkingBtn;
    FloatingActionButton getParkingSpacesByDirectLocation;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.location_fragment, container, false);
        final View v = rootView;
        getParkingSpacesByDirectLocation = rootView.findViewById(R.id.getParkingSpacesBtn);
        getParkingSpacesByDirectLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchParkingSpotByDirectLocation();
            }
        });

        addNewParkingBtn = rootView.findViewById(R.id.addParkingSpaceBtn);
        addNewParkingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nextActivity;
                System.out.println("go to mainActivity");
                nextActivity = new Intent(getContext(), AddParkingSpaceActivity.class);
                startActivity(nextActivity);
            }
        });

        mAddress = (EditText) rootView.findViewById(R.id.search);
        mAddress.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    searchParkingSpotByAddress(mAddress.getText().toString());
                }
                return false;
            }
        });
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

        searchBarBtn = (ImageButton) rootView.findViewById(R.id.search_button);
        searchBarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            searchParkingSpotByAddress(mAddress.getText().toString());
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
                if (menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);
                //Closing drawer on item click
                drawerLayout.closeDrawers();
                switch (menuItem.getItemId()) {

                    case R.id.nav_logout:
                    {
                        System.out.println("da");
                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Login", MODE_PRIVATE);
                        SharedPreferences.Editor Ed = sharedPreferences.edit();
                        Ed.putString("name", null);
                        Ed.putString("id", null);
                        Ed.putString("email", null);
                        Ed.putString("phone", null);
                        Ed.putString("password", null);
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
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return rootView;
    }

    public void searchParkingSpotByAddress(String address) {
        SendPostRequest request = null;
        try {
            googleMap.clear();
            request = new SendPostRequest(new URL("http://parkaround.herokuapp.com/api/getParkingSpacesByAddress"));
            if(!address.equals("")) {
                JSONObject response = request.execute("?address=" + address).get();
                System.out.println("raspuns :" + response);
                if(response.has("search_error")) {
                    System.out.println("search_error");
                    AlertDialog.Builder popupEroareBuilder = new AlertDialog.Builder(getActivity());

                    popupEroareBuilder.setMessage("No parking spots found in your search area")
                            .setTitle("No parking spots");

                    final AlertDialog dialogPopupEroare = popupEroareBuilder.create();
                    dialogPopupEroare.create();
                    popupEroareBuilder.create();
                    popupEroareBuilder.setPositiveButton("Try another location", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialogPopupEroare.dismiss();
                        }
                    });
                    popupEroareBuilder.show();
                }
                else if(response.has("google_api_error")) {
                    System.out.println("google_api_error");
                    AlertDialog.Builder popupEroareBuilder = new AlertDialog.Builder(getActivity());

                    popupEroareBuilder.setMessage("Internal error. Try again in a few minutes")
                            .setTitle("Internal error");

                    final AlertDialog dialogPopupEroare = popupEroareBuilder.create();
                    popupEroareBuilder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialogPopupEroare.dismiss();
                        }
                    });
                } else {
                    Iterator<String> keys = response.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        JSONObject row = new JSONObject(response.get(key).toString());
                        addMarker(row);
                        LatLng point = new LatLng(Float.parseFloat(row.get("latitude").toString()), Float.parseFloat(row.get("longitude").toString()));
                        zoom(point);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void searchParkingSpotByDirectLocation() {
        SendPostRequest request = null;
        try {
            googleMap.clear();
            request = new SendPostRequest(new URL("http://parkaround.herokuapp.com/api/getParkingSpacesByDirectLocation"));
            double lat = getLatitude();
            double lng = getLongitude();
            JSONObject response = request.execute("?longitude=" + lng + "&latitude=" + lat).get();

            System.out.println("raspuns :" + response);
            if(response.has("search_error")) {
                System.out.println("search_error");
                AlertDialog.Builder popupEroareBuilder = new AlertDialog.Builder(getActivity());

                popupEroareBuilder.setMessage("No parking spots found in your search area")
                        .setTitle("No parking spots");

                final AlertDialog dialogPopupEroare = popupEroareBuilder.create();
                dialogPopupEroare.create();
                popupEroareBuilder.create();
                popupEroareBuilder.setPositiveButton("Try another location", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialogPopupEroare.dismiss();
                    }
                });
                popupEroareBuilder.show();
            } else {
                Iterator<String> keys = response.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    JSONObject row = new JSONObject(response.get(key).toString());
                    addMarker(row);
                    LatLng point = new LatLng(Float.parseFloat(row.get("latitude").toString()), Float.parseFloat(row.get("longitude").toString()));
                    zoom(point);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                Context mContext = getActivity().getApplicationContext();
                LinearLayout info = new LinearLayout(mContext);
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(mContext);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(mContext);
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });
        LatLng point = new LatLng(Float.parseFloat(row.get("latitude").toString()), Float.parseFloat(row.get("longitude").toString()));
        String snippet;
        String days = new String();
        if(row.get("sunday").toString().equals("1")) {
            days += "Sunday, ";
        } else if(row.get("monday").toString().equals("1")) {
            days += "Monday, ";
        } else if(row.get("tuesday").toString().equals("1")) {
            days += "Tuesday, ";
        } else if(row.get("wednesday").toString().equals("1")) {
            days += "Wednesday, ";
        } else if(row.get("thursday").toString().equals("1")) {
            days += "Thursday, ";
        } else if(row.get("friday").toString().equals("1")) {
            days += "Friday, ";
        } else if(row.get("saturday").toString().equals("1")) {
            days += "Saturday, ";
        }
        if(!days.isEmpty())
            days = days.substring(0, days.length() - 2);
        snippet = "Details: " + row.get("details").toString()
                + "\nOpen Hours: " + row.get("start_time").toString().substring(0,row.get("start_time").toString().length() - 3)
                                        + " - " + row.get("end_time").toString().substring(0,row.get("end_time").toString().length() - 3)
                + "\nOpen Days: " + days
                + "\nPrice: " + row.get("price").toString() + "lei";
        System.out.println(snippet);
        googleMap.addMarker(new MarkerOptions().position(point).
                title(row.get("address").toString())
                .snippet(snippet));
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