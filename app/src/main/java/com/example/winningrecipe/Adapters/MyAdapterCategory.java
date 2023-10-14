package com.example.winningrecipe.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.winningrecipe.R;

import java.util.ArrayList;

import Utils.Recipe;

public class MyAdapterCategory extends RecyclerView.Adapter<MyViewHolderCategory> {

    private Context context;
    private ArrayList<Recipe> dataList;

    View viewF;
    FragmentManager fragmentManager;

    public MyAdapterCategory(Context context,ArrayList<Recipe> dataList, View viewF, FragmentManager fragmentManager) {
        this.dataList = dataList;
        this.viewF = viewF;
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public MyViewHolderCategory onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_category_recipe, parent, false);
        return new MyViewHolderCategory(view);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolderCategory holder, int position) {
        Recipe currentRecipe = dataList.get(position);

        holder.recipeName.setText(currentRecipe.getName());
        //holder.recipeDescription.setText(currentRecipe.getDataInstructions());
        //holder.recipeCategory.setText(currentRecipe.getDataCategory());
        Glide.with(context).load(currentRecipe.getImageUrl()).into(holder.recipeImage);

        holder.recipeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle args = new Bundle();
                args.putString("recipeName", dataList.get(holder.getAdapterPosition()).getName());
                args.putString("recipeCategory", dataList.get(holder.getAdapterPosition()).getCategory());
                args.putString("description", dataList.get(holder.getAdapterPosition()).getDescription());
                args.putString("imageUrl", dataList.get(holder.getAdapterPosition()).getImageUrl());
                args.putString("ingredients", dataList.get(holder.getAdapterPosition()).getIngredients().toString());
                args.putString("preparationTime", String.valueOf(dataList.get(holder.getAdapterPosition()).getPreparationTime()));
                fragmentManager.setFragmentResult("requestKey", args);

                Navigation.findNavController(viewF).navigate(R.id.action_categoryRecipesFragment_to_recipe_details_fragment);

            }
        });
    }

    public void searchDataList(ArrayList<Recipe> searchList){
        dataList = searchList;
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return dataList.size();
    }
}

class MyViewHolderCategory extends RecyclerView.ViewHolder{

    ImageView recipeImage;
    TextView recipeName,  recipeDescription, recipeCategory;
    CardView recipeCard;


    public MyViewHolderCategory(@NonNull View itemView) {
        super(itemView);

        recipeImage = itemView.findViewById(R.id.recipe_image);
        recipeName = itemView.findViewById(R.id.recipe_name);
        recipeCard = itemView.findViewById(R.id.recipe_card);
    }
}

