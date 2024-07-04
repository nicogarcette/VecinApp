package com.example.vecinapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;

import com.example.vecinapp.singleton.UserSingleton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import android.content.Intent;

import com.example.vecinapp.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

import org.osmdroid.config.Configuration;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);

        boolean mantenerSesion = sharedPreferences.getBoolean("mantenerSesion", false);

        if (currentUser == null && !mantenerSesion) {

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        UserSingleton.getInstance();

        FirebaseMessaging.getInstance().subscribeToTopic("nuevo").addOnCompleteListener(
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "listo";
                        if (!task.isSuccessful()){
                            msg ="fallo";
                        }
                    }
                }
        );

        
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

    }


    public void loadLocale() {

       SharedPreferences sharedPreferences = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String language = sharedPreferences.getString("language", "");
        setLocale(language);
    }

    public void setLocale(String lang) {

        Locale locale = new Locale(getLanguageCode(lang));
        Locale.setDefault(locale);

        Resources resources = getResources();
        android.content.res.Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);

        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());
    }

    private String getLanguageCode(String language) {
        switch (language) {
            case "Español":
                return "es";
            case "English":
                return "en";
            default:
                return "es";
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
        if (!sharedPreferences.getBoolean("mantenerSesion", false)) {
            FirebaseAuth.getInstance().signOut();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
        if (!sharedPreferences.getBoolean("mantenerSesion", false)) {
            FirebaseAuth.getInstance().signOut();
        }
    }

}