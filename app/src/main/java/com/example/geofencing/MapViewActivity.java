package com.example.geofencing;

import android.location.Location;
import android.os.Bundle;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.location.LocationCallback;;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class MapViewActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static final String INTENT_LOCATION_KEY = "INTENT_LOCATION_KEY";
    private ArrayList<Place> places;
    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private LocationRequest mLocationRequest;
    Location mCurrentLocation;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);
        places = (ArrayList<Place>)getIntent().getExtras().getSerializable(INTENT_LOCATION_KEY);
        mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        mapFragment.getMapAsync(this);
        startLocationUpdates();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        for (Place p : places) {
            googleMap.addMarker(new MarkerOptions().position(p.getLatLng()).title(p.getName().toString()));
        }
    }

    void startLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setInterval(60000);
        mLocationRequest.setFastestInterval(1000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);
        //noinspection MissingPermission
        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        locationResult.getLastLocation();
                        onLocationChanged(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper());
    }

    private void onLocationChanged(android.location.Location location) {
        mCurrentLocation = location;
        mapFragment.getMapAsync(this);
        if (mCurrentLocation != null && map != null) {
            LatLng mLocLat = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            map.addMarker(new MarkerOptions().position(mLocLat).title("You are here"));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(mLocLat, 17));
        }
    }
}
