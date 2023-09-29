package com.example.winningrecipe;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

//import com.example.winningrecipe.databinding.FragmentHomeBinding;
import androidx.navigation.Navigation;


import com.github.clans.fab.FloatingActionButton;

public class Home extends Fragment {

    FloatingActionButton button;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View viewF = inflater.inflate(R.layout.fragment_home, container, false);

        button = viewF.findViewById(R.id.addFirebaseMoviesBtn);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(viewF).navigate(R.id.action_home4_to_addRecipe);

            }
        });

        return viewF;
    }
}