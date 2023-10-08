package com.example.winningrecipe;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.navigation.Navigation;
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
    String user;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Receive user's email from the Bundle
        String user = SingletonUser.getInstance().getUser().replace(".", ",");

        // Inflate the layout for this fragment
        View viewF = inflater.inflate(R.layout.fragment_category_recipes, container, false);

        category = viewF.findViewById(R.id.category);
        recyclerView = viewF.findViewById(R.id.category_recyclerView);

        dataList = new ArrayList<>();
        myAdapter = new MyAdapter(getContext(), dataList, viewF, getParentFragmentManager(), user);

        FirebaseHandler firebaseHandler = new FirebaseHandler();


        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        categoryString = sharedPref.getString("category", "default_value");

        category.setText(categoryString);

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

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                Log.d("result" , "yes");
                Navigation.findNavController(viewF).navigate(R.id.action_categoryRecipesFragment_to_home);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), callback);

        return viewF;
    }
}