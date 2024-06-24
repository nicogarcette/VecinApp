package com.example.vecinapp.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.vecinapp.R;
import com.example.vecinapp.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);

//        Button navigateButton = binding.navigateButton;
//        navigateButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(requireContext(), EventDetailHostActivity.class);
//                startActivity(intent);
//            }
//        });

        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Configurar el Toolbar como ActionBar
        ((AppCompatActivity) requireActivity()).setSupportActionBar(binding.toolbar);

        // Obtener el NavController
        NavHostFragment navHostFragment = (NavHostFragment) getChildFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_event_detail);
        NavController navController = navHostFragment.getNavController();

        // Configurar AppBarConfiguration con los destinos del grafo de navegación
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph())
                .build();

        // Configurar la ActionBar con NavController y AppBarConfiguration
        NavigationUI.setupWithNavController(binding.toolbar, navController, appBarConfiguration);

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (!navController.popBackStack()) {
                    requireActivity().finish();
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}