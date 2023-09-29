package com.example.winningrecipe;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import Utils.FirebaseHandler;
import Utils.Recipe;


public class AddRecipe extends Fragment {

    ImageView uploadImage;
    TextInputEditText uploadName, uploadIngredients, uploadCategory, uploadTime, uploadDescription;
    Button saveButton;
    Uri uri;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View viewF = inflater.inflate(R.layout.fragment_add_recipe, container, false);

        uploadImage = viewF.findViewById(R.id.upload_image);

        uploadName = viewF.findViewById(R.id.upload_name);
        uploadIngredients = viewF.findViewById(R.id.upload_ingredients);
        uploadCategory = viewF.findViewById(R.id.upload_category);
        uploadTime = viewF.findViewById(R.id.upload_time);
        uploadDescription = viewF.findViewById(R.id.upload_description);

        saveButton = viewF.findViewById(R.id.addRecipeToFirebase);

//        uploadCategory.setEnabled(false);

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

                List<String> listt = new ArrayList<>();
                listt.add(uploadIngredients.getText().toString());
                Recipe recipe = new Recipe(uploadName.getText().toString(), listt," ",uploadCategory.getText().toString(), Integer.parseInt(uploadTime.getText().toString()), uploadDescription.getText().toString());

                FirebaseHandler firebaseHandler = new FirebaseHandler();
                firebaseHandler.addRecipe(recipe.getCategory(), recipe.getName(), recipe, uri);
            }
        });

        return viewF;
    }
}