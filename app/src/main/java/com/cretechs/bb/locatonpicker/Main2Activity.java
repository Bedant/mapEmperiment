package com.cretechs.bb.locatonpicker;

import android.content.Context;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Main2Activity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    Context context;
    Double longitude, latitude;
    GoogleMap mGoogleMap;
    private LatLng curentpoint;
    private LatLng center;
    private TextView Address;
    private LinearLayout markerLayout;
    private TextView markerText;
    private Geocoder geocoder;
    private List<Address> addresses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        markerText = (TextView) findViewById(R.id.locationMarkertext);
        Address = (TextView) findViewById(R.id.adressText);
        markerLayout = (LinearLayout) findViewById(R.id.locationMarker);
        context = this;
        if (checkPlayServices()) {

            buildGoogleApiClient();

        } else {

        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkPlayServices();
    }

    private void updateLocation() {
        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
            stupMap();

        } else {
            Toast.makeText(getApplicationContext(),
                    "Couldn't get the location. Make sure location is enabled on the device", Toast.LENGTH_LONG)
                    .show();
        }

    }


    /**
     * Google api callback methods
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
//        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
//                + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {

        updateLocation();

    }


    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }


    private void stupMap() {
        try {

            mGoogleMap = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.map)).getMap();

// Enabling MyLocation in Google Map
            mGoogleMap.setMyLocationEnabled(true);
            mGoogleMap.getUiSettings().setZoomGesturesEnabled(true);
           // mGoogleMap.getUiSettings().setZoomControlsEnabled(true);

            //mGoogleMap.getUiSettings().setRotateGesturesEnabled(false);

            // mGoogleMap.getUiSettings().setTiltGesturesEnabled(false);


//            PendingResult<Status> result = LocationServices.FusedLocationApi
//                    .requestLocationUpdates(mGoogleApiClient, mLastLocation,
//                            new LocationListener() {
//
//                                @Override
//                                public void onLocationChanged(Location location) {
//                                    markerText.setText("Location received: "
//                                            + location.toString());
//
//                                }
//                            });


//
//            Log.e("Reached", "here");
//
//            result.setResultCallback(new ResultCallback<Status>() {
//
//                @Override
//                public void onResult(Status status) {
//
//                    if (status.isSuccess()) {
//
//                        Log.e("Result", "success");
//
//                    } else if (status.hasResolution()) {
//// Google provides a way to fix the issue
//                        try {
//                            status.startResolutionForResult(Main2Activity.this,
//                                    100);
//                        } catch (IntentSender.SendIntentException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            });

            curentpoint = new LatLng(latitude, longitude);

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(curentpoint).zoom(19f).build();

            mGoogleMap.setMyLocationEnabled(true);
            mGoogleMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));
// Clears all the existing markers
            mGoogleMap.clear();

            mGoogleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

                @Override
                public void onCameraChange(CameraPosition arg0) {
// TODO Auto-generated method stub
                    center = mGoogleMap.getCameraPosition().target;

                    markerText.setText(" Set your Location ");
                    mGoogleMap.clear();
                    markerLayout.setVisibility(View.VISIBLE);

                    try {
                        new GetLocationAsync(center.latitude, center.longitude)
                                .execute();

                    } catch (Exception e) {
                    }
                }
            });

            markerLayout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
// TODO Auto-generated method stub

                    try {

                        LatLng latLng1 = new LatLng(center.latitude,
                                center.longitude);

                        Marker m = mGoogleMap.addMarker(new MarkerOptions()
                                .position(latLng1)
                                .title(" Set your Location ")
                                .snippet("")
                                .icon(BitmapDescriptorFactory
                                        .fromResource(R.drawable.ic_location74)));
                        m.setDraggable(true);

                        markerLayout.setVisibility(View.GONE);
                    } catch (Exception e) {
                    }

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class GetLocationAsync extends AsyncTask<String, Void, String> {

        // boolean duplicateResponse;
        double x, y;
        StringBuilder str;

        public GetLocationAsync(double latitude, double longitude) {
// TODO Auto-generated constructor stub

            x = latitude;
            y = longitude;
        }

        @Override
        protected void onPreExecute() {
            Address.setText(" Getting location ");
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                geocoder = new Geocoder(Main2Activity.this, Locale.ENGLISH);
                addresses = geocoder.getFromLocation(x, y, 1);
                str = new StringBuilder();
                if (Geocoder.isPresent()) {
                    Address returnAddress = addresses.get(0);

                    String localityString = returnAddress.getLocality();
                    String city = returnAddress.getCountryName();
                    String region_code = returnAddress.getCountryCode();
                    String zipcode = returnAddress.getPostalCode();

                    str.append(localityString + "");
                    str.append(city + "" + region_code + "");
                    str.append(zipcode + "");

                } else {
                }
            } catch (IOException e) {
                Log.e("tag", e.getMessage());
            }
            return null;

        }

        @Override
        protected void onPostExecute(String result) {
            try {
                Address.setText(addresses.get(0).getAddressLine(0)
                        + addresses.get(0).getAddressLine(1) + " ");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {

        }
    }


}
