package com.example.vecinapp.ui.home.eventos_publicos;

import android.content.ClipData;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vecinapp.ModelData.Evento;
import com.example.vecinapp.R;
import com.example.vecinapp.ui.home.HomeViewModel;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.example.vecinapp.placeholder.PlaceholderContent;
import com.example.vecinapp.databinding.FragmentEventDetailBinding;
import com.google.firebase.Timestamp;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.text.SimpleDateFormat;
import java.util.Locale;


public class eventDetailFragment extends Fragment {

    public static final String ARG_ITEM_ID = "item_id";
    private Evento mItem;
    private CollapsingToolbarLayout mToolbarLayout;
    private TextView descriccionView;
    private TextView dateView;
    private TextView categoryView;
    private TextView titleView;

    private HomeViewModel viewModel;
    private MapView mapView;

    private final View.OnDragListener dragListener = (v, event) -> {
        if (event.getAction() == DragEvent.ACTION_DROP) {
            ClipData.Item clipDataItem = event.getClipData().getItemAt(0);
            mItem = PlaceholderContent.ITEM_MAP.get(clipDataItem.getText().toString());
            updateContent();
        }
        return true;
    };
    private FragmentEventDetailBinding binding;

    public eventDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentEventDetailBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        mToolbarLayout = rootView.findViewById(R.id.toolbar_layout);
        descriccionView = binding.eventDetail;
        dateView = binding.eventDate;
        categoryView = binding.eventCategoria;
        titleView = binding.eventTitle;

        rootView.setOnDragListener(dragListener);
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
        mapView.getController().setZoom(10.0);
        GeoPoint startPoint = new GeoPoint(mItem.direccion.getLatitude(),mItem.direccion.getLongitude());
        mapView.getController().setCenter(startPoint);

        Marker marker = new Marker(mapView);
        marker.setPosition(startPoint);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle("Direccion");
        mapView.getOverlays().add(marker);

    }
    private void updateContent() {

        Log.d("EVENTODETAIL", "BUSCANDO categoria  " + mItem.IdCategoria);
        if (mItem != null) {
            descriccionView.setText(mItem.descripcion);
            dateView.setText(formatoFecha(mItem.fecha));
            categoryView.setText(mItem.IdCategoria);
            titleView.setText(mItem.titulo);
            configuracionMapa();

//            if (mToolbarLayout != null) {
//                mToolbarLayout.setTitle(mItem.titulo);
//            }
        }
    }
}