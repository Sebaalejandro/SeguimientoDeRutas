package com.example.seguimientoderutas;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class RouteData {
    private String routeId;
    private String routeName;
    private List<LatLng> routePoints;
    private double totalDistance; // Cambiado a double para mayor precisión
    private long startTime;
    private long endTime;

    // Constructor vacío requerido por Firebase
    public RouteData() {}

    // Constructor completo
    public RouteData(String routeId, String routeName, List<LatLng> routePoints, double totalDistance, long startTime, long endTime) {
        this.routeId = routeId;
        this.routeName = routeName;
        this.routePoints = routePoints;
        this.totalDistance = totalDistance;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getters y setters
    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public List<LatLng> getRoutePoints() {
        return routePoints;
    }

    public void setRoutePoints(List<LatLng> routePoints) {
        this.routePoints = routePoints;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
}
