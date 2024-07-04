package com.example.vecinapp.ui.perfil;


import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vecinapp.R;
import com.example.vecinapp.singleton.UserSingleton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import android.content.Intent;
import android.widget.Spinner;
import android.widget.Toast;

import android.widget.EditText;

import android.util.Log;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.HashMap;
import java.util.Map;




public class UpdatePerfilActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_UPDATE_PERFIL = 1;

    private FirebaseStorage storage;
    private FirebaseUser actualtUser;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private  String comunidad;

    private StorageReference storageRef;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_perfil);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        actualtUser = mAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        Button actualizarPerfil = findViewById(R.id.btn_actualizar_perfil);
        addComunidad();


        imageView = findViewById(R.id.imageView);


        actualizarPerfil.setOnClickListener(v -> ActualizarPerfil());

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_UPDATE_PERFIL && resultCode == RESULT_OK) {

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment_activity_main, new PerfilFragment())
                    .commit();
        }
    }

    private void ActualizarPerfil(){

        EditText nombreEditText = findViewById(R.id.nombre_updated);
        EditText apellidoEditText = findViewById(R.id.apellido_updated);

        String nombre = nombreEditText.getText().toString();
        String apellido = apellidoEditText.getText().toString();

        Query query = db.collection("usuario").whereEqualTo("mail", actualtUser.getEmail()).limit(1);;

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {

                    String documentId = document.getId();

                    Map<String, Object> updates = new HashMap<>();
                    updates.put("nombre", nombre);
                    updates.put("apellido", apellido);
                    updates.put("comunidad", comunidad);

                    db.collection("usuario").document(documentId)
                            .update(updates)
                            .addOnSuccessListener(aVoid -> {
                                UserSingleton.getInstance().refresh();
                                Log.d("MainActivity", "Perfil actualizado exitosamente");
                                Toast.makeText(UpdatePerfilActivity.this, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show();

                                Intent resultIntent = new Intent();
                                setResult(RESULT_OK, resultIntent);
                                finish();
                            })
                            .addOnFailureListener(e -> Log.w("MainActivity", "Error al actualizar el perfil", e));
                }
            } else {
                Log.w("MainActivity", "Error obteniendo documentos: ", task.getException());
            }
        });
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