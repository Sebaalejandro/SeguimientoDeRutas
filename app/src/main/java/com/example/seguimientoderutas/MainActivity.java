package com.example.seguimientoderutas;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Button startButton, stopButton;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private List<LatLng> routePoints = new ArrayList<>();
    private boolean isRecording = false;
    private float totalDistance = 0;
    private long startTime;
    private boolean isFirstPoint = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeUI();
        configureMap();
    }

    private void initializeUI() {
        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);

        startButton.setOnClickListener(v -> startRecording());
        stopButton.setOnClickListener(v -> stopRecording());
    }

    private void configureMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    private void startRecording() {
        if (!isRecording) {
            isRecording = true;
            routePoints.clear();
            totalDistance = 0;
            startTime = System.currentTimeMillis();
            isFirstPoint = true;
            Toast.makeText(this, "Grabación iniciada", Toast.LENGTH_SHORT).show();

            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    if (isRecording) {
                        addRoutePoint(new LatLng(location.getLatitude(), location.getLongitude()));
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                    // No se utiliza
                }

                @Override
                public void onProviderEnabled(@NonNull String provider) {
                    // No se utiliza
                }

                @Override
                public void onProviderDisabled(@NonNull String provider) {
                    // No se utiliza
                }
            };

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, locationListener);
            }
        }
    }

    private void addRoutePoint(LatLng point) {
        routePoints.add(point);

        // Verifica si hay más de un punto para calcular la distancia
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

        // Crear la polilínea con un color y grosor más visible
        mMap.addPolyline(new PolylineOptions()
                .addAll(routePoints)
                .color(0xFFFF0000)  // Rojo brillante
                .width(25));        // Grosor de 12px

        // Mover la cámara para enfocar la ruta con animación
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(routePoints.get(routePoints.size() - 1), 16));
    }

    private void stopRecording() {
        if (isRecording) {
            isRecording = false;
            locationManager.removeUpdates(locationListener);
            long endTime = System.currentTimeMillis();
            RouteData routeData = new RouteData("Ruta_" + endTime, "default", routePoints, totalDistance, startTime, endTime);
            saveRouteToFirestore(routeData);
            showRouteSummary();
        }
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

    private void saveRouteToFirestore(RouteData routeData) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("routes")
                .add(routeData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(MainActivity.this, "Ruta guardada exitosamente", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(MainActivity.this, "Error al guardar la ruta", Toast.LENGTH_SHORT).show();
                });
    }
}
