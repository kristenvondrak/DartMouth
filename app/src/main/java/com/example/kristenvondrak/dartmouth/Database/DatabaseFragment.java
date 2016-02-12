package com.example.kristenvondrak.dartmouth.Database;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.kristenvondrak.dartmouth.Diary.AddUserMealActivity;
import com.example.kristenvondrak.dartmouth.Main.Constants;
import com.example.kristenvondrak.dartmouth.Main.SearchHeader;
import com.example.kristenvondrak.dartmouth.Main.Utils;
import com.example.kristenvondrak.dartmouth.Menu.NutritionFragment;
import com.example.kristenvondrak.dartmouth.Parse.ParseAPI;
import com.example.kristenvondrak.dartmouth.R;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kristenvondrak on 2/11/16.
 */
public class DatabaseFragment extends NutritionFragment implements SearchHeader{

    private ViewFlipper m_ViewFlipper;
    private ListView m_ListView;
    private ProgressBar m_ProgressSpinner;
    private TextView m_PromptTextView;
    private RequestQueue m_RequestQueue;


    private List<DatabaseRecipe> m_DbRecipesList;
    private DatabaseListAdapter m_DbRecipesListAdapter;
    private AddUserMealActivity m_AddUserMealActivity;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_database, container, false);

        m_Activity = getActivity();
        m_AddUserMealActivity = (AddUserMealActivity) m_Activity;
        m_Calendar = ((AddUserMealActivity)m_Activity).getCalendar();
        m_SelectedUserMeal = ((AddUserMealActivity)m_Activity).getUserMeal();
        initializeViews(v);
        initializeListeners();

        m_DbRecipesList = new ArrayList<>();
        m_DbRecipesListAdapter = new DatabaseListAdapter(m_Activity, this, m_DbRecipesList);
        m_ListView.setAdapter(m_DbRecipesListAdapter);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        // If search open, make request
        if (m_AddUserMealActivity.inSearchMode()) {
            queryDbRecipes(m_AddUserMealActivity.getSearchText());

        // Otherwise open search bar and show keyboard
        } else {
            m_AddUserMealActivity.getSearchBtn().callOnClick();
        }
    }


    // ------------------------------------------------------------------------------------- Views

    private void initializeViews(View v) {
        // Main View
        m_ListView = (ListView) v.findViewById(R.id.db_listview);
        m_ViewFlipper = (ViewFlipper) v.findViewById(R.id.db_viewflipper);
        m_ProgressSpinner = (ProgressBar) v.findViewById(R.id.progress_spinner);
        m_PromptTextView = (TextView) v.findViewById(R.id.db_prompt_textview);

        // Nutrition View
        m_RecipeNutrientsView = v.findViewById(R.id.nutrients);
        m_RecipeName = (TextView) v.findViewById(R.id.name);
        m_NumberPickerWhole = (NumberPicker) v.findViewById(R.id.servings_picker_number);
        m_NumberPickerFrac = (NumberPicker) v.findViewById(R.id.servings_picker_fraction);
        m_UserMealSelector = (LinearLayout) v.findViewById(R.id.usermeal_selector);
        m_AddBtn = ((AddUserMealActivity)m_Activity).getAddBtn();
        m_CancelBtn = ((AddUserMealActivity)m_Activity).getCancelBtn();

    }

    @Override
    protected void flipToNext() {
        ((AddUserMealActivity)m_Activity).showAlternativeHeader();
        m_ViewFlipper.setInAnimation(m_Activity, R.anim.slide_in_from_right);
        m_ViewFlipper.setOutAnimation(m_Activity, R.anim.slide_out_to_left);
        m_ViewFlipper.showNext();
    }

    @Override
    protected void flipToPrev() {
        ((AddUserMealActivity)m_Activity).showMainHeader();
        m_ViewFlipper.setInAnimation(m_Activity, R.anim.slide_in_from_left);
        m_ViewFlipper.setOutAnimation(m_Activity, R.anim.slide_out_to_right);
        m_ViewFlipper.showPrevious();
    }


    // --------------------------------------------------------------------------------- Listeners

    protected  void initializeListeners() {
        // Add button
        m_AddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float fraction = Constants.ServingsFracFloats.get(m_ServingsFraction);
                ParseAPI.addDiaryEntry(m_Calendar, ParseUser.getCurrentUser(), m_SelectedRecipe,
                        m_ServingsWhole + fraction, m_SelectedUserMeal);

                flipToPrev();
                Toast.makeText(m_Activity, "Added to diary!", Toast.LENGTH_SHORT).show();
            }

        });

        // Cancel Button
        m_CancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flipToPrev();
            }
        });

        initializeNutritionListeners();
    }


    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    public void onItemClick(DatabaseRecipe dbRecipe) {
        flipToNext();
        m_ServingsFraction = 0;
        m_ServingsWhole = 1;

        // Clear any current search
        if (((AddUserMealActivity)m_Activity).SEARCH_MODE)
            ((AddUserMealActivity)m_Activity).getCancelSearchBtn().callOnClick();

        // query db
        // convert to regular recipe

       // super.onItemClick(recipe);
    }

    public void resetInitialSearchView(boolean showKeyboard) {

        if (showKeyboard)
            Utils.showKeyboard(m_Activity);

        // Show search prompt text
        m_PromptTextView.setVisibility(View.VISIBLE);
        m_PromptTextView.bringToFront();
    }

    // ------------------------------------------------------------------------------------- Parse

    public RequestQueue getRequestQueue() {
        if (m_RequestQueue == null) {
            m_RequestQueue = Volley.newRequestQueue(m_Activity.getApplicationContext());
        }
        return m_RequestQueue;
    }

    private void queryDbRecipes(String searchText) {
        Utils.showProgressSpinner(m_ProgressSpinner);

        Map<String, String> params = new HashMap<>();
        params.put(Constants.Database.ParameterKeys.ApiKey, Constants.Database.ApiKey);
        params.put(Constants.Database.ParameterKeys.SearchText, searchText);

        // TODO: figure out why the fuck volley doesnt take the params
        String URL = Constants.Database.SearchBaseUrl + "?";
        URL += Constants.Database.ParameterKeys.ResponseFormat + "=" + Constants.Database.ParameterValues.JSON + "&";
        URL += Constants.Database.ParameterKeys.SearchText + "=" + searchText + "&";
        URL += Constants.Database.ParameterKeys.MaxResultCount + "=" + Constants.Database.ParameterValues.Max +"&";
        URL += Constants.Database.ParameterKeys.ResultOffset + "=" + Constants.Database.ParameterValues.Offset + "&";
        URL += Constants.Database.ParameterKeys.ApiKey + "=" + Constants.Database.ApiKey;

//        params.put(ParameterKeys.SortType, ParameterValues.Relevance);
//        params.put(ParameterKeys.MaxResultCount, ParameterValues.Max);
//        params.put(ParameterKeys.ResultOffset, ParameterValues.Offset);
//        params.put(ParameterKeys.ResponseFormat, ParameterValues.JSON);



        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        // Clear current list
                        m_DbRecipesList.clear();


                        List<JSONObject> dbRecipesList = new ArrayList<>();
                        try {
                            if (response.has("list") && response.getJSONObject("list").has("item")) {

                                // Extract all food items in list
                                JSONArray itemsJSONArray = response.getJSONObject("list").getJSONArray("item");
                                for (int i = 0; i < itemsJSONArray.length(); i++) {
                                    dbRecipesList.add((JSONObject) itemsJSONArray.get(i));
                                }


                                // Create DbRecipe objects and add to list
                                for (JSONObject item : dbRecipesList) {
                                    if (!isValidJSON(item))
                                        continue;

                                    DatabaseRecipe dbRecipe;
                                    dbRecipe = new DatabaseRecipe(item.getString("group"),
                                                item.getString("name"), item.getString("ndbno"));
                                    m_DbRecipesList.add(dbRecipe);

                                }

                            } else {
                                // TODO: no items found
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // Update
                        m_DbRecipesListAdapter.notifyDataSetChanged();
                        Utils.hideProgressSpinner(m_ProgressSpinner);

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.d("ERROR", error.getMessage());
                        Utils.hideProgressSpinner(m_ProgressSpinner);

                    }
                });
        getRequestQueue().add(jsObjRequest);
    }


    private void queryDbForNutrients(DatabaseRecipe dbRecipe) {

    }


    private boolean isValidJSON(JSONObject object) {
        return object.has("group") && object.has("name") && object.has("ndbno");

    }


    // ------------------------------------------------------------------------------------ Search

    @Override
    public void onSearchClick() {
        resetInitialSearchView(true);
    }

    @Override
    public void onCancelSearchClick() {
        resetInitialSearchView(false);
        clearSearch();
    }

    @Override
    public void onClearSearchClick() {
        clearSearch();
    }

    @Override
    public void onEnterClick() {
        m_PromptTextView.setVisibility(View.GONE);
        queryDbRecipes(m_AddUserMealActivity.getSearchText());
    }

    @Override
    public void onSearchEditTextChanged(String text, int start, int before) {
        // Only search if search button pressed
    }

    private void clearSearch() {
        // clear the list
        m_DbRecipesList.clear();
        m_DbRecipesListAdapter.notifyDataSetChanged();
    }





}
