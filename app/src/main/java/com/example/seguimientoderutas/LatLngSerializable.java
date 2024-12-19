package com.example.seguimientoderutas;

public class LatLngSerializable {
    private double latitude;
    private double longitude;

    // Constructor vacío necesario para Firebase
    public LatLngSerializable() {}

    // Constructor con parámetros
    public LatLngSerializable(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters y Setters
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
