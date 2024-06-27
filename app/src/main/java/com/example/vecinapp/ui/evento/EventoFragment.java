package com.example.vecinapp.ui.evento;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.os.Environment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;


import com.example.vecinapp.ModelData.Evento;
import com.example.vecinapp.ModelData.User;
import com.example.vecinapp.singleton.UserSingleton;
import com.example.vecinapp.ui.dialog.DatePickerFragment;
import com.example.vecinapp.R;
import com.example.vecinapp.databinding.FragmentEventoBinding;
import com.example.vecinapp.ui.home.HomeFragment;
import com.example.vecinapp.ui.perfil.eventos_propios.fragment_evento_editar;


import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;

import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.MapView;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;

import org.osmdroid.util.GeoPoint;


public class EventoFragment extends Fragment {

    private FragmentEventoBinding binding;

    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_SELECT_PHOTO = 2;
    private String currentPhotoPath;
    private ImageView eventImageView;
    private EditText eventDescription;
    private EditText eventTitulo;
    private com.google.firebase.firestore.GeoPoint eventGeopoint;
    private  String eventCategory;

    private EditText eventDate;
    private EventoViewModel EventoViewModel;
    private MapView mapView;
    private Marker marker;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        Configuration.getInstance().setUserAgentValue(getContext().getPackageName());

        binding = FragmentEventoBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        eventImageView = view.findViewById(R.id.event_image);
        eventDescription = view.findViewById(R.id.event_description);
        eventTitulo = view.findViewById(R.id.event_title);

        addCategoria();
        configuracionMapa();

        Button selectPhotoButton = view.findViewById(R.id.btn_select_photo);
        selectPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchSelectPictureIntent();
            }
        });

        Button saveEventButton = view.findViewById(R.id.btn_save_event);
        saveEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crearEvento();
            }
        });

    }

    private void addCategoria() {

        Spinner spinner = binding.categoriaSpinner;

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireActivity(),
                R.array.categories_values,
                android.R.layout.simple_spinner_item
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                eventCategory = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



    }


    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventoViewModel = new ViewModelProvider(this).get(EventoViewModel.class);
        EventoViewModel.getEventoCreado().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean eventoCreado) {
                if (eventoCreado != null) {
                    if (eventoCreado) {
                        Toast.makeText(getActivity(), "Evento creado.", Toast.LENGTH_SHORT).show();

                        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
                        navController.navigate(R.id.navigation_home);

                    } else {
                        Toast.makeText(getActivity(), "Error al crear el evento.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void configuracionMapa() {

        mapView = binding.map;

        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(15.0);
        GeoPoint startPoint = new GeoPoint(-34.6037, -58.3816);
        mapView.getController().setCenter(startPoint);

        marker = new Marker(mapView);
        MapEventsReceiver mReceive = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                mapView.getOverlays().remove(marker);
                marker.setPosition(p);
                mapView.getOverlays().add(marker);

                String coordinates = "Lat: " + p.getLatitude() + ", Lng: " + p.getLongitude();
                Toast.makeText(getActivity(), coordinates, Toast.LENGTH_LONG).show();
                eventGeopoint = new com.google.firebase.firestore.GeoPoint(p.getLatitude(),p.getLongitude());

                return true;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                return false;
            }
        };

        MapEventsOverlay overlayEventos = new MapEventsOverlay(getContext(), mReceive);
        mapView.getOverlays().add(overlayEventos);

    }


    private void showDatePickerDialog() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                final String selectedDate = day + " / " + (month+1) + " / " + year;
                eventDate.setText(selectedDate);
            }
        });

        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }

    private void crearEvento() {

        String titulo = eventTitulo.getText().toString().trim();
        String descripcion = eventDescription.getText().toString().trim();

        User user = UserSingleton.getInstance().getUser();

        Evento nuevoEvento = new Evento();
        nuevoEvento.direccion = eventGeopoint;
        nuevoEvento.IdCategoria = eventCategory;
        nuevoEvento.descripcion = descripcion;
        nuevoEvento.titulo = titulo;
        nuevoEvento.apellidoUser = user.apellido;
        nuevoEvento.nombreUser = user.nombre;
        nuevoEvento.comunidad = user.comunidad;

        EventoViewModel.agregarEvento(nuevoEvento);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT_PHOTO && resultCode == getActivity().RESULT_OK) {

            Uri selectedImageUri = data.getData();
            eventImageView.setImageURI(selectedImageUri);

            EventoViewModel.setCurrentPhoto(selectedImageUri);

        } else {
            Toast.makeText(getActivity(), "Photo not taken", Toast.LENGTH_SHORT).show();
        }

    }

    private void dispatchSelectPictureIntent() {
        Intent selectPictureIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (selectPictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(selectPictureIntent, REQUEST_SELECT_PHOTO);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}























