package Utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.List;

public class FirebaseHandler {
    private final DatabaseReference databaseReference;

    public FirebaseHandler() {
        // Initialize Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void addRecipe(String category, String recipeId, Recipe recipe) {
        // Assuming "categories" is the top-level node in Firebase
        DatabaseReference categoryRef = databaseReference.child("categories").child(category);
        categoryRef.child(recipeId).setValue(recipe);
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
