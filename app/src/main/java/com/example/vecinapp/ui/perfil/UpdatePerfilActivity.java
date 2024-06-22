package com.example.vecinapp.ui.perfil;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import com.example.vecinapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import android.content.Intent;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.widget.EditText;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.HashMap;
import java.util.Map;




public class UpdatePerfilActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private String currentPhotoPath;
    private FirebaseStorage storage;
    private FirebaseUser actualtUser;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

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

        Button btnCapturarImagen = findViewById(R.id.btn_capturar_imagen);
        Button btnBuscarImagen = findViewById(R.id.btn_buscar_imagen);
        Button actualizarPerfil = findViewById(R.id.btn_actualizar_perfil);


        imageView = findViewById(R.id.imageView);

        btnCapturarImagen.setOnClickListener(v -> TomarImagen());
        btnBuscarImagen.setOnClickListener(v -> BuscarImagen());
        actualizarPerfil.setOnClickListener(v -> ActualizarPerfil());

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

                    db.collection("usuario").document(documentId)
                            .update(updates)
                            .addOnSuccessListener(aVoid -> {
                                Log.d("MainActivity", "Perfil actualizado exitosamente");
                                Toast.makeText(UpdatePerfilActivity.this, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(UpdatePerfilActivity.this, PerfilFragment.class);
                                startActivity(intent);
                                finish();
                            })
                            .addOnFailureListener(e -> Log.w("MainActivity", "Error al actualizar el perfil", e));
                }
            } else {
                Log.w("MainActivity", "Error obteniendo documentos: ", task.getException());
            }
        });
    }

    private void TomarImagen() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.vecinapp.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void BuscarImagen() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, REQUEST_IMAGE_PICK);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_IMAGE_PICK:
                    if (data != null && data.getData() != null) {
                        Uri selectedImage = data.getData();
                        imageView.setImageURI(selectedImage);
                        uploadImageToFirebase(selectedImage);
                    }
                    break;
                case REQUEST_IMAGE_CAPTURE:
                    File file = new File(currentPhotoPath);
                    Uri capturedImageUri = Uri.fromFile(file);
                    imageView.setImageURI(capturedImageUri);
                    uploadImageToFirebase(capturedImageUri);
                    break;
            }
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        StorageReference imageRef = storageRef.child("images/" + imageUri.getLastPathSegment());
        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    Toast.makeText(UpdatePerfilActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {

                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(UpdatePerfilActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show();
                });
    }
}