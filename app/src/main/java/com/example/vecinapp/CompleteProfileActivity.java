package com.example.vecinapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;


public class CompleteProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText nameText, lastNameText;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        nameText = findViewById(R.id.nombreRegister);
        lastNameText = findViewById(R.id.apellidoRegister);
        saveButton = findViewById(R.id.guardarButton);

        saveButton.setOnClickListener(v -> saveUserProfile());
    }

    private void saveUserProfile() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            String nombre = nameText.getText().toString().trim();
            String apellido = lastNameText.getText().toString().trim();

            Map<String, Object> user = new HashMap<>();
            user.put("nombre", nombre);
            user.put("apellido", apellido);
            user.put("mail", currentUser.getEmail());

            db.collection("usuario").document(userId).set(user)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Toast.makeText(CompleteProfileActivity.this, "Perfil guardado.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(CompleteProfileActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(CompleteProfileActivity.this, "Error al guardar perfil.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

}