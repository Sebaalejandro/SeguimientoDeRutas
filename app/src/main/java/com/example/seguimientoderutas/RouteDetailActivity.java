package com.example.seguimientoderutas;

import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class RouteDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap detailMap;
    private TextView tvRouteDetailName, tvRouteDetailDistance, tvRouteDetailDuration;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_detail);

        tvRouteDetailName = findViewById(R.id.tvRouteDetailName);
        tvRouteDetailDistance = findViewById(R.id.tvRouteDetailDistance);
        tvRouteDetailDuration = findViewById(R.id.tvRouteDetailDuration);

        db = FirebaseFirestore.getInstance();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.routeId);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        String routeId = getIntent().getStringExtra("routeId");
        if (routeId != null) {
            fetchRouteDetails(routeId);
        }
    }

    private void fetchRouteDetails(String routeId) {
        DocumentReference docRef = db.collection("routes").document(routeId);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                RouteData routeData = documentSnapshot.toObject(RouteData.class);
                if (routeData != null) {
                    displayRouteDetails(routeData);
                }
            }
        }).addOnFailureListener(e -> {
            // Handle errors, e.g., show a Toast
        });
    }

    private void displayRouteDetails(RouteData routeData) {
        tvRouteDetailName.setText("Nombre: " + routeData.getRouteName());
        tvRouteDetailDistance.setText("Distancia: " + routeData.getTotalDistance() + " m");
        long durationMillis = routeData.getEndTime() - routeData.getStartTime();
        tvRouteDetailDuration.setText("Duración: " + formatDuration(durationMillis));

        List<LatLng> routePoints = routeData.getRoutePoints();
        if (!routePoints.isEmpty()) {
            detailMap.addMarker(new MarkerOptions().position(routePoints.get(0)).title("Inicio"));
            detailMap.addMarker(new MarkerOptions().position(routePoints.get(routePoints.size() - 1)).title("Fin"));
            detailMap.addPolyline(new PolylineOptions().addAll(routePoints).color(0xFF1F8A65).width(8));
            detailMap.moveCamera(CameraUpdateFactory.newLatLngZoom(routePoints.get(0), 15));
        }
    }

    private String formatDuration(long durationMillis) {
        long seconds = durationMillis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        return String.format("%02d:%02d:%02d", hours, minutes % 60, seconds % 60);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        detailMap = googleMap;
    }
}
