package com.example.vecinapp.ui.perfil.eventos_propios;


import android.os.Bundle;
import android.util.Log;


import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.vecinapp.ModelData.Evento;
import com.example.vecinapp.R;
import com.example.vecinapp.ui.perfil.PerfilViewModel;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import com.example.vecinapp.databinding.FragmentEventopropioDetailBinding;

import com.google.firebase.Timestamp;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.text.SimpleDateFormat;
import java.util.Locale;


public class eventoPropioDetailFragment extends Fragment {

    public static final String ARG_ITEM_ID = "item_id";

    private Evento mItem;
    private CollapsingToolbarLayout mToolbarLayout;
    private TextView mTextView;

    private TextView descriccionView;
    private TextView dateView;
    private TextView categoryView;
    private TextView titleView;

    private PerfilViewModel viewModel;
    private MapView mapView;
    private ImageView imageView;

    private FragmentEventopropioDetailBinding binding;

    public eventoPropioDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(PerfilViewModel.class);

        String ID = getArguments().getString(ARG_ITEM_ID);
        viewModel.getEventoById(ID);

        viewModel.getEvent().observe(this, new Observer<Evento>() {
            @Override
            public void onChanged(Evento evento) {
                if (evento != null) {
                    mItem =evento;
                    updateContent();
                    Log.d("EVENTODETAIL", "BUSCANDO  " + mItem.descripcion);
                }
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentEventopropioDetailBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        mToolbarLayout = rootView.findViewById(R.id.toolbar_layout);
        mTextView = binding.eventopropioDetail;

        descriccionView = binding.eventDetail;
        dateView = binding.eventDate;
        categoryView = binding.eventCategoria;
        titleView = binding.eventTitle;
        imageView= binding.headerImage;

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private String formatoFecha(Timestamp timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(timestamp.toDate());
    }

    private void configuracionMapa() {

        mapView = binding.map;

        mItem.direccion.getLongitude();

        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(17.0);
        GeoPoint startPoint = new GeoPoint(mItem.direccion.getLatitude(),mItem.direccion.getLongitude());
        mapView.getController().setCenter(startPoint);

        Marker marker = new Marker(mapView);
        marker.setPosition(startPoint);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle("Direccion");
        mapView.getOverlays().add(marker);

    }
    private void configuracionImagen(String imageUrl){
        if (imageUrl != null && !imageUrl.isEmpty())
            Glide.with(this).load(imageUrl).into(imageView);
    }
    private void updateContent() {

        if (mItem != null) {
            descriccionView.setText(mItem.descripcion);
            dateView.setText(formatoFecha(mItem.fecha));
            categoryView.setText(mItem.IdCategoria);
            titleView.setText(mItem.titulo);
            configuracionMapa();
            configuracionImagen(mItem.ImageUrl);
        }
    }
}