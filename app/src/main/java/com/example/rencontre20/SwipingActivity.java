package com.example.rencontre20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class SwipingActivity extends AppCompatActivity implements OnMapReadyCallback
{
    private GoogleMap mMap;
    Button left_arrow, right_arrow;
    private static final int LOCATION_PERMISSION_REQUEST = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing_page);

        left_arrow = findViewById(R.id.left_arrow_main);
        right_arrow = findViewById(R.id.right_arrow_main);

        left_arrow.setOnClickListener(view -> {
            Intent i = new Intent(SwipingActivity.this, LeftActivity.class);
            startActivity(i);
            finish();
        });
        right_arrow.setOnClickListener(view -> {
            Intent i = new Intent(SwipingActivity.this, RightActivity.class);
            startActivity(i);
            finish();
        });

        // Obtain the Map and get notified when it is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null)
        {
            mapFragment.getMapAsync(this);
        }


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        try {
            // Load and apply the style from the JSON file (It's currently erasing logos other than mine
            boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));
        }
        catch (Resources.NotFoundException e)
        {
        }
        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter()
        {
            @Override
            public View getInfoWindow(Marker marker)
            {
                View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_window, null);
                TextView title = infoWindow.findViewById(R.id.title);
                TextView snippet = infoWindow.findViewById(R.id.snippet);

                title.setText(marker.getTitle());
                snippet.setText(marker.getSnippet());
                return infoWindow;
            }

            @Override
            public View getInfoContents(Marker marker)
            {
                return null; // Use default background
            }
        });

        googleMap.setOnInfoWindowClickListener(marker -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(SwipingActivity.this);
            builder.setMessage("Voulez vous choisir " + marker.getTitle() + "?");
            builder.setPositiveButton("Yes", (dialog, which) -> {
                // Show the bottom sheet login dialog fragment
                MatchBottomSheetDialogFragment fragment = new MatchBottomSheetDialogFragment();
                fragment.show(getSupportFragmentManager(), fragment.getTag());
            });
            builder.setNegativeButton("No", null);
            builder.show();
        });


        fetchRestaurantsAndAddMarkers();


        // Move the camera to the location with a zoom level
        enableMyLocation();

    }

    private void fetchRestaurantsAndAddMarkers() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("restaurants").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult() != null && !task.getResult().isEmpty())
                {
                    for (QueryDocumentSnapshot document : task.getResult())
                    {
                        double lat = document.getDouble("latitude");
                        double lng = document.getDouble("longitude");
                        LatLng location = new LatLng(lat, lng);
                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(location)
                                .title(document.getString("name"))
                                .snippet(document.getString("description"))
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.resized_restaurant_55x55));
                        mMap.addMarker(markerOptions);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 13.0f));

                    }
                }
            }
        });
    }

    private void enableMyLocation()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
        {
            mMap.setMyLocationEnabled(true);
        }
        else
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);
        }
    }


    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if (requestCode == LOCATION_PERMISSION_REQUEST)
        {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                enableMyLocation();
            }
        }
    }

}