package com.breno.projeto;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.breno.projeto.model.Operacoes;
import com.breno.projeto.sqlite.MySQLiteHelper;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.content.Context;
import android.graphics.Color;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {

    private static TextView internetStatus;

    private ListView lv;

    //local

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int REQUEST_PERMISSIONS_LOCATION_SETTINGS_REQUEST_CODE = 33;
    private static final int REQUEST_PERMISSIONS_LAST_LOCATION_REQUEST_CODE = 34;
    private static final int REQUEST_PERMISSIONS_CURRENT_LOCATION_REQUEST_CODE = 35;

    private FusedLocationProviderClient mFusedLocationClient;

    protected static long MIN_UPDATE_INTERVAL = 30 * 1000; // 1  minute is the minimum Android recommends, but we use 30 seconds

    protected Location mLastLocation;

    private TextView resultTextView;
    LocationRequest locationRequest;
    Location lastLocation = null;
    Location currentLocation = null;

    private Geocoder geocoder;

    Context mContext;

    private BroadcastReceiver broadcastReceiverConnectionChanged = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(!ChecaInternet.isNetworkAvailable(MainActivity.this)){
                changeTextStatus(false);
            }
            else{
                changeTextStatus(true);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        internetStatus = (TextView) findViewById(R.id.internet_status);

        MySQLiteHelper db = new MySQLiteHelper(this);

        lv = (ListView) findViewById(R.id.list);

        List<Operacoes> list = db.getAllOperacoes();

        ArrayAdapter<Operacoes> adapter = new ArrayAdapter<Operacoes>(this,android.R.layout.simple_list_item_1,list);

        lv.setAdapter(adapter);

        resultTextView = (TextView) findViewById((R.id.country));

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        geocoder = new Geocoder(this);

        checkForLocationRequest();
        checkForLocationSettings();


        /**
         * CRUD Operations
         * */
        // add Books
        //db.addOperacao(new Operacoes("Android Application Development Cookbook", "Wei Meng Lee"));
        //db.addOperacao(new Operacoes("Android Programming: The Big Nerd Ranch Guide", "Bill Phillips and Brian Hardy"));
        //db.addOperacao(new Operacoes("Learn Android App Development", "Wallace Jackson"));

        // get all books
        //List<Operacoes> list = db.getAllOperacoes();

        // delete one book
        //db.deleteOperacao(list.get(0));

        // get all books
        //db.getAllOperacoes();

    }

    public void mudarActivity(View view) {
        Intent intent = new Intent(this, CorretorasActivity.class);
        startActivity(intent);
    }

    public void cadastroActivity(View view) {
        Intent intent = new Intent(this, CadastroActivity.class);
        startActivity(intent);
    }

    public void deleteActivity(View view) {
        MySQLiteHelper db = new MySQLiteHelper(this);
        db.deleteAllOperacaoes();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    // Method to change the text status
    public void changeTextStatus(boolean isConnected) {

        // Change status according to boolean value
        if (isConnected) {
            internetStatus.setText("Conectado a Internet.");
            internetStatus.setTextColor(Color.parseColor("#00ff00"));
        } else {
            internetStatus.setText("Desconectado da Internet.");
            internetStatus.setTextColor(Color.parseColor("#ff0000"));
        }
    }

    @Override
    protected void onPause() {

        super.onPause();
        //  remove broadcast receiver when activity stops
        unregisterReceiver(broadcastReceiverConnectionChanged);
        super.onPause();
    }

    @Override
    protected void onResume() {

        super.onResume();
        //  register broadcast receiver after starting activity
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(broadcastReceiverConnectionChanged, intentFilter);

        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int result = googleApiAvailability.isGooglePlayServicesAvailable(this);

        if (result != ConnectionResult.SUCCESS && result != ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED) {
            Toast.makeText(this, "Are you running in Emulator ? try a real device.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void callLastKnownLocation(View view) {
        try {
            if (
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                requestPermissions(REQUEST_PERMISSIONS_LAST_LOCATION_REQUEST_CODE);
                return;
            }

            getLastLocation();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void callCurrentLocation(View view) {

        try {

            if (
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    ) {
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                requestPermissions(REQUEST_PERMISSIONS_CURRENT_LOCATION_REQUEST_CODE);
                return;
            }

            mFusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {

                    currentLocation = (Location) locationResult.getLastLocation();

                    try {

                        List<Address> addresses = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
                        Address obj = addresses.get(0);
                        String result = obj.getCountryName();
                        resultTextView.setText(result);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }, Looper.myLooper());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    @SuppressWarnings("MissingPermission")
    private void getLastLocation() {

        mFusedLocationClient.getLastLocation()
                .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            mLastLocation = task.getResult();

                            String result = "Last known Location Latitude is " +
                                    mLastLocation.getLatitude() + "\n" +
                                    "Last known longitude Longitude is " + mLastLocation.getLongitude();

                            resultTextView.setText(result);
                        } else {
                            //showSnackbar("No Last known location found. Try current location..!");
                        }
                    }
                });
    }

    private void startLocationPermissionRequest(int requestCode) {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, requestCode);
    }

    private void requestPermissions(final int requestCode) {
        boolean shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            //showSnackbar("Permission is must to find the location", "Ok",
             //       new View.OnClickListener() {
               //         @Override
                //        public void onClick(View view) {
                            // Request permission
                  //          startLocationPermissionRequest(requestCode);
                   //     }
                   // });

        } else {
            startLocationPermissionRequest(requestCode);
        }
    }

    public void checkForLocationRequest(){
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(MIN_UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    //Check for location settings.
    public void checkForLocationSettings() {
        try {
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
            builder.addLocationRequest(locationRequest);
            SettingsClient settingsClient = LocationServices.getSettingsClient(MainActivity.this);

            settingsClient.checkLocationSettings(builder.build())
                    .addOnSuccessListener(MainActivity.this, new OnSuccessListener<LocationSettingsResponse>() {
                        @Override
                        public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                            //Setting is success...
                            Toast.makeText(MainActivity.this, "Enabled the Location successfully. Now you can press the buttons..", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(MainActivity.this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {


                            int statusCode = ((ApiException) e).getStatusCode();
                            switch (statusCode) {
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                                    try {
                                        // Show the dialog by calling startResolutionForResult(), and check the
                                        // result in onActivityResult().
                                        ResolvableApiException rae = (ResolvableApiException) e;
                                        rae.startResolutionForResult(MainActivity.this, REQUEST_PERMISSIONS_LOCATION_SETTINGS_REQUEST_CODE);
                                    } catch (IntentSender.SendIntentException sie) {
                                        sie.printStackTrace();
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    Toast.makeText(MainActivity.this, "Setting change is not available.Try in another device.", Toast.LENGTH_LONG).show();
                            }

                        }
                    });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS_LAST_LOCATION_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                getLastLocation();
            }
        }

        if (requestCode == REQUEST_PERMISSIONS_CURRENT_LOCATION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callCurrentLocation(null);
            }
        }
    }

}
