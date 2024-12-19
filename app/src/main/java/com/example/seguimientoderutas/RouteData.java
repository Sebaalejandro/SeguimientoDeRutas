package com.example.seguimientoderutas;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class RouteData {
    private String routeId;
    private String routeName;
    private List<LatLng> routePoints;
    private float totalDistance;
    private long startTime;
    private long endTime;

    public RouteData(String routeId, String routeName, List<LatLng> routePoints, float totalDistance, long startTime, long endTime) {
        this.routeId = routeId;
        this.routeName = routeName;
        this.routePoints = routePoints;
        this.totalDistance = totalDistance;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getRouteId() {
        return routeId;
    }

    public String getRouteName() {
        return routeName;
    }

    public List<LatLng> getRoutePoints() {
        return routePoints;
    }

    public float getTotalDistance() {
        return totalDistance;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }
}
