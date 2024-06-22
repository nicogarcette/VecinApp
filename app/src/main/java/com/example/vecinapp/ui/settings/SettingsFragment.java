package com.example.vecinapp.ui.settings;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import java.util.Locale;

import com.example.vecinapp.LoginActivity;
import com.example.vecinapp.R;

import com.example.vecinapp.databinding.FragmentSettingsBinding;
import com.google.firebase.auth.FirebaseAuth;



public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private SharedPreferences sharedPreferences;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        Button logoutButton = binding.logoutButton;
        logoutButton.setOnClickListener(v -> logoutUser());

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        mantenerSesion(view);

        seleccionIdioma();

    }

    private void seleccionIdioma(){

        Spinner spinner = binding.languageSpinner;

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireActivity(),
                R.array.language_entries,
                android.R.layout.simple_spinner_item
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Cargar y establecer el idioma seleccionado desde SharedPreferences
        String savedLanguage = sharedPreferences.getString("language", "");

        if (!savedLanguage.isEmpty()) {
            int position = adapter.getPosition(savedLanguage);
            spinner.setSelection(position);
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLanguage = parent.getItemAtPosition(position).toString();
                saveLanguagePreference(selectedLanguage);
                //setAppLocale(selectedLanguage);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Método requerido, pero no necesitamos hacer nada aquí.
            }
        });
    }

    public void setAppLocale(String languageCode) {
        // Guardar el idioma seleccionado en las preferencias
        saveLanguagePreference(languageCode);

        // Actualizar la configuración de idioma en la aplicación
        updateLocale(languageCode);

        // Reiniciar la vista actual para aplicar los cambios
        requireActivity().recreate();
    }

    private void updateLocale(String languageCode) {
        Locale locale = new Locale(getLanguageCode(languageCode));
        Locale.setDefault(locale);

        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);

        // Actualizar la configuración de recursos de la aplicación
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }


//    public void setAppLocale(String languageCode) {
//        Locale locale = new Locale(getLanguageCode(languageCode));
//        Locale.setDefault(locale);
//
//        Resources resources = getResources();
//        Configuration configuration = resources.getConfiguration();
//        configuration.setLocale(locale);
//
//        // Actualizar la configuración de recursos de la aplicación
//        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
//
//        // Reiniciar la actividad actual para aplicar los cambios de idioma
//        Intent refreshIntent = requireActivity().getIntent();
//        requireActivity().finish();
//        startActivity(refreshIntent);
//    }

    private void saveLanguagePreference(String selectedLanguage) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("language", selectedLanguage);
        editor.apply();
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

    private void mantenerSesion(View view) {

        Switch switch_sesion = view.findViewById(R.id.switch_sesion);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Cargar el estado del Switch desde SharedPreferences
        boolean mantenerSesion = sharedPreferences.getBoolean("mantenerSesion", false);
        switch_sesion.setChecked(mantenerSesion);

        // Manejar cambios en el estado del Switch
        switch_sesion.setOnCheckedChangeListener((buttonView, isChecked) -> {
            editor.putBoolean("mantenerSesion", isChecked);
            editor.apply();
        });
    }

    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("mantenerSesion", false);
        editor.apply();

        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        startActivity(intent);
        requireActivity().finish(); // evitar q el usuario use el boton de retroceso
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}