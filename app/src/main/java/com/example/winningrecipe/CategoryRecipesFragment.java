package com.example.winningrecipe;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.winningrecipe.Adapters.MyAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Utils.FirebaseHandler;
import Utils.Recipe;

public class CategoryRecipesFragment extends Fragment {

    TextView category;
    RecyclerView recyclerView;
    MyAdapter myAdapter;
    List<Recipe> dataList;

    DatabaseReference databaseReference;
    String categoryString;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View viewF = inflater.inflate(R.layout.fragment_category_recipes, container, false);

        category = viewF.findViewById(R.id.category);
        recyclerView = viewF.findViewById(R.id.category_recyclerView);

        dataList = new ArrayList<>();
        myAdapter = new MyAdapter(getContext(), dataList, viewF, getParentFragmentManager());

        FirebaseHandler firebaseHandler = new FirebaseHandler();

        getParentFragmentManager().setFragmentResultListener("category", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                categoryString = result.getString("category");

                // Retrieve recipes for the specified category
                firebaseHandler.getRecipesByCategory(categoryString, new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        dataList.clear();
                        for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                            Recipe dataClass = itemSnapshot.getValue(Recipe.class);
                            dataList.add(dataClass);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), error.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        return viewF;
    }
}