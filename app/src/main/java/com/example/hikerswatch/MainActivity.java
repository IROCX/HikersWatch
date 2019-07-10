package com.example.hikerswatch;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {


    LocationManager locationManager;
    LocationListener locationListener;
    TextView latitude;
    TextView longitude;
    TextView accuracy;
    TextView altitude;
    TextView address;
    Geocoder geocoder;
    Button getLocationButton;
    Location currentLocation;


    public void updateLocation(Location location) {
        String temp = "";
        Log.i("UpdateMethod", location.toString());
        temp = "<strong>" + "Latitude : " + "</strong>" + String.format("%.4f", location.getLatitude()) + "";
        latitude.setText(Html.fromHtml(temp));
        temp = "<strong>" + "Longitude : " + "</strong>" + String.format("%.4f", location.getLongitude()) + "";
        longitude.setText(Html.fromHtml(temp));
        temp = "<strong>" + "Accuracy : " + "</strong>" + location.getAccuracy() + "";
        accuracy.setText(Html.fromHtml(temp));
        temp = "<strong>" + "Altitude : " + "</strong>" + location.getAltitude() + "";
        altitude.setText(Html.fromHtml(temp));


        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addressList != null && addressList.size() > 0) {
                if (addressList.get(0).getAddressLine(0) != null) {
                    String s = addressList.get(0).getAddressLine(0);
                    temp = "<strong>" + "Address : " + "</strong>" + s + "";
                    address.setText(Html.fromHtml(temp));
                } else {
                    address.setText("Address : Failed to fetch address.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 0, locationListener);
            }
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        latitude = findViewById(R.id.latitudeTextView);
        longitude = findViewById(R.id.longitudeTextView);
        accuracy = findViewById(R.id.accuracyTextView);
        altitude = findViewById(R.id.altitudeTextView);
        address = findViewById(R.id.addressTextView);
        getLocationButton = findViewById(R.id.button);

        getLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (currentLocation == null) {
                    Toast.makeText(getApplicationContext(), "Unable to get the location.\nPlease wait for 10-15 seconds", Toast.LENGTH_LONG).show();
                } else {
                    updateLocation(currentLocation);
                }
            }
        });

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                currentLocation = location;
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };


        if (Build.VERSION.SDK_INT < 23) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (lastLocation != null) {
                    updateLocation(lastLocation);
                }
            }
        }
    }
}
