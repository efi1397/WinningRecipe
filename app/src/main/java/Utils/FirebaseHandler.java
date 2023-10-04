package Utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.winningrecipe.MainActivity;
import com.example.winningrecipe.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class FirebaseHandler {
    private final DatabaseReference databaseReference;
    private final StorageReference storageReference;

    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }

    public StorageReference getStorageReference() {
        return storageReference;
    }

    public FirebaseHandler() {
        // Initialize Firebase database reference
        this.databaseReference = FirebaseDatabase.getInstance().getReference();
        // Initialize Firebase Storage reference with the specific bucket URL
        this.storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://winningrecipe-5f0f1.appspot.com");
    }

    public void createNewUser(String email) {
        email = email.replace(".", ",");
        DatabaseReference userRef = databaseReference.child("Users").child(email);

        // Create a "Categories" node under the user's email
        DatabaseReference categoriesRef = userRef.child("Categories");
        categoriesRef.setValue(null); // Trigger a write operation to create the "Categories" node

        // Create a "UserInfo" node under the user's email
        DatabaseReference userInfoRef = userRef.child("UserInfo");
        userInfoRef.child("email").setValue(email); // You can set other user information here as well
    }


    public void addRecipe(String category, String recipeId, Recipe recipe, Activity activity) {
        DatabaseReference categoryRef = databaseReference.child("categories").child(category);
        Log.d("FirebaseHandler", "Trying to add a recipe.");

        if (!TextUtils.isEmpty(recipe.getImageUrl())) {
            uploadImageAndAddRecipe(categoryRef, category, recipeId, recipe, activity);
        } else {
            // If no image to upload, continue with adding the recipe to the database
            addNewRecipe(categoryRef, category, recipeId, recipe, activity);
        }
    }
    private void uploadImageAndAddRecipe(DatabaseReference categoryRef, String category, String recipeId, Recipe recipe, Activity activity) {
        uploadImageToFirebaseStorage(recipe, activity, new OnImageUploadListener() {
            @Override
            public void onImageUploadSuccess(String imageUrl) {
                if (imageUrl != null) {
                    recipe.setImageUrl(imageUrl);
                }
                // Image upload finished, now add the recipe to the database
                addNewRecipe(categoryRef, category, recipeId, recipe, activity);
            }

            @Override
            public void onImageUploadFailure(Exception e) {
                Log.e("FirebaseHandler", "Image upload failed: " + e.getMessage(), e);
                // Add the recipe to the database without an image
                addNewRecipe(categoryRef, category, recipeId, recipe, activity);
            }
        });
    }

    // Interface to handle callbacks for image upload, so imageUrl contains the updated URL
    public interface OnImageUploadListener {
        void onImageUploadSuccess(String imageUrl);

        void onImageUploadFailure(Exception e);
    }

    private void uploadImageToFirebaseStorage(Recipe recipe, Activity activity, OnImageUploadListener listener) {
        // Create a reference to the image file in Firebase Storage
        String imagePath = "recipe_images/" + UUID.randomUUID().toString() + ".jpg";
        StorageReference imageRef = storageReference.child(imagePath);

        // Create a content resolver to get the input stream from the image URI
        ContentResolver contentResolver = activity.getContentResolver();
        try {
            InputStream imageStream = contentResolver.openInputStream(Uri.parse(recipe.getImageUrl()));

            if (imageStream != null) {
                // Upload the image to Firebase Storage
                UploadTask uploadTask = imageRef.putStream(imageStream);

                // Register observers to listen for the upload process so imageUrl contains the updated URL
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Image uploaded successfully
                        Toast.makeText(activity, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                        Log.d("Image", "Finish uploading image");

                        // Get the HTTP URL of the uploaded image
                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri downloadUri) {
                                if (downloadUri != null) {
                                    // Set the HTTP URL in the Recipe object
                                    Log.d("Image", "Finish uploading image");
                                    listener.onImageUploadSuccess(downloadUri.toString());
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle any errors in getting the download URL
                                listener.onImageUploadFailure(e);
                                Toast.makeText(activity, "Failed to get image URL", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle any errors during the upload
                        listener.onImageUploadFailure(e);
                        Toast.makeText(activity, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                // Handle the case where the image stream is null
                listener.onImageUploadFailure(new IOException("Image stream is null"));
                Toast.makeText(activity, "Image not found", Toast.LENGTH_SHORT).show();
            }
        } catch (FileNotFoundException e) {
            // Handle the case where the image file cannot be found
            listener.onImageUploadFailure(e);
            Toast.makeText(activity, "Image not found", Toast.LENGTH_SHORT).show();
        }
    }

    public void addNewRecipe(DatabaseReference categoryRef, String category, String recipeId, Recipe recipe, Activity activity) {
        categoryRef.child(recipeId).setValue(recipe)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firebase Handler", "Recipe added successfully.");

                        // Reset fields in Upload Recipe form
                        resetUploadRecipeFields(activity);

                        // Display a success message
                        Toast.makeText(activity, "Recipe added successfully.", Toast.LENGTH_SHORT).show();

                        // Move to MainActivity after complete
                        //Intent intent = new Intent(activity, MainActivity.class);
                        //activity.startActivity(intent);
                        //activity.finish(); // Close the current activity if needed
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

    // Add a method to add a category for a user
    public void addCategoryForUser(String userId, String category) {
        DatabaseReference userCategoriesRef = databaseReference.child("Users").child(userId).child("Categories");
        userCategoriesRef.child(category).setValue(true);
    }

    // Add a method to remove a category for a user
    public void removeCategoryForUser(String userId, String category) {
        DatabaseReference userCategoriesRef = databaseReference.child("Users").child(userId).child("Categories");
        userCategoriesRef.child(category).removeValue();
    }

    // Add a method to get all categories for a user
    public void getCategoriesForUser(String userId, ValueEventListener valueEventListener) {
        DatabaseReference userCategoriesRef = databaseReference.child("Users").child(userId).child("Categories");
        userCategoriesRef.addValueEventListener(valueEventListener);
    }

    // Add a method to get recipes for a specific category under a user
    public void getRecipesForCategory(String userId, String category, ValueEventListener valueEventListener) {
        DatabaseReference categoryRef = databaseReference.child("Users").child(userId).child("Categories").child(category);
        categoryRef.addValueEventListener(valueEventListener);
    }

    // Add a method to add a recipe for a specific category under a user
    public void addRecipeForCategory(String userId, String category, String recipeId, Recipe recipe, Activity activity) {
        DatabaseReference categoryRef = databaseReference.child("Users").child(userId).child("Categories").child(category);

        if (!TextUtils.isEmpty(recipe.getImageUrl())) {
            uploadImageAndAddRecipe(categoryRef, category, recipeId, recipe, activity);
        } else {
            addNewRecipe(categoryRef, category,recipeId, recipe, activity);
        }
    }

    public void getRecipesForUserAndCategory(String userId, String category, ValueEventListener valueEventListener) {
        DatabaseReference userRef = databaseReference.child("Users").child(userId).child("Categories").child(category);
        userRef.addValueEventListener(valueEventListener);
    }

    private void resetUploadRecipeFields(Activity activity) {
        TextInputEditText uploadName = activity.findViewById(R.id.upload_name);
        TextInputEditText uploadIngredients = activity.findViewById(R.id.upload_ingredients);
        TextInputEditText uploadCategory = activity.findViewById(R.id.upload_category);
        TextInputEditText uploadTime = activity.findViewById(R.id.upload_time);
        TextInputEditText uploadDescription = activity.findViewById(R.id.upload_description);
        ImageView uploadImage = activity.findViewById(R.id.upload_image);

        resetTextField(uploadName);
        resetTextField(uploadIngredients);
        resetTextField(uploadCategory);
        resetTextField(uploadTime);
        resetTextField(uploadDescription);

        if (uploadImage != null) {
            uploadImage.setImageResource(R.drawable.add_image);
        }
    }

    private void resetTextField(TextInputEditText textField) {
        if (textField != null) {
            textField.setText("");
        }
    }

    public void getRecipesByCategory(String category, ValueEventListener valueEventListener) {
        DatabaseReference categoryRef = databaseReference.child("categories").child(category);
        categoryRef.addValueEventListener(valueEventListener);
    }

    public void removeRecipe(String user, String category, String recipeName) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(user).child("Categories").child(category);

        databaseReference.child(recipeName).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("FirebaseHandler", "Recipe removed successfully.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle the failure to remove the recipe
                        Log.e("FirebaseHandler", "Failed to remove recipe: " + e.getMessage());
                    }
                });
    }

    public void updateRecipe(String user, String category, Recipe updatedRecipe) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(user).child("Categories").child(category).child(updatedRecipe.getName());
        databaseReference.setValue(updatedRecipe)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("FirebaseHandler", "Recipe updated successfully.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle the failure to update the recipe
                        Log.e("FirebaseHandler", "Failed to update recipe: " + e.getMessage());
                    }
                });
    }

    // Method to get the DatabaseReference for a specific user's categories
    public DatabaseReference getUserCategoriesReference(String userId) {
        return getDatabaseReference().child("Users").child(userId).child("Categories");
    }

    // Method to get the DatabaseReference for a specific category
    public DatabaseReference getCategoryReference(String userId, String category) {
        return getUserCategoriesReference(userId).child(category);
    }
}
