package com.example.winningrecipe;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

//import com.example.winningrecipe.databinding.FragmentHomeBinding;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.winningrecipe.Adapters.MyAdapter;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Utils.FirebaseHandler;
import Utils.Recipe;

public class Home extends Fragment {

    FloatingActionButton addRecipeBtn;
    TextView category_1, category_2, category_3, category_4;
    RecyclerView recyclerView_1, recyclerView_2, recyclerView_3, recyclerView_4;
    List<Recipe> dataList_1, dataList_2, dataList_3, dataList_4;
    DatabaseReference databaseReference;
    SearchView searchView;
    MyAdapter adapter_1, adapter_2, adapter_3, adapter_4;
    Recipe EmptyRecipe;
    String user;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View viewF = inflater.inflate(R.layout.fragment_home, container, false);

        addRecipeBtn = viewF.findViewById(R.id.addFirebaseMoviesBtn);

        category_1 = viewF.findViewById(R.id.category_1);
        category_2 = viewF.findViewById(R.id.category_2);
        category_3 = viewF.findViewById(R.id.category_3);
        category_4 = viewF.findViewById(R.id.category_4);

        recyclerView_1 = viewF.findViewById(R.id.category_1_recyclerView);
        recyclerView_2 = viewF.findViewById(R.id.category_2_recyclerView);
        recyclerView_3 = viewF.findViewById(R.id.category_3_recyclerView);
        recyclerView_4 = viewF.findViewById(R.id.category_4_recyclerView);

        GridLayoutManager layoutManager1 = new GridLayoutManager(getContext(), 1, GridLayoutManager.HORIZONTAL, false);
        GridLayoutManager layoutManager2 = new GridLayoutManager(getContext(), 1, GridLayoutManager.HORIZONTAL, false);
        GridLayoutManager layoutManager3 = new GridLayoutManager(getContext(), 1, GridLayoutManager.HORIZONTAL, false);
        GridLayoutManager layoutManager4 = new GridLayoutManager(getContext(), 1, GridLayoutManager.HORIZONTAL, false);

        recyclerView_1.setLayoutManager(layoutManager1);
        recyclerView_2.setLayoutManager(layoutManager2);
        recyclerView_3.setLayoutManager(layoutManager3);
        recyclerView_4.setLayoutManager(layoutManager4);

        dataList_1 = new ArrayList<>();
        dataList_2 = new ArrayList<>();
        dataList_3 = new ArrayList<>();
        dataList_4 = new ArrayList<>();

        adapter_1 =new MyAdapter(getContext(),dataList_1, viewF, getParentFragmentManager());
        adapter_2 =new MyAdapter(getContext(),dataList_2, viewF, getParentFragmentManager());
        adapter_3 =new MyAdapter(getContext(),dataList_3, viewF, getParentFragmentManager());
        adapter_4 =new MyAdapter(getContext(),dataList_4, viewF, getParentFragmentManager());

        recyclerView_1.setAdapter(adapter_1);
        recyclerView_2.setAdapter(adapter_2);
        recyclerView_3.setAdapter(adapter_3);
        recyclerView_4.setAdapter(adapter_4);

        EmptyRecipe = new Recipe("add rcipe", Arrays.asList("Ingredient 1", "Ingredient 2", "Ingredient 3"), "d", 1, "g", "https://firebasestorage.googleapis.com/v0/b/winningrecipe-5f0f1.appspot.com/o/recipe_images%2F24eaea92-c9a2-492e-8f2e-d926c3a0958d.jpg?alt=media&token=0fd294b8-70ff-4401-8144-61db30693f03");
        user = "Yaellevi";

        FirebaseHandler firebaseHandler = new FirebaseHandler();
        databaseReference = firebaseHandler.getDatabaseReference().child("Users").child(user).child("Categories");

        databaseReference.child("Italian").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList_1.clear();
                for (DataSnapshot itemSnapshot: snapshot.getChildren()){
                    Log.d("movieInstance.value",itemSnapshot.toString());

                    Recipe dataClass = itemSnapshot.getValue(Recipe.class);
                    //dataClass.setKey(itemSnapshot.getKey());
                    dataList_1.add(dataClass);
                }
                adapter_1.notifyDataSetChanged();
                if (dataList_1.isEmpty()){
                    dataList_1.add(EmptyRecipe);
                }
                Log.d("data",dataList_1.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });

        databaseReference.child("Chicken").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList_2.clear();
                for (DataSnapshot itemSnapshot: snapshot.getChildren()){
                    Log.d("movieInstance.value",itemSnapshot.toString());

                    Recipe dataClass =itemSnapshot.getValue(Recipe.class);
                    //dataClass.setKey(itemSnapshot.getKey());
                    dataList_2.add(dataClass);
                }
                adapter_2.notifyDataSetChanged();
                if (dataList_2.isEmpty()){
                    dataList_2.add(EmptyRecipe);
                }
                Log.d("data",dataList_2.toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });

        databaseReference.child("Beef").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList_3.clear();
                for (DataSnapshot itemSnapshot: snapshot.getChildren()){
                    Log.d("movieInstance.value",itemSnapshot.toString());

                    Recipe dataClass =itemSnapshot.getValue(Recipe.class);
                    //dataClass.setKey(itemSnapshot.getKey());

                    dataList_3.add(dataClass);
                }
                adapter_3.notifyDataSetChanged();
                if (dataList_3.isEmpty()){
                    dataList_3.add(EmptyRecipe);
                }
                Log.d("data",dataList_3.toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });






        category_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = new Bundle();
                args.putString("category", category_1.getText().toString());

                Log.d("spt_cat",category_1.getText().toString());
                getParentFragmentManager().setFragmentResult("category", args);
                Navigation.findNavController(viewF).navigate(R.id.action_home_to_categoryRecipesFragment);

            }
        });

        addRecipeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(viewF).navigate(R.id.action_home4_to_addRecipe);

            }
        });

        return viewF;
    }
}