package com.example.winningrecipe;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Utils.FirebaseHandler;
import Utils.Recipe;


public class AddRecipe extends Fragment {

    ImageView uploadImage;
    TextInputEditText uploadName, uploadIngredients, uploadCategory, uploadTime, uploadDescription;
    Button saveButton;
    Uri uri;


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View viewF = inflater.inflate(R.layout.fragment_add_recipe, container, false);

        // Add TextInputLayouts for error messages
        TextInputLayout textInputLayoutName = viewF.findViewById(R.id.textInputLayoutName);
        TextInputLayout textInputLayoutIngredients = viewF.findViewById(R.id.textInputLayoutIngredients);
        TextInputLayout textInputLayoutCategory = viewF.findViewById(R.id.textInputLayoutCategory);
        TextInputLayout textInputLayoutTime = viewF.findViewById(R.id.textInputLayoutTime);
        TextInputLayout textInputLayoutDescription = viewF.findViewById(R.id.textInputLayoutDescription);

        uploadImage = viewF.findViewById(R.id.upload_image);

        uploadName = viewF.findViewById(R.id.upload_name);
        uploadIngredients = viewF.findViewById(R.id.upload_ingredients);
        uploadCategory = viewF.findViewById(R.id.upload_category);
        uploadTime = viewF.findViewById(R.id.upload_time);
        uploadDescription = viewF.findViewById(R.id.upload_description);

        saveButton = viewF.findViewById(R.id.addRecipeToFirebase);

        uploadCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadCategory.setEnabled(true);

                // Initializing the popup menu and giving the reference as current context
                PopupMenu popupMenu = new PopupMenu(viewF.getContext(), uploadCategory);

                // Inflating popup menu from popup_menu.xml file
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu_category, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        uploadCategory.setText(menuItem.getTitle());
//                        uploadCategory.setEnabled(false);
                        // Toast message on menu item clicked
                        return true;
                    }
                });
                // Showing the popup menu
                popupMenu.show();
            }
        });

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            uri = data.getData();
                            uploadImage.setImageURI(uri);
                        }else {
                            Toast.makeText(getContext(), "No Image Selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        uploadImage.setOnClickListener(new View.OnClickListener() {

            // Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            @Override
            public void onClick(View v) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve input values from the EditText fields
                String name = uploadName.getText().toString();
                String ingredientsText = uploadIngredients.getText().toString();
                String category = uploadCategory.getText().toString();
                String timeText = uploadTime.getText().toString();
                String description = uploadDescription.getText().toString();

                List<String> ingredients;
                // Check if the ingredientsText is empty or contains only spaces
                if (ingredientsText.trim().isEmpty()) {
                    ingredients = new ArrayList<>(); // Create an empty list
                } else {
                    // Split the ingredientsText using a comma as the delimiter
                    ingredients = Arrays.asList(ingredientsText.split(","));
                }

                String preparationTimeText = uploadTime.getText().toString();
                int preparationTime;
                // Check if the timeText is not empty
                if (!preparationTimeText.trim().isEmpty()) {
                    // Parse the timeText to an integer
                    preparationTime = Integer.parseInt(timeText);
                } else {
                    // Handle the case where the time field is empty
                    preparationTime = -1;
                }

                // Validate each input field
                boolean isValidName = Recipe.isValidName(name);
                boolean isValidIngredients = Recipe.isValidIngredients(ingredients);
                boolean isValidCategory = Recipe.isValidCategory(category);
                boolean isValidTime = Recipe.isValidPreparationTime(preparationTime);
                boolean isValidDescription = Recipe.isValidDescription(description);

                // Display validation errors (if any)
                if (!isValidName) {
                    textInputLayoutName.setError("Name is required.");
                } else {
                    textInputLayoutName.setError(null); // Clear error message if valid
                }

                if (!isValidIngredients) {
                    textInputLayoutIngredients.setError("Ingredients are required.");
                } else {
                    textInputLayoutIngredients.setError(null); // Clear error message if valid
                }

                if (!isValidCategory) {
                    textInputLayoutCategory.setError("Category is required.");
                } else {
                    textInputLayoutCategory.setError(null); // Clear error message if valid
                }

                if (!isValidTime || preparationTime < 0) {
                    textInputLayoutTime.setError("Preparation time is required.");
                } else {
                    textInputLayoutTime.setError(null); // Clear error message if valid
                }

                if (!isValidDescription) {
                    textInputLayoutDescription.setError("Description is required.");
                } else {
                    textInputLayoutDescription.setError(null); // Clear error message if valid
                }

                // If all fields are valid, create and add the recipe
                if (isValidName && isValidIngredients && isValidCategory && isValidTime && isValidDescription) {
                    // Create a new Recipe object
                    Recipe recipe = new Recipe(name, ingredients, category, Integer.parseInt(timeText), description);

                    FirebaseHandler firebaseHandler = new FirebaseHandler();
                    firebaseHandler.addRecipe(recipe.getCategory(), recipe.getName(), recipe);
                }
            }
        });

        return viewF;
    }
}