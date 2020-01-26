package com.example.rentacar;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Looper;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener {

    private GoogleMap mMap;
    private String task;
    private Button select_location;
    private Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        initComponents();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            task = bundle.getString("task");
        }
    }

    private void initComponents() {
        select_location = findViewById(R.id.select_location_btn);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (task != null) {
            if (task.equalsIgnoreCase("register_select_location")) {
                final LocationRequest locationRequest = new LocationRequest();
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                locationRequest.setInterval(1000);

                LocationCallback locationCallback = new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        LatLng latLng;
                        if (locationResult != null) {
                            latLng = new LatLng(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude());
                        }else {
                            latLng = new LatLng(6.918420, 79.856891);
                        }
                        mMap.clear();
                        marker = mMap.addMarker(new MarkerOptions().position(latLng));
                        marker.showInfoWindow();
                        marker.setDraggable(true);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f));
                    }
                };

                if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mMap.setMyLocationEnabled(true);

                FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
                if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

                LatLng latLng = new LatLng(6.918420, 79.856891);

                marker = mMap.addMarker(new MarkerOptions().position(latLng));
                marker.showInfoWindow();
                marker.setDraggable(true);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f));

                mMap.setOnMarkerDragListener(this);

                select_location.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (marker != null) {
                            LatLng latLng = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
                            Gson gson = new Gson();

                            Intent intent = new Intent();
                            intent.putExtra("selected_location", gson.toJson(latLng));
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker m) {
        marker = m;
    }
}
