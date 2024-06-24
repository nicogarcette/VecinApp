package com.example.vecinapp.ui.home.eventos_publicos;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.vecinapp.R;
import com.example.vecinapp.databinding.ActivityEventDetailBinding;

public class EventDetailHostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflar el layout usando el binding
        ActivityEventDetailBinding binding = ActivityEventDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Configurar el Toolbar como ActionBar
        setSupportActionBar(binding.toolbar);

        // Obtener el NavHostFragment y NavController
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_event_detail);
        NavController navController = navHostFragment.getNavController();

        // Configurar AppBarConfiguration con los destinos del grafo de navegación
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph())
                .build();

        // Configurar la ActionBar con NavController y AppBarConfiguration
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_event_detail);
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}

