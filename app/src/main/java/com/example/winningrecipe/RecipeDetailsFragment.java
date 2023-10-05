package com.example.winningrecipe;


import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;

import Utils.FirebaseHandler;

public class RecipeDetailsFragment extends Fragment {

    ImageView single_recipe_image;
    TextView recipe_name, recipe_category, recipe_preparation_time, recipe_ingredients, recipe_instructions;
    DatabaseReference databaseReference;
    String imageUrl = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View viewF =  inflater.inflate(R.layout.fragment_recipe_details, container, false);

        single_recipe_image = viewF.findViewById(R.id.single_recipe_image);

        recipe_name = viewF.findViewById(R.id.recipe_name);
        recipe_category = viewF.findViewById(R.id.recipe_category);
        recipe_preparation_time = viewF.findViewById(R.id.recipe_preparation_time);
        recipe_ingredients = viewF.findViewById(R.id.recipe_ingredients);
        recipe_instructions = viewF.findViewById(R.id.recipe_instructions);

        //SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);

        FirebaseHandler firebaseHandler = new FirebaseHandler();
        databaseReference = firebaseHandler.getDatabaseReference().child("categories");

        getParentFragmentManager().setFragmentResultListener("requestKey", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {

                recipe_name.setText(result.getString("recipeName"));
                recipe_category.setText(result.getString("recipeCategory"));
                recipe_instructions.setText(result.getString("description"));
                String ingredients = result.getString("ingredients");
                if (!TextUtils.isEmpty(ingredients)) {
                    // Remove square brackets from the string
                    ingredients = ingredients.replace("[", "").replace("]", "");
                }
                recipe_ingredients.setText(ingredients);
                recipe_preparation_time.setText(String.format("%s minutes", result.getString("preparationTime"))); // add minutes to preparation time
                imageUrl = result.getString("imageUrl");
                Glide.with(getContext()).load(imageUrl).into(single_recipe_image);


            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                Log.d("result" , "yes");
                Navigation.findNavController(viewF).navigate(R.id.action_recipeDetailsFragment_to_home);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), callback);
        return viewF;
    }
}