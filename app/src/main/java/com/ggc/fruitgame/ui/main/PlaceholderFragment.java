package com.ggc.fruitgame.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.ggc.fruitgame.databinding.FragmentInstruccionesBinding;
import com.ggc.fruitgame.fragmentos.AmarilloFragment;
import com.ggc.fruitgame.fragmentos.AzulFragment;
import com.ggc.fruitgame.fragmentos.RojoFragment;
import com.ggc.fruitgame.fragmentos.VerdeFragment;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;
    private FragmentInstruccionesBinding binding;

    public static Fragment newInstance(int index) {

        Fragment fragment = null;

        switch (index){
            case 1:fragment=new RojoFragment();break;
            case 2:fragment=new AmarilloFragment();break;
            case 3:fragment=new AzulFragment();break;
            case 4:fragment=new VerdeFragment();break;

        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = new ViewModelProvider(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentInstruccionesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.sectionLabel;
        pageViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}