package com.example.vecinapp.ui.perfil.eventos_propios;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import com.example.vecinapp.R;
import com.example.vecinapp.databinding.FragmentEventoEditarBinding;
import com.example.vecinapp.ui.perfil.PerfilViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;

public class fragment_evento_editar extends Fragment {

    private static final String ARG_EVENTO_ID = "evento_id";

    private EditText eventTitulo;
    private EditText eventDescription;
    private PerfilViewModel viewModel;
    private String eventId;
    private  String eventCategory;
    private MapView mapView;
    private Marker marker;
    private com.google.firebase.firestore.GeoPoint eventGeopoint;

    private FragmentEventoEditarBinding binding;

    public static fragment_evento_editar newInstance(String eventoId) {
        fragment_evento_editar fragment = new fragment_evento_editar();
        Bundle args = new Bundle();
        args.putString(ARG_EVENTO_ID, eventoId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventId = getArguments().getString(ARG_EVENTO_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentEventoEditarBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        eventDescription = view.findViewById(R.id.event_description);
        eventTitulo = view.findViewById(R.id.event_title);

        viewModel = new ViewModelProvider(this).get(PerfilViewModel.class);

        addCategoria();
        configuracionMapa();

        Button saveEventButton = view.findViewById(R.id.btn_update_event);
        saveEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(requireContext())
                        .setTitle("Confirmacion")
                        .setMessage("Esta seguro de que desea actualizar el evento?")
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {

                            ActualizarEvento();

                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }
        });

    }

    private void ActualizarEvento() {

        String titulo = eventTitulo.getText().toString().trim();
        String descripcion = eventDescription.getText().toString().trim();
        String IdCategoria = eventCategory;
        com.google.firebase.firestore.GeoPoint direccion = eventGeopoint;

        viewModel.actualizarEvento(titulo,descripcion,IdCategoria,direccion,eventId)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(requireContext(), "Evento actualizado.", Toast.LENGTH_SHORT).show();
                        requireActivity().onBackPressed();//volvemo
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(requireContext(), "Error al actualizar evento", Toast.LENGTH_SHORT).show();
                    }
                });;
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
        Log.d("arraycaregoria", " configurado?");

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

}