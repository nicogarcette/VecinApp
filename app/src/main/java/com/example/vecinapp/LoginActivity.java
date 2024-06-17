package com.example.vecinapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import android.content.Intent;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    private EditText emailText;
    private EditText passText;
    private Button loginButton ;
    private Button irARegistrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main);

        auth = FirebaseAuth.getInstance();

        emailText = findViewById(R.id.email);
        passText = findViewById(R.id.password);
        loginButton = findViewById(R.id.btnIngresar);
        irARegistrar = findViewById(R.id.irARegistrar);

        loginButton.setOnClickListener(v -> {
            String email = emailText.getText().toString();
            String password = passText.getText().toString();

            if (email.isEmpty() && password.isEmpty())
                Toast.makeText(LoginActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();

            loginUser(email, password);
        });


        irARegistrar.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                        // aca tenenemos que lanzar la aplicacion.
                    } else {
                        Toast.makeText(LoginActivity.this, "Algo salio mal...: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}