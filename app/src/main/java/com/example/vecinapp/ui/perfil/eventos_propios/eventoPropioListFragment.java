package com.example.vecinapp.ui.perfil.eventos_propios;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;


import android.view.KeyEvent;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import android.widget.TextView;
import android.widget.Toast;

import com.example.vecinapp.ModelData.Evento;
import com.example.vecinapp.R;
import com.example.vecinapp.databinding.FragmentEventopropioListBinding;
import com.example.vecinapp.databinding.EventopropioListContentBinding;

import com.example.vecinapp.ui.perfil.PerfilViewModel;


import java.util.ArrayList;
import java.util.List;

public class eventoPropioListFragment extends Fragment {

    ViewCompat.OnUnhandledKeyEventListenerCompat unhandledKeyEventListenerCompat = (v, event) -> {
        if (event.getKeyCode() == KeyEvent.KEYCODE_Z && event.isCtrlPressed()) {
            Toast.makeText(
                    v.getContext(),
                    "Undo (Ctrl + Z) shortcut triggered",
                    Toast.LENGTH_LONG
            ).show();
            return true;
        } else if (event.getKeyCode() == KeyEvent.KEYCODE_F && event.isCtrlPressed()) {
            Toast.makeText(
                    v.getContext(),
                    "Find (Ctrl + F) shortcut triggered",
                    Toast.LENGTH_LONG
            ).show();
            return true;
        }
        return false;
    };

    private PerfilViewModel viewModel;
    private FragmentEventopropioListBinding binding;
    private SimpleItemRecyclerViewAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentEventopropioListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(PerfilViewModel.class);

        viewModel.loadEvents();

        viewModel.getEvents().observe(this, eventos -> {
            if (eventos != null) {
                adapter.setEventos(eventos);
            } else {

                adapter.setEventos(new ArrayList<>());
            }
        });

        viewModel.getEvents();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewCompat.addOnUnhandledKeyEventListener(view, unhandledKeyEventListenerCompat);

        RecyclerView recyclerView = binding.eventopropioList;

        View itemDetailFragmentContainer = view.findViewById(R.id.eventopropio_detail_nav_container);

        setupRecyclerView(recyclerView, itemDetailFragmentContainer);
    }

    private void setupRecyclerView(RecyclerView recyclerView, View itemDetailFragmentContainer) {

        adapter = new SimpleItemRecyclerViewAdapter(new ArrayList<>(), itemDetailFragmentContainer, viewModel);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.loadEvents();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public static class SimpleItemRecyclerViewAdapter extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private List<Evento> mValues;
        private final View mItemDetailFragmentContainer;
        private final PerfilViewModel viewModel;

        SimpleItemRecyclerViewAdapter(List<Evento> items, View itemDetailFragmentContainer,PerfilViewModel viewModel ) {
            mValues = items;
            mItemDetailFragmentContainer = itemDetailFragmentContainer;
            this.viewModel = viewModel;
        }

        public void setEventos(List<Evento> eventos) {
            this.mValues = eventos != null ? eventos : new ArrayList<>();
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            EventopropioListContentBinding binding =
                    EventopropioListContentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new ViewHolder(binding);

        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {

            holder.mIdView.setText(mValues.get(position).nombreUser);
            holder.mContentView.setText(mValues.get(position).titulo);

            holder.itemView.setTag(mValues.get(position));

            holder.itemView.setOnClickListener(itemView -> {
                Evento item = (Evento) itemView.getTag();
                Bundle arguments = new Bundle();

                arguments.putString(eventoPropioDetailFragment.ARG_ITEM_ID, item.id);//le pasa como argumento el id
                if (mItemDetailFragmentContainer != null) {
                    Navigation.findNavController(mItemDetailFragmentContainer)
                            .navigate(R.id.fragment_eventopropio_detail, arguments);
                } else {
                    Navigation.findNavController(itemView).navigate(R.id.show_eventopropio_detail, arguments);
                }
            });

            // botones
            holder.editButton.setOnClickListener(v -> {
                int itemPosition = holder.getAdapterPosition();
                if (itemPosition != RecyclerView.NO_POSITION) {
                    Evento clickedEvento = mValues.get(itemPosition);

                    Fragment editarEventoFragment = fragment_evento_editar.newInstance(mValues.get(position).id);

                    FragmentManager fragmentManager = ((AppCompatActivity) v.getContext()).getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.nav_host_fragment_eventopropio_detail, editarEventoFragment)
                            .addToBackStack(null)
                            .commit();

                }
            });

            eliminarEvento(holder);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                holder.itemView.setOnContextClickListener(v -> {
                    Evento item =
                            (Evento) holder.itemView.getTag();
                    Toast.makeText(
                            holder.itemView.getContext(),
                            "Context click of item " + item.id,
                            Toast.LENGTH_LONG
                    ).show();
                    return true;
                });
            }
            holder.itemView.setOnLongClickListener(v -> {
                // Setting the item id as the clip data so that the drop target is able to
                // identify the id of the content
                ClipData.Item clipItem = new ClipData.Item(mValues.get(position).id);
                ClipData dragData = new ClipData(
                        ((Evento) v.getTag()).descripcion,
                        new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN},
                        clipItem
                );

                if (Build.VERSION.SDK_INT >= 24) {
                    v.startDragAndDrop(
                            dragData,
                            new View.DragShadowBuilder(v),
                            null,
                            0
                    );
                } else {
                    v.startDrag(
                            dragData,
                            new View.DragShadowBuilder(v),
                            null,
                            0
                    );
                }
                return true;
            });
        }

        private void eliminarEvento(ViewHolder holder){

            holder.deleteButton.setOnClickListener(v -> {

                int itemPosition = holder.getAdapterPosition();

                if (itemPosition != RecyclerView.NO_POSITION) {
                    Evento clickedEvento = mValues.get(itemPosition);

                    new AlertDialog.Builder(v.getContext())
                            .setTitle("Confirmacion")
                            .setMessage("Esta seguro de que desea eliminar el evento?")
                            .setPositiveButton(android.R.string.yes, (dialog, which) -> {

                                viewModel.eliminarEvento(clickedEvento.id)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(v.getContext(), "Evento eliminado correctamente", Toast.LENGTH_SHORT).show();
                                            viewModel.loadEvents();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(v.getContext(), "Error al eliminar el evento", Toast.LENGTH_SHORT).show();
                                        });
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            });

        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mIdView;
            final TextView mContentView;
            public ImageButton editButton;
            public ImageButton deleteButton;

            ViewHolder(EventopropioListContentBinding binding) {
                super(binding.getRoot());
                mIdView = binding.userName;
                mContentView = binding.eventTitle;
                editButton = itemView.findViewById(R.id.edit_button);
                deleteButton = itemView.findViewById(R.id.delete_button);

            }

        }
    }
}