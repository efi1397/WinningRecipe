package com.example.winningrecipe;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.example.winningrecipe.Adapters.MyAdapterCategory;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import java.text.MessageFormat;
import java.util.ArrayList;
import Utils.FirebaseHandler;
import Utils.Recipe;

public class CategoryRecipesFragment extends Fragment {

    RecyclerView recyclerView;
    SearchView searchView;
    MyAdapterCategory myAdapter;
    ArrayList<Recipe> dataList;
    ValueEventListener eventListener;
    DatabaseReference databaseReference;
    CircularProgressIndicator progressIndicator;
    String categoryString;
    String user;
    TextView userEmailTextView;
    TextView category;
    View viewF;
    String extractedUsername;
    SharedPreferences sharedPref;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewF = inflater.inflate(R.layout.fragment_category_recipes, container, false);

        progressIndicator = viewF.findViewById(R.id.loading_progress);
        progressIndicator.show();

        userEmailTextView = viewF.findViewById(R.id.userEmailTextView);
        recyclerView = viewF.findViewById(R.id.recyclerView);
        searchView = viewF.findViewById(R.id.search);
        searchView.clearFocus();
        category = viewF.findViewById(R.id.category);
        dataList = new ArrayList<>();
        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        categoryString = sharedPref.getString("category", "default_value");

        category.setText(categoryString);

        String user = SingletonUser.getInstance().getUser().replace(".", ",");
        Toast.makeText(getContext(), "Loading Data...", Toast.LENGTH_SHORT).show();
        String username = user.replace(",", ".");
        extractedUsername = username.split("@")[0];
        userEmailTextView.setText(MessageFormat.format("Hey {0}, what you'd like to make today?", extractedUsername));

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Navigation.findNavController(viewF).navigate(R.id.action_categoryRecipesFragment_to_home);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), callback);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchList(newText);
                return true;
            }
        });

        FirebaseHandler firebaseHandler = new FirebaseHandler();

        DatabaseReference categoryReference = firebaseHandler.getCategoryReference(user, categoryString);
        categoryReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    Recipe dataClass = itemSnapshot.getValue(Recipe.class);
                    dataList.add(dataClass);
                }
                updateRecyclerView(dataList);
                progressIndicator.hide();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
        return viewF;
    }

    private void updateRecyclerView(ArrayList<Recipe> dataList) {
        myAdapter = new MyAdapterCategory(getContext(), dataList, viewF, getParentFragmentManager());
        recyclerView.setAdapter(myAdapter);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(gridLayoutManager);
    }

    private void searchList(String text) {
        ArrayList<Recipe> searchList = new ArrayList<>();
        for (Recipe dataClass : dataList) {
            if (dataClass.getName().toLowerCase().contains(text.toLowerCase())) {
                searchList.add(dataClass);
            }
        }
        myAdapter.searchDataList(searchList);
    }
}
