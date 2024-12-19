package com.example.seguimientoderutas;

import com.google.firebase.firestore.FirebaseFirestore;

public class FirestoreUtils {

    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void saveRoute(RouteData routeData) {
        db.collection("routes")
                .add(routeData)  // Añade el objeto RouteData a Firestore
                .addOnSuccessListener(documentReference -> {
                    // Maneja el éxito
                    System.out.println("Ruta guardada con ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    // Maneja el error
                    System.out.println("Error al guardar la ruta: " + e.getMessage());
                });
    }
}
