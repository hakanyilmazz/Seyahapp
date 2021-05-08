package com.hakanyilmazz.seyahapp.view;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hakanyilmazz.seyahapp.R;
import com.hakanyilmazz.seyahapp.model.Place;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;

    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);

        Intent intent = getIntent();
        String info = intent.getStringExtra("info");

        if (info.equals("new")) {
            locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
            locationListener = location -> {
                SharedPreferences sharedPreferences = MapsActivity.this.getSharedPreferences(getPackageName(), MODE_PRIVATE);
                boolean trackBoolean = sharedPreferences.getBoolean("trackBoolean", false);

                if (!trackBoolean) {
                    LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));

                    sharedPreferences.edit().putBoolean("trackBoolean", true).apply();
                }
            };

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (lastLocation != null) {
                    LatLng lastUserLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation, 15));
                }
            }
        } else {
            mMap.clear();

            Place place = (Place) intent.getSerializableExtra("place");
            LatLng latLng = new LatLng(place.getLatitude(), place.getLongitude());

            String placeName = place.getName();

            mMap.addMarker(new MarkerOptions().position(latLng).title(placeName));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0) {
            if (requestCode == 1) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                    Intent intent = getIntent();
                    String info = intent.getStringExtra("info");

                    if (info.equals("new")) {
                        Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                        if (lastLocation != null) {
                            LatLng lastUserLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation, 15));
                        }
                    } else {
                        mMap.clear();

                        Place place = (Place) intent.getSerializableExtra("place");

                        LatLng latLng = new LatLng(place.getLatitude(), place.getLongitude());
                        String placeName = place.getName();

                        mMap.addMarker(new MarkerOptions().position(latLng).title(placeName));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    }
                }
            }
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        String address = "";

        try {
            List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

            if (addressList != null && addressList.size() > 0) {
                if (addressList.get(0).getThoroughfare() != null) {
                    address += addressList.get(0).getThoroughfare();

                    if (addressList.get(0).getSubThoroughfare() != null) {
                        address += " " + addressList.get(0).getSubThoroughfare();
                    }
                }
            } else {
                address = "New Place";
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng).title(address));

        double latitude = latLng.latitude;
        double longitude = latLng.longitude;

        final Place place = new Place(address, latitude, longitude);

        AlertDialog.Builder alert = new AlertDialog.Builder(MapsActivity.this);

        alert.setCancelable(false);

        alert.setTitle("Save?");
        alert.setMessage(place.getName());

        alert.setPositiveButton("Yes", (dialog, which) -> {
            try {
                database = MapsActivity.this.openOrCreateDatabase("Places", MODE_PRIVATE, null);
                database.execSQL("CREATE TABLE IF NOT EXISTS places (id INTEGER PRIMARY KEY, name VARCHAR, latitude VARCHAR, longitude VARCHAR)");

                String toCompile = "INSERT INTO places (name, latitude, longitude) VALUES (?, ?, ?)";
                SQLiteStatement sqLiteStatement = database.compileStatement(toCompile);

                sqLiteStatement.bindString(1, place.getName());
                sqLiteStatement.bindString(2, String.valueOf(place.getLatitude()));
                sqLiteStatement.bindString(3, String.valueOf(place.getLongitude()));

                sqLiteStatement.execute();

                Toast.makeText(getApplicationContext(), "Saved!", Toast.LENGTH_LONG).show();

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        alert.setNegativeButton("No", (dialog, which) -> Toast.makeText(getApplicationContext(), "Canceled!", Toast.LENGTH_LONG).show());

        alert.show();
    }
}