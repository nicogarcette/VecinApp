package com.example.vecinapp.ui.perfil.eventos_propios;

import android.content.ClipData;
import android.os.Bundle;
import android.view.DragEvent;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vecinapp.R;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.example.vecinapp.placeholder.PlaceholderContent;
import com.example.vecinapp.databinding.FragmentEventopropioDetailBinding;

/**
 * A fragment representing a single eventoPropio detail screen.
 * This fragment is either contained in a {@link eventoPropioListFragment}
 * in two-pane mode (on larger screen devices) or self-contained
 * on handsets.
 */
public class eventoPropioDetailFragment extends Fragment {

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The placeholder content this fragment is presenting.
     */
    //private PlaceholderContent.PlaceholderItem mItem;
    private CollapsingToolbarLayout mToolbarLayout;
    private TextView mTextView;

//    private final View.OnDragListener dragListener = (v, event) -> {
//        if (event.getAction() == DragEvent.ACTION_DROP) {
//            ClipData.Item clipDataItem = event.getClipData().getItemAt(0);
//            mItem = PlaceholderContent.ITEM_MAP.get(clipDataItem.getText().toString());
//            updateContent();
//        }
//        return true;
//    };
    private FragmentEventopropioDetailBinding binding;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public eventoPropioDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the placeholder content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
           //mItem = PlaceholderContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentEventopropioDetailBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        mToolbarLayout = rootView.findViewById(R.id.toolbar_layout);
        mTextView = binding.eventopropioDetail;

        // Show the placeholder content as text in a TextView & in the toolbar if available.
        //updateContent();
       // rootView.setOnDragListener(dragListener);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

//    private void updateContent() {
//        if (mItem != null) {
//            mTextView.setText(mItem.details);
//            if (mToolbarLayout != null) {
//                mToolbarLayout.setTitle(mItem.content);
//            }
//        }
//    }
}