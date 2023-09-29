package Utils;

import android.app.AlertDialog;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.winningrecipe.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class FirebaseHandler {
    private final DatabaseReference databaseReference;
    private final StorageReference storageReference;
    public FirebaseHandler() {
        // Initialize Firebase database reference
        this.databaseReference = FirebaseDatabase.getInstance().getReference();
        this.storageReference = FirebaseStorage.getInstance().getReference().child("Android Images");
    }

    public void addRecipe(String category, String recipeId, Recipe recipe, Uri uri) {

        storageReference.child(uri.getLastPathSegment()).putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                recipe.setImage(uriTask.getResult().toString());

                // Assuming "categories" is the top-level node in Firebase
                DatabaseReference categoryRef = databaseReference.child("categories").child(category);
                categoryRef.child(recipeId).setValue(recipe);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("error", e.getMessage());
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
