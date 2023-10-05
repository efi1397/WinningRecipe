package com.example.winningrecipe;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.SearchView;

import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.winningrecipe.Adapters.MyAdapter;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import Utils.FirebaseHandler;
import Utils.Recipe;


public class Home extends Fragment {

    // Constants for categories
    private static final String[] CATEGORIES = {
            "Chicken", "Beef", "Vegetarian", "Dessert",
            "Kosher", "Soups", "Salad", "Italian", "Chinese", "Indian"
    };

    FloatingActionButton addRecipeBtn;
    FloatingActionButton favoriteBtn;
    FloatingActionButton logoutBtn;
    TextView[] categoryTextViews;
    RecyclerView[] recyclerViews;
    List<Recipe>[] dataLists;
    DatabaseReference databaseReference;
    MyAdapter[] adapters;
    Recipe emptyRecipe;
    SearchView searchView;
    CircularProgressIndicator progressIndicator;
    String user;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    Boolean flagFavoriteBtn = false;
    TextView userEmailTextView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewF = inflater.inflate(R.layout.fragment_home, container, false);

        progressIndicator = viewF.findViewById(R.id.loading_progress);
        searchView = viewF.findViewById(R.id.search);
        searchView.clearFocus();

        addRecipeBtn = viewF.findViewById(R.id.addFirebaseMoviesBtn);
        favoriteBtn = viewF.findViewById(R.id.favoriteBtn);
        logoutBtn = viewF.findViewById(R.id.logoutBtn);
        userEmailTextView = viewF.findViewById(R.id.userEmailTextView);

        // Initialize arrays for category TextViews, RecyclerViews, data lists, and adapters
        categoryTextViews = new TextView[CATEGORIES.length];
        recyclerViews = new RecyclerView[CATEGORIES.length];
        dataLists = new List[CATEGORIES.length];
        adapters = new MyAdapter[CATEGORIES.length];

        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        String user = SingletonUser.getInstance().getUser().replace(".", ",");
        Toast.makeText(getContext(), user.replace(",", "."), Toast.LENGTH_SHORT).show();

        // Show the username without the email in home page
        String username = user.replace(",", ".");
        String extractedUsername = username.split("@")[0];
        userEmailTextView.setText(MessageFormat.format("Hey {0}, what you''d like to make today?", extractedUsername));

        // Find and set up category TextViews and RecyclerViews
        for (int i = 0; i < CATEGORIES.length; i++) {
            categoryTextViews[i] = viewF.findViewById(getResources().getIdentifier("category_" + (i + 1), "id", requireContext().getPackageName()));
            recyclerViews[i] = viewF.findViewById(getResources().getIdentifier("category_" + (i + 1) + "_recyclerView", "id", requireContext().getPackageName()));

            // Set category text from the constant array
            categoryTextViews[i].setText(CATEGORIES[i]);
            final String category = CATEGORIES[i];

            // Set click listener for each category TextView
            categoryTextViews[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editor.putString("category", category);
                    editor.commit();
                    Navigation.findNavController(viewF).navigate(R.id.action_home_to_categoryRecipesFragment);
                }
            });

            // Initialize data lists and adapters
            dataLists[i] = new ArrayList<>();
            adapters[i] = new MyAdapter(getContext(), dataLists[i], viewF, getParentFragmentManager(), user);
            recyclerViews[i].setLayoutManager(new GridLayoutManager(getContext(), 1, GridLayoutManager.HORIZONTAL, false));
            recyclerViews[i].setAdapter(adapters[i]);
        }

        emptyRecipe = new Recipe("add recipe", Arrays.asList("Ingredient 1", "Ingredient 2", "Ingredient 3"), "d", 1, "g", "https://firebasestorage.googleapis.com/v0/b/winningrecipe-5f0f1.appspot.com/o/recipe_images%2F24eaea92-c9a2-492e-8f2e-d926c3a0958d.jpg?alt=media&token=0fd294b8-70ff-4401-8144-61db30693f03");


        // Initialize FirebaseHandler instance
        FirebaseHandler firebaseHandler = new FirebaseHandler();

        // Get DatabaseReference for user's categories
        databaseReference = firebaseHandler.getUserCategoriesReference(user);

        // Initialize data lists and adapters for each category
        for (String category : CATEGORIES) {
            setupCategoryListener(firebaseHandler, user, category);
        }

        // Show the CircularProgressIndicator initially
        progressIndicator.show();

        // Hide the CircularProgressIndicator when all recipes are loaded
        hideProgressIndicatorWhenAllRecipesLoaded();

        addRecipeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(viewF).navigate(R.id.action_home4_to_addRecipe);
            }
        });
        favoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!flagFavoriteBtn) {
                    favoriteBtn.setImageResource(R.drawable.baseline_white_favorite_24);
                    showFavoriteItems();
                    flagFavoriteBtn = true;
                } else {
                    favoriteBtn.setImageResource(R.drawable.baseline_white_favorite_border_24);
                    showAllItems();
                    flagFavoriteBtn = false;
                }
            }
        });
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(viewF).navigate(R.id.action_home_to_login);
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (flagFavoriteBtn) {
                    searchListForFavorite(newText);
                } else {
                    searchList(newText);
                }
                return true;
            }
        });
        return viewF;
    }

    private void setupCategoryListener(FirebaseHandler firebaseHandler, String userId, final String category) {
        DatabaseReference categoryReference = firebaseHandler.getCategoryReference(userId, category);

        final int categoryIndex = Arrays.asList(CATEGORIES).indexOf(category);
        final List<Recipe> dataList = dataLists[categoryIndex];
        final MyAdapter adapter = adapters[categoryIndex];

        categoryReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    Recipe dataClass = itemSnapshot.getValue(Recipe.class);
                    dataList.add(dataClass);
                }
                adapter.notifyDataSetChanged();
                if (dataList.isEmpty()) {
                    dataList.add(emptyRecipe);
                }

                hideProgressIndicatorWhenAllRecipesLoaded();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage().toString(), Toast.LENGTH_SHORT).show();

                // Hide the progress indicator in case of an error.
                progressIndicator.hide();
            }
        });
    }

    private void hideProgressIndicatorWhenAllRecipesLoaded() {
        boolean allRecipesLoaded = true;
        for (List<Recipe> dataList : dataLists) {
            if (dataList.isEmpty()) {
                allRecipesLoaded = false;
                break;
            }
        }

        if (allRecipesLoaded) {
            progressIndicator.hide();
        }
    }

    public void showAllItems(){
        ArrayList<List<Recipe>> searchLists = new ArrayList<>();
        // To show everything
        for (int i = 0; i < CATEGORIES.length; i++) {
            List<Recipe> currentSearchList = new ArrayList<>();
            currentSearchList.addAll(dataLists[i]);
            searchLists.add(currentSearchList);
            adapters[i].searchDataList(currentSearchList);
        }
    }

    public void showFavoriteItems() {
        ArrayList<List<Recipe>> searchLists = new ArrayList<>();

        // Create lists based on favorite
        for (int i = 0; i < CATEGORIES.length; i++) {
            List<Recipe> currentSearchList = new ArrayList<>();
            for (Recipe recipe : dataLists[i]) {
                if (recipe.getIsFavorite()) {
                    currentSearchList.add(recipe);
                }
            }
            if (currentSearchList.isEmpty()) {
                currentSearchList.add(emptyRecipe);
            }
            searchLists.add(currentSearchList);
            adapters[i].searchDataList(currentSearchList);
        }
    }

    public void searchList(String text) {
        ArrayList<List<Recipe>> searchLists = new ArrayList<>();

        // Create lists based on search
        for (int i = 0; i < CATEGORIES.length; i++) {
            List<Recipe> currentSearchList = new ArrayList<>();
            if (!text.isEmpty()) {
                for (Recipe recipe : dataLists[i]) {
                    if (recipe.getName().toLowerCase().contains(text.toLowerCase())) {
                        currentSearchList.add(recipe);
                    }
                }
                if (currentSearchList.isEmpty()) {
                    currentSearchList.add(emptyRecipe);
                }
            } else {
                currentSearchList.addAll(dataLists[i]);
            }
            searchLists.add(currentSearchList);
            adapters[i].searchDataList(currentSearchList);
        }
    }

    public void searchListForFavorite(String text) {
        ArrayList<List<Recipe>> searchLists = new ArrayList<>();

        // Create lists based on search
        for (int i = 0; i < CATEGORIES.length; i++) {
            List<Recipe> currentSearchList = new ArrayList<>();
            if (!text.isEmpty()) {
                for (Recipe recipe : dataLists[i]) {
                    if ((recipe.getIsFavorite()) && recipe.getName().toLowerCase().contains(text.toLowerCase())) {
                        currentSearchList.add(recipe);
                    }
                }
            } else {
                for (Recipe recipe : dataLists[i]) {
                    if (recipe.getIsFavorite()) {
                        currentSearchList.add(recipe);
                    }
                }
            }
            if (currentSearchList.isEmpty()) {
                currentSearchList.add(emptyRecipe);
            }
            searchLists.add(currentSearchList);
            adapters[i].searchDataList(currentSearchList);
        }
    }
}
