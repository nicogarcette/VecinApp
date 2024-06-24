package com.example.vecinapp.ui.home.eventos_publicos;

import android.content.ClipData;
import android.content.ClipDescription;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vecinapp.ModelData.Evento;
import com.example.vecinapp.R;
import com.example.vecinapp.databinding.FragmentEventListBinding;
import com.example.vecinapp.databinding.EventListContentBinding;

import com.example.vecinapp.ui.home.HomeViewModel;

import java.util.ArrayList;
import java.util.List;

public class eventListFragment extends Fragment {

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

    private HomeViewModel viewModel;
    private FragmentEventListBinding binding;
    private SimpleItemRecyclerViewAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentEventListBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

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
        RecyclerView recyclerView = binding.eventList;

        View itemDetailFragmentContainer = view.findViewById(R.id.event_detail_nav_container);

        setupRecyclerView(recyclerView, itemDetailFragmentContainer);
    }

    private void setupRecyclerView(RecyclerView recyclerView, View itemDetailFragmentContainer) {

        adapter = new SimpleItemRecyclerViewAdapter(new ArrayList<>(), itemDetailFragmentContainer);
        recyclerView.setAdapter(adapter);
    }
    @Override
    public void onResume() {
        super.onResume();
        viewModel.loadEvents("ezpeleta");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public static class SimpleItemRecyclerViewAdapter extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private List<Evento> mValues;
        private final View mItemDetailFragmentContainer;

        SimpleItemRecyclerViewAdapter(List<Evento> items, View itemDetailFragmentContainer) {
            this.mValues = items != null ? items : new ArrayList<>();
            mItemDetailFragmentContainer = itemDetailFragmentContainer;
        }

        public void setEventos(List<Evento> eventos) {
            this.mValues = eventos != null ? eventos : new ArrayList<>();
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            EventListContentBinding binding =
                    EventListContentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new ViewHolder(binding);

        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mIdView.setText(mValues.get(position).nombreUser);
            holder.mContentView.setText(mValues.get(position).titulo);

            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(itemView -> {
                Evento item =
                        (Evento) itemView.getTag();
                Bundle arguments = new Bundle();
                arguments.putString(eventDetailFragment.ARG_ITEM_ID, item.id);
                if (mItemDetailFragmentContainer != null) {
                    Navigation.findNavController(mItemDetailFragmentContainer)
                            .navigate(R.id.fragment_event_detail, arguments);
                } else {
                    Navigation.findNavController(itemView).navigate(R.id.show_event_detail, arguments);
                }
            });
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                holder.itemView.setOnContextClickListener(v -> {
                    Evento item = (Evento) holder.itemView.getTag();
                    Toast.makeText(
                            holder.itemView.getContext(),
                            "Context click of item " + item.id,
                            Toast.LENGTH_LONG
                    ).show();
                    return true;
                });
            }
            holder.itemView.setOnLongClickListener(v -> {

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

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mIdView;
            final TextView mContentView;

            ViewHolder(EventListContentBinding binding) {
                super(binding.getRoot());
                mIdView = binding.userName;
                mContentView = binding.eventTitle;
            }

        }
    }
}