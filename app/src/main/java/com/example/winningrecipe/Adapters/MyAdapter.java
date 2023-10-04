package com.example.winningrecipe.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.winningrecipe.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import Utils.FirebaseHandler;
import Utils.Recipe;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder>{

    private Context context;
    private List<Recipe> dataList;
    View viewF;
    FragmentManager fragmentManager;
    Boolean isFavorite = false;
    String addRecipeUrl = "https://firebasestorage.googleapis.com/v0/b/winningrecipe-5f0f1.appspot.com/o/recipe_images%2F24eaea92-c9a2-492e-8f2e-d926c3a0958d.jpg?alt=media&token=0fd294b8-70ff-4401-8144-61db30693f03";

    String user;

    public MyAdapter(Context context, List<Recipe> dataList, View viewF, FragmentManager fragmentManager, String user) {
        this.context = context;
        this.dataList = dataList;
        this.viewF = viewF;
        this.fragmentManager = fragmentManager;
        this.user = user;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new MyViewHolder(view);    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(dataList.get(position).getImageUrl()).into(holder.recipeImage);

        if (Objects.equals(dataList.get(position).getImageUrl(), addRecipeUrl)) {
            holder.recipeName.setText("");
        }else {
            holder.recipeName.setText(dataList.get(position).getName());
        }

        holder.recipeConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Objects.equals(dataList.get(position).getImageUrl(), addRecipeUrl)){
                    Bundle args = new Bundle();
                    args.putString("recipeName", dataList.get(holder.getAdapterPosition()).getName());
                    args.putString("recipeCategory", dataList.get(holder.getAdapterPosition()).getCategory());
                    args.putString("description", dataList.get(holder.getAdapterPosition()).getDescription());
                    args.putString("imageUrl", dataList.get(holder.getAdapterPosition()).getImageUrl());
                    args.putString("ingredients", dataList.get(holder.getAdapterPosition()).getIngredients().toString());
                    args.putString("preparationTime", String.valueOf(dataList.get(holder.getAdapterPosition()).getPreparationTime()));
                    fragmentManager.setFragmentResult("requestKey", args);

                    Navigation.findNavController(viewF).navigate(R.id.action_home_to_recipeDetailsFragment);
                } else {
                    Navigation.findNavController(viewF).navigate(R.id.action_home4_to_addRecipe);

                }

            }
        }
        
        );

        FirebaseHandler firebaseHandler = new FirebaseHandler();

        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toggle the isFavorite state of the corresponding Recipe object
                Recipe recipe = dataList.get(position);
                firebaseHandler.removeRecipe(user.replace(".",","),recipe.getCategory(),recipe.getName());
                Log.d("Remove recipe","Recipe removed successfully.");
            }
        });
        holder.favoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toggle the isFavorite state of the corresponding Recipe object
                Recipe recipe = dataList.get(position);
                recipe.setIsFavorite(!recipe.getIsFavorite());
                firebaseHandler.updateRecipe(user.replace(".",","),recipe.getCategory(),recipe);
                // Update the favorite button state based on the updated isFavorite state
                if (recipe.getIsFavorite()) {
                    holder.favoriteBtn.setImageResource(R.drawable.baseline_white_favorite_24);
                } else {
                    holder.favoriteBtn.setImageResource(R.drawable.baseline_white_favorite_border_24);
                }
            }
        });
        holder.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toggle the isFavorite state of the corresponding Recipe object
                Recipe recipe = dataList.get(position);
                // Create here function that represent an edit form for the recipe and then use updateRecipe function

                Bundle args = new Bundle();
                args.putString("recipeName", dataList.get(holder.getAdapterPosition()).getName());
                args.putString("recipeCategory", dataList.get(holder.getAdapterPosition()).getCategory());
                args.putString("description", dataList.get(holder.getAdapterPosition()).getDescription());
                args.putString("imageUrl", dataList.get(holder.getAdapterPosition()).getImageUrl());
                args.putString("ingredients", dataList.get(holder.getAdapterPosition()).getIngredients().toString());
                args.putString("preparationTime", String.valueOf(dataList.get(holder.getAdapterPosition()).getPreparationTime()));
                fragmentManager.setFragmentResult("requestKey_editbtn", args);

                Navigation.findNavController(viewF).navigate(R.id.action_home_to_updateRecipe);
                Log.d("Update recipe","Recipe changed successfully.");
            }
        });
    }

    @Override
    public int getItemCount() {return dataList.size();}

    public void searchDataList(List<Recipe> searchList){
        dataList = searchList;
        notifyDataSetChanged();
    }
}
class MyViewHolder extends RecyclerView.ViewHolder{

    ImageButton deleteBtn;
    ImageView editBtn;
    ImageView recipeImage;
    ImageButton favoriteBtn;

    TextView recipeName;
    ConstraintLayout recipeConstraintLayout;


    public MyViewHolder(@NonNull View itemView) {
        super(itemView);

        recipeImage = itemView.findViewById(R.id.recipe_image);
        recipeName = itemView.findViewById(R.id.recipe_name);
        recipeConstraintLayout = itemView.findViewById(R.id.recipe_card);
        favoriteBtn = itemView.findViewById(R.id.favoriteBtn);
        deleteBtn = itemView.findViewById(R.id.deleteBtn);
        editBtn = itemView.findViewById(R.id.editBtn);
    }
}