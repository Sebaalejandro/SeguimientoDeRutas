package com.example.seguimientoderutas;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Button startButton, stopButton;
    private ImageView btnMapType;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private DatabaseReference databaseReference;

    private List<LatLng> routePoints = new ArrayList<>();
    private boolean isRecording = false;
    private String routeId;
    private float totalDistance = 0; // Distancia total
    private long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeUI();
        initializeFirebase();
        initializeMap();
        initializeLocationServices();
    }

    private void initializeUI() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);
        btnMapType = findViewById(R.id.btnMapType);

        startButton.setOnClickListener(v -> startRecording());
        stopButton.setOnClickListener(v -> stopRecording());

        btnMapType.setOnClickListener(v -> {
            if (mMap != null) {
                int currentMapType = mMap.getMapType();
                if (currentMapType == GoogleMap.MAP_TYPE_NORMAL) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                } else {
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }
            }
        });

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_view_routes) {
                viewRoutes();
            } else if (id == R.id.nav_edit_profile) {
                editProfile();
            } else if (id == R.id.nav_logout) {
                logout();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void initializeFirebase() {
        databaseReference = FirebaseDatabase.getInstance().getReference("routes");
    }

    private void initializeMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void initializeLocationServices() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (isRecording) {
                    for (Location location : locationResult.getLocations()) {
                        LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
                        addRoutePoint(point);
                        savePointToFirebase(point);
                    }
                }
            }
        };
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
            }
        });
    }

    private void startRecording() {
        if (!isRecording) {
            isRecording = true;
            routePoints.clear();
            totalDistance = 0;
            routeId = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            startTime = System.currentTimeMillis();
            Toast.makeText(this, "Grabación iniciada", Toast.LENGTH_SHORT).show();

            LocationRequest locationRequest = LocationRequest.create()
                    .setInterval(3000)
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
            }
        }
    }

    private void stopRecording() {
        if (isRecording) {
            isRecording = false;
            fusedLocationClient.removeLocationUpdates(locationCallback);

            if (!routePoints.isEmpty()) {
                saveRouteDetailsToFirebase();
                showRouteSummary();
                Toast.makeText(this, "Ruta guardada correctamente", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No se registraron puntos en la ruta", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void savePointToFirebase(LatLng point) {
        databaseReference.child(routeId).child("points").push().setValue(point);
    }

    private void saveRouteDetailsToFirebase() {
        databaseReference.child(routeId).child("details").setValue(
                new RouteData(
                        routeId,
                        "Ruta grabada",
                        routePoints,
                        totalDistance,
                        startTime,
                        System.currentTimeMillis()
                )
        );
    }

    private void addRoutePoint(LatLng point) {
        routePoints.add(point);

        if (routePoints.size() > 1) {
            LatLng lastPoint = routePoints.get(routePoints.size() - 2);
            Location prevLocation = new Location("");
            prevLocation.setLatitude(lastPoint.latitude);
            prevLocation.setLongitude(lastPoint.longitude);
            Location currentLocation = new Location("");
            currentLocation.setLatitude(point.latitude);
            currentLocation.setLongitude(point.longitude);
            totalDistance += prevLocation.distanceTo(currentLocation);
        }

        mMap.addPolyline(new PolylineOptions()
                .addAll(routePoints)
                .color(0xFFFF0000)
                .width(25));

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(routePoints.get(routePoints.size() - 1), 16));
    }

    private void showRouteSummary() {
        long elapsedTime = System.currentTimeMillis() - startTime;
        String duration = String.format("%02d:%02d:%02d", elapsedTime / 3600000, (elapsedTime % 3600000) / 60000, (elapsedTime % 60000) / 1000);
        Toast.makeText(this, "Ruta guardada. Distancia: " + totalDistance + " m. Duración: " + duration, Toast.LENGTH_LONG).show();

        if (!routePoints.isEmpty()) {
            mMap.addMarker(new MarkerOptions().position(routePoints.get(0)).title("Inicio"));
            mMap.addMarker(new MarkerOptions().position(routePoints.get(routePoints.size() - 1)).title("Fin"));
        }
    }

    private void viewRoutes() {
        Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
        startActivity(intent);
    }

    private void editProfile() {
        Intent intent = new Intent(MainActivity.this, EditProfileActivity.class);
        startActivity(intent);
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            onMapReady(mMap);
        } else {
            Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
