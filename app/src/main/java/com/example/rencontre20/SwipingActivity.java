package com.example.rencontre20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MotionEvent;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class SwipingActivity extends AppCompatActivity implements OnMapReadyCallback
{

    private GoogleMap mMap;
    float x1, x2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing_page);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    public boolean onTouchEvent (MotionEvent touchEvent)
    {
        switch(touchEvent.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                x1 = touchEvent.getX();
                break;
            case MotionEvent.ACTION_UP:
                x2 = touchEvent.getX();
                if(x1 < x2)
                {
                    Intent i = new Intent(SwipingActivity.this, LeftActivity.class);
                    startActivity(i);
                    finish();
                }
                else if (x1 > x2)
                {
                    Intent i = new Intent(SwipingActivity.this, RightActivity.class);
                    startActivity(i);
                    finish();
                }
        }
        return false;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        LatLng location = new LatLng(-34, 151); // Example location
        MarkerOptions markerOptions = new MarkerOptions()
                .position(location)
                .title("Marker Title") // Title of the marker
                .snippet("This is a custom description for the marker.") // Snippet text with the description
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.resized_restaurant_40x40)); // Custom icon
        mMap.addMarker(markerOptions);

        // Move the camera to the location with a zoom level
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10.0f));
        enableMyLocation();
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            // Show rationale and request permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);
        }
    }

    private static final int LOCATION_PERMISSION_REQUEST = 1;

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation();
            }
        }
    }

}