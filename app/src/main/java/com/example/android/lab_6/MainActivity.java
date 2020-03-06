package com.example.android.lab_6;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MainActivity extends AppCompatActivity {

    private final LatLng mDestinationLatLng = new LatLng(43.0752778, -89.4041667);
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final int PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 12;
    Location mLastKnownLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SupportMapFragment mapFragment =
                (SupportMapFragment)
                        getSupportFragmentManager().findFragmentById(R.id.fragment_map);
        mapFragment.getMapAsync(googleMap -> {
            mMap = googleMap;
            googleMap.addMarker(new MarkerOptions()
                    .position(mDestinationLatLng)
                    .title("Destination"));
            //googleMap.addMarker(new MarkerOptions()
                    //.position(new LatLng(mLastKnownLocation.getLatitude(),
                            //mLastKnownLocation.getLongitude()))
                                    //.title("Current"));
            displayMyLocation();
        });

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


    }

    private void displayMyLocation(){
        int permission = ActivityCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION);

        if(permission == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
        }else{
            mFusedLocationProviderClient.getLastLocation()
                    .addOnCompleteListener(this, task -> {
                         mLastKnownLocation = task.getResult();
                        if(task.isSuccessful() && mLastKnownLocation != null){
                            mMap.addPolyline(new PolylineOptions().add(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), mDestinationLatLng
                            ));

                            SupportMapFragment mapFrag2 = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_map);
                            mapFrag2.getMapAsync(googleMap -> {
                                mMap = googleMap;
                                googleMap.addMarker(new MarkerOptions()
                                                    .position(new LatLng(mLastKnownLocation.getLatitude(),
                                                            mLastKnownLocation.getLongitude()))
                                                    .title("Current"));
                            });

                        }
                    });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permission,
                                           @NonNull int[] grantResults){
        if(requestCode == PERMISSION_REQUEST_ACCESS_FINE_LOCATION){
            if(grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                displayMyLocation();
            }
        }
    }


}
