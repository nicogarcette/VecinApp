package com.example.vecinapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.vecinapp.singleton.UserSingleton;
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
    private String comunidad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        nameText = findViewById(R.id.nombreRegister);
        lastNameText = findViewById(R.id.apellidoRegister);
        saveButton = findViewById(R.id.guardarButton);

        addComunidad();

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
            user.put("comunidad", comunidad);

            db.collection("usuario").document(userId).set(user)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            UserSingleton.getInstance().refresh();
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


    private void addComunidad() {

        Spinner spinner = findViewById(R.id.comunidad_spinner);;

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.comunidades_values,
                android.R.layout.simple_spinner_item
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                comunidad = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

}