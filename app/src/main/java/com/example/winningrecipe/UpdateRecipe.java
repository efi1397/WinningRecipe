package com.example.winningrecipe;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import Utils.FirebaseHandler;
import Utils.Recipe;


public class UpdateRecipe extends Fragment {

    ImageView uploadImage;
    TextInputEditText uploadName, uploadIngredients, uploadCategory, uploadTime, uploadDescription;
    Button saveButton;
    Uri uri;
    String user;
    String updateImageUrl = "";
    String originalName= "";
    String originalCategory = "";

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View viewF = inflater.inflate(R.layout.fragment_update_recipe, container, false);

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                Log.d("result" , "yes");
                Navigation.findNavController(viewF).navigate(R.id.action_updateRecipe_to_home);
            }
        };


        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), callback);

        // Add TextInputLayouts for error messages
        TextInputLayout textInputLayoutName = viewF.findViewById(R.id.textInputLayoutName);
        TextInputLayout textInputLayoutIngredients = viewF.findViewById(R.id.textInputLayoutIngredients);
        TextInputLayout textInputLayoutCategory = viewF.findViewById(R.id.textInputLayoutCategory);
        TextInputLayout textInputLayoutTime = viewF.findViewById(R.id.textInputLayoutTime);
        TextInputLayout textInputLayoutDescription = viewF.findViewById(R.id.textInputLayoutDescription);

        uploadName = viewF.findViewById(R.id.upload_name);
        uploadIngredients = viewF.findViewById(R.id.upload_ingredients);
        uploadCategory = viewF.findViewById(R.id.upload_category);
        uploadTime = viewF.findViewById(R.id.upload_time);
        uploadDescription = viewF.findViewById(R.id.upload_description);
        uploadImage = viewF.findViewById(R.id.upload_image);

        saveButton = viewF.findViewById(R.id.updateRecipeToFirebase);

        // Disable text input in the uploadCategory field
        uploadCategory.setFocusable(false);

        user = SingletonUser.getInstance().getUser().replace(".", ",");

        getParentFragmentManager().setFragmentResultListener("requestKey_editbtn", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {

                String recipeName = result.getString("recipeName");
                originalName = recipeName;
                String recipeCategory = result.getString("recipeCategory");
                originalCategory = recipeCategory;
                String recipeDescription = result.getString("description");
                String recipeIngredients = result.getString("ingredients");
                String preparationTime = result.getString("preparationTime");
                updateImageUrl = result.getString("imageUrl");

                uploadName.setText(recipeName);
                uploadCategory.setText(recipeCategory);
                uploadDescription.setText(recipeDescription);
                uploadTime.setText(preparationTime);

                if (!TextUtils.isEmpty(recipeIngredients)) {
                    // Remove square brackets from the string
                    recipeIngredients = recipeIngredients.replace("[", "").replace("]", "");
                    uploadIngredients.setText(recipeIngredients);
                }


            }
        });
        uploadCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCategoryPopupMenu(view);
            }
        });

        // Upload image
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

                if (!(isValidName && isValidIngredients && isValidCategory && isValidTime && isValidDescription)) {
                    Toast.makeText(getActivity(), "Please follow the instructions.", Toast.LENGTH_SHORT).show();
                }

                // If all fields are valid, create and update the recipe
                if (isValidName && isValidIngredients && isValidCategory && isValidTime && isValidDescription) {
                    // Convert the Uri to a String, upload image is optional
                    String imageUrl = (uri != null) ? uri.toString() : updateImageUrl;

                    // Create a new Recipe object
                    Recipe recipe = new Recipe(name, ingredients, category, preparationTime, description, imageUrl);

                    // Add recipe to DB
                    FirebaseHandler firebaseHandler = new FirebaseHandler();
                    firebaseHandler.removeRecipe(user, originalCategory, originalName);
                    firebaseHandler.addRecipeForCategory(user, category, name, recipe, getActivity());
                    Navigation.findNavController(viewF).navigate(R.id.action_updateRecipe_to_home);
                }
            }
        });

        return viewF;
    }

    // Show the category popup menu
    private void showCategoryPopupMenu(View view) {
        // Initializing the popup menu and giving the reference as current context
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);

        // Inflating popup menu from popup_menu_category.xml file
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu_category, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                uploadCategory.setText(menuItem.getTitle());
                // Toast message on menu item clicked
                return true;
            }
        });

        // Showing the popup menu
        popupMenu.show();
    }
}
