package Utils;
import com.example.winningrecipe.MainActivity;
import com.example.winningrecipe.R.layout;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.winningrecipe.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseHandler {
    private final DatabaseReference databaseReference;
    public FirebaseHandler() {
        // Initialize Firebase database reference
        this.databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void addRecipe(String category, String recipeId, Recipe recipe, Activity activity) {
        // Assuming "categories" is the top-level node in Firebase
        DatabaseReference categoryRef = databaseReference.child("categories").child(category);
        Log.d("FirebaseHandler", "Trying to add a recipe.");
        categoryRef.child(recipeId).setValue(recipe)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("FirebaseHandler", "Recipe added successfully.");

                        // Reset fields in Upload Recipe form
                        if (activity != null) {
                            TextInputEditText uploadName = activity.findViewById(R.id.upload_name);
                            TextInputEditText uploadIngredients = activity.findViewById(R.id.upload_ingredients);
                            TextInputEditText uploadCategory = activity.findViewById(R.id.upload_category);
                            TextInputEditText uploadTime = activity.findViewById(R.id.upload_time);
                            TextInputEditText uploadDescription = activity.findViewById(R.id.upload_description);
                            ImageView uploadImage = activity.findViewById(R.id.upload_image);

                            if (uploadName != null) {
                                uploadName.setText("");
                            }
                            if (uploadIngredients != null) {
                                uploadIngredients.setText("");
                            }
                            if (uploadCategory != null) {
                                uploadCategory.setText("");
                            }
                            if (uploadTime != null) {
                                uploadTime.setText("");
                            }
                            if (uploadDescription != null) {
                                uploadDescription.setText("");
                            }
                            if (uploadImage != null) {
                                uploadImage.setImageResource(R.drawable.add_image);
                            }
                        }

                        // Display a success message
                        Toast.makeText(activity, "Recipe added successfully.", Toast.LENGTH_SHORT).show();

                        // Move to MainActivity after complete
                        Intent intent = new Intent(activity, MainActivity.class);
                        activity.startActivity(intent);
                        activity.finish(); // Close the current activity if needed
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Display a failure message
                        Toast.makeText(activity, "Write to DB failed.", Toast.LENGTH_SHORT).show();

                        Log.e("FirebaseHandler", "Failed to add recipe: " + e.getMessage());
                    }
                });
    }



    public void removeRecipe(String category, String recipeId) {
        // Assuming "categories" is the top-level node in Firebase
        DatabaseReference categoryRef = databaseReference.child("categories").child(category);
        categoryRef.child(recipeId).removeValue();
    }

    public void updateRecipe(String category, String recipeId, Recipe updatedRecipe) {
        // Assuming "categories" is the top-level node in Firebase
        DatabaseReference categoryRef = databaseReference.child("categories").child(category);
        categoryRef.child(recipeId).setValue(updatedRecipe);
    }

    // Get a reference to a specific category in the database
    public DatabaseReference getCategoryReference(String category) {
        // Assuming "categories" is the top-level node in Firebase
        return databaseReference.child("categories").child(category);
    }
}
