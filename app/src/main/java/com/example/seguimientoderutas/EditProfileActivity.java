package com.example.seguimientoderutas;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditProfileActivity extends AppCompatActivity {

    private EditText userNameEditText, userEmailEditText;
    private Button confirmChangesButton;
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Inicializar Firebase
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Referencias a la UI
        userNameEditText = findViewById(R.id.userNameEditText);
        userEmailEditText = findViewById(R.id.userEmailEditText);
        confirmChangesButton = findViewById(R.id.confirmChangesButton);

        // Cargar datos actuales
        loadUserData();

        // Manejar confirmación de cambios
        confirmChangesButton.setOnClickListener(v -> saveChanges());
    }

    private void loadUserData() {
        if (currentUser != null) {
            // Mostrar el correo electrónico directamente
            userEmailEditText.setText(currentUser.getEmail());

            // Cargar el nombre de usuario desde la base de datos
            databaseReference.child(currentUser.getUid()).child("name")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String userName = snapshot.getValue(String.class);
                            if (userName != null) {
                                userNameEditText.setText(userName);
                            } else {
                                userNameEditText.setHint("Nombre no disponible");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(EditProfileActivity.this, "Error al cargar datos", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void saveChanges() {
        String updatedName = userNameEditText.getText().toString().trim();
        String updatedEmail = userEmailEditText.getText().toString().trim();

        if (updatedName.isEmpty() || updatedEmail.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentUser != null) {
            // Actualizar el nombre en la base de datos
            databaseReference.child(currentUser.getUid()).child("name").setValue(updatedName)
                    .addOnSuccessListener(aVoid -> Toast.makeText(EditProfileActivity.this, "Nombre actualizado correctamente", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(EditProfileActivity.this, "Error al actualizar nombre", Toast.LENGTH_SHORT).show());

            // Actualizar el correo electrónico en Firebase Authentication
            currentUser.updateEmail(updatedEmail)
                    .addOnSuccessListener(aVoid -> Toast.makeText(EditProfileActivity.this, "Correo actualizado correctamente", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(EditProfileActivity.this, "Error al actualizar correo", Toast.LENGTH_SHORT).show());
        }
    }
}
