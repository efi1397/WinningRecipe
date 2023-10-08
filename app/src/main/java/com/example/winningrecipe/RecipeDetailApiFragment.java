package com.example.winningrecipe;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecipeDetailApiFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecipeDetailApiFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RecipeDetailApiFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DetailApi.
     */
    // TODO: Rename and change types and number of parameters
    public static RecipeDetailApiFragment newInstance(String param1, String param2) {
        RecipeDetailApiFragment fragment = new RecipeDetailApiFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    ImageView detailImage;
    TextView detailName, detailIngredients, detailInstructions, detailCategory;
    String key = "";
    String imageUrl = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View viewF = inflater.inflate(R.layout.fragment_recipe_api_detail,container,false);

        detailName = viewF.findViewById(R.id.detail_api_name);
        detailCategory = viewF.findViewById(R.id.detail_api_category);
        detailIngredients = viewF.findViewById(R.id.detail_api_ingredients);
        detailInstructions = viewF.findViewById(R.id.detail_api_instructions);
        detailImage = viewF.findViewById(R.id.detail_api_image);
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Navigation.findNavController(viewF).navigate(R.id.action_recipe_detail_api_to_home_api_objects);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), callback);
        getParentFragmentManager().setFragmentResultListener("requestKey", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                detailName.setText(result.getString("recipeName"));
                detailIngredients.setText(result.getString("recipeIngredients"));
                detailInstructions.setText(result.getString("recipeInstructions"));
                detailCategory.setText(result.getString("recipeCategory"));
                key = result.getString("key");
                imageUrl = result.getString("recipeImage");
                Glide.with(getContext()).load(result.getString("recipeImage")).into(detailImage);
            }
        });

        return viewF;
    }
}