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
import com.example.winningrecipe.Models.ApiObject;
import com.example.winningrecipe.Models.DataClass;
import com.example.winningrecipe.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MyAdapterApi extends RecyclerView.Adapter<MyViewHolderApi> {

    private Context context;
    private ArrayList<ApiObject> dataList;

    View viewF;
    FragmentManager fragmentManager;

    public MyAdapterApi(Context context, ArrayList<ApiObject> dataList, View viewF, FragmentManager fragmentManager) {
        this.context = context;
        this.dataList = dataList;
        this.viewF = viewF;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public MyViewHolderApi onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_api_recipe, parent, false);
        return new MyViewHolderApi(view);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolderApi holder, int position) {
        ApiObject currentRecipe = dataList.get(position);

        holder.recipeName.setText(currentRecipe.getDataName());
        holder.recipeDescription.setText(currentRecipe.getDataInstructions());
        holder.recipeCategory.setText(currentRecipe.getDataCategory());
        Glide.with(context).load(currentRecipe.getDataImage()).into(holder.recipeImage);

        holder.recipeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle args = new Bundle();
                args.putString("recipeImage",(currentRecipe.getDataImage()));
                args.putString("recipeName",currentRecipe.getDataName());
                args.putString("recipeIngredients", currentRecipe.getDataIngredients().replace(",",".\n").replace("\n ","\n"));
                args.putString("recipeInstructions",currentRecipe.getDataInstructions().replace(".",".\n").replace("\n ","\n"));
                args.putString("recipeCategory",currentRecipe.getDataCategory().replace(".",".\n").replace("\n ","\n"));

                fragmentManager.setFragmentResult("requestKey", args);
                Navigation.findNavController(viewF).navigate(R.id.action_home_api_objects_to_recipe_api_detail);

            }
        });
    }

    public void searchDataList(ArrayList<ApiObject> searchList){
        dataList = searchList;
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return dataList.size();
    }
}

class MyViewHolderApi extends RecyclerView.ViewHolder{

    ImageView recipeImage;
    TextView recipeName,  recipeDescription, recipeCategory;
    CardView recipeCard;


    public MyViewHolderApi(@NonNull View itemView) {
        super(itemView);

        recipeImage = itemView.findViewById(R.id.recipe_api_image);
        recipeDescription = itemView.findViewById(R.id.recipe_api_brief_description);
        recipeCategory = itemView.findViewById(R.id.recipe_api_category);
        recipeName = itemView.findViewById(R.id.recipe_api_name);
        recipeCard = itemView.findViewById(R.id.recipe_api_card);
    }
}

