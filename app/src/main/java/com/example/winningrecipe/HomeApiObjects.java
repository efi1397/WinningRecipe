package com.example.winningrecipe;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import com.example.winningrecipe.Adapters.MyAdapter;
import com.example.winningrecipe.Adapters.MyAdapterApi;
import com.example.winningrecipe.Models.ApiObject;
import com.example.winningrecipe.Models.DataClass;
import com.example.winningrecipe.R;
import com.example.winningrecipe.Services.DataService;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import Utils.Recipe;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeApiObjects#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeApiObjects extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeApiObjects() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment show_api_recipeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeApiObjects newInstance(String param1, String param2) {
        HomeApiObjects fragment = new HomeApiObjects();
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


    RecyclerView recyclerView;
    SearchView searchView;
    ArrayList<ApiObject> dataList;
    ValueEventListener eventListener;
    String url;
    MyAdapterApi adapterApi;
    TextView userEmailTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View viewF = inflater.inflate(R.layout.fragment_home_api_objects,container,false);
        userEmailTextView = viewF.findViewById(R.id.userEmailTextView);
        recyclerView = viewF.findViewById(R.id.recyclerViewApi);
        searchView = viewF.findViewById(R.id.searchApi);
        searchView.clearFocus();

        String user = SingletonUser.getInstance().getUser().replace(".", ",");
        Toast.makeText(getContext(), "Loading Data...", Toast.LENGTH_SHORT).show();
        // Show the username without the email in home page
        String username = user.replace(",", ".");
        String extractedUsername = username.split("@")[0];
        userEmailTextView.setText(MessageFormat.format("Hey {0}, what you''d like to make today?", extractedUsername));

        showData();

        adapterApi = new MyAdapterApi(getContext(), dataList, viewF, getParentFragmentManager());
        recyclerView.setAdapter(adapterApi);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        adapterApi =new MyAdapterApi(getContext(),dataList, viewF, getParentFragmentManager());
        recyclerView.setAdapter(adapterApi);

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Navigation.findNavController(viewF).navigate(R.id.action_home_api_objects_to_home);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), callback);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchList(newText);
                return true;
            }
        });

        return viewF;
    }

    private void showData() {
        DataService dataService = new DataService();

        // Search meal by first letter
        String str = "bcdefghijklmnopqrstuvwxyz";

        dataList = dataService.getRecipesFromApi('a');
        for (char ch : str.toCharArray()) {
            dataList.addAll(dataService.getRecipesFromApi(ch));
        }
    }

    public void searchList(String text){
        ArrayList<ApiObject> searchList = new ArrayList<>();
        for (ApiObject dataClass:dataList){
            if(dataClass.getDataName().toLowerCase().contains(text.toLowerCase())){
                searchList.add(dataClass);
            }
        }
        adapterApi.searchDataList(searchList);
    }
}