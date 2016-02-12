package com.example.kristenvondrak.dartmouth.Database;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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
import com.example.kristenvondrak.dartmouth.Main.Constants.Database;
import com.example.kristenvondrak.dartmouth.Main.Constants.Database.ReportNutrients;
import com.example.kristenvondrak.dartmouth.Main.SearchHeader;
import com.example.kristenvondrak.dartmouth.Main.Utils;
import com.example.kristenvondrak.dartmouth.Menu.NutritionFragment;
import com.example.kristenvondrak.dartmouth.Parse.ParseAPI;
import com.example.kristenvondrak.dartmouth.Parse.Recipe;
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
    private View m_EmptyMessage;
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
        m_EmptyMessage = v.findViewById(R.id.empty_food_list);

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
                m_SelectedRecipe.saveInBackground();
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
        queryDbRecipeNutrients(dbRecipe);
    }

    private void showRecipeNutrients(Recipe recipe) {
        flipToNext();
        m_ServingsFraction = 0;
        m_ServingsWhole = 1;

        // Clear any current search
        if (((AddUserMealActivity)m_Activity).SEARCH_MODE)
            ((AddUserMealActivity)m_Activity).getCancelSearchBtn().callOnClick();

        super.onItemClick(recipe);
    }

    public void resetInitialSearchView(boolean showKeyboard) {

        // Show keyboard if no search in progress
        if (showKeyboard) {
            EditText editText = m_AddUserMealActivity.getSearchEditText();
            editText.requestFocus();
            InputMethodManager imm = (InputMethodManager) m_Activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            m_Activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        }

        // Show search prompt text
        m_PromptTextView.setVisibility(View.VISIBLE);
        m_EmptyMessage.setVisibility(View.GONE);
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
        m_PromptTextView.setVisibility(View.GONE);
        m_EmptyMessage.setVisibility(View.GONE);
        Utils.showProgressSpinner(m_ProgressSpinner);

        searchText = convertTextToDbQuery(searchText);

        Map<String, String> params = new HashMap<>();
        params.put(Database.ParameterKeys.ApiKey, Database.ApiKey);
        params.put(Database.ParameterKeys.SearchText, searchText);
        params.put(Database.ParameterKeys.SortType, Database.ParameterValues.Relevance);
        params.put(Database.ParameterKeys.MaxResultCount, Database.ParameterValues.Max);
        params.put(Database.ParameterKeys.ResultOffset, Database.ParameterValues.Offset);
        params.put(Database.ParameterKeys.ResponseFormat, Database.ParameterValues.JSON);

        // TODO: figure out why the fuck volley doesnt take the params
        String url = Database.SearchBaseUrl + getQueryParams(searchText);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

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
                                queryDbRecipesFail("No items found.");
                            }

                        } catch (JSONException e) {
                            queryDbRecipes("No items found.");
                            e.printStackTrace();

                        }

                        // Update
                        m_DbRecipesListAdapter.notifyDataSetChanged();
                        Utils.hideProgressSpinner(m_ProgressSpinner);

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        queryDbRecipesFail("Unknown error.");
                        Utils.hideProgressSpinner(m_ProgressSpinner);

                    }
                });
        getRequestQueue().add(jsObjRequest);
    }


    private void queryDbRecipeNutrients(final DatabaseRecipe dbRecipe) {

        String url = Database.ReportsBaseUrl + getQueryNutrientsParams(dbRecipe);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {


                        List<JSONObject> dbRecipesList = new ArrayList<>();
                        try {
                            if (response.has("report") && response.getJSONObject("report").has("food")
                                    && response.getJSONObject("report").getJSONObject("food").has("nutrients")){

                                // Create parse recipe
                                JSONArray nutrientsJSON = response.getJSONObject("report")
                                        .getJSONObject("food").getJSONArray("nutrients");

                                Recipe recipe = new Recipe();
                                recipe.setName(dbRecipe.getName());
                                recipe.setCategory(dbRecipe.getGroup());
                                recipe.setCreatedBy(ParseUser.getCurrentUser());
                                recipe.setNutrients(extractNutrients(nutrientsJSON));
                                //TODO: do we want to save this recipe in parse??
                                //recipe.saveInBackground();
                                showRecipeNutrients(recipe);

                            } else {
                                queryDbRecipesFail("No items found.");
                            }

                        } catch (JSONException e) {
                            queryDbRecipes("No items found.");
                            e.printStackTrace();

                        }

                        // Update
                        m_DbRecipesListAdapter.notifyDataSetChanged();
                        Utils.hideProgressSpinner(m_ProgressSpinner);

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        queryDbRecipesFail("Unknown error.");
                        Utils.hideProgressSpinner(m_ProgressSpinner);

                    }
                });

        getRequestQueue().add(jsObjRequest);

    }

    private HashMap<String, HashMap<String, Object>> extractNutrients(JSONArray nutrientsJSON) {
        HashMap<String, Object> nutrients = new HashMap<>();

        // Add all the nutrient values
        for (int i = 0; i < nutrientsJSON.length(); i ++) {
            String name = null;
            JSONObject item = null;
            try {
                item = nutrientsJSON.getJSONObject(i);
                name = item.getString("name");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (name == null)
                continue;

            String value;
            switch (name) {

                case Constants.Database.ReportNutrients.Calories:
                    value = getNutrientValue(item, name);
                    if (value != null)
                        nutrients.put(Recipe.Fields.Calories, value);
                    break;

                case ReportNutrients.TotalFat:
                    value = getNutrientValue(item, name);
                    if (value != null)
                        nutrients.put(Recipe.Fields.TotalFat, value);
                    break;

                case ReportNutrients.SaturatedFat:
                    value = getNutrientValue(item, name);
                    if (value != null)
                        nutrients.put(Recipe.Fields.SaturatedFat, value);
                    break;

                case ReportNutrients.Cholesterol:
                    value = getNutrientValue(item, name);
                    if (value != null)
                        nutrients.put(Recipe.Fields.Cholesterol, value);
                    break;

                case ReportNutrients.Sodium:
                    value = getNutrientValue(item, name);
                    if (value != null)
                        nutrients.put(Recipe.Fields.Sodium, value);
                    break;

                case ReportNutrients.TotalCarbs:
                    value = getNutrientValue(item, name);
                    if (value != null)
                        nutrients.put(Recipe.Fields.TotalCarbs, value);
                    break;

                case ReportNutrients.Fiber:
                    value = getNutrientValue(item, name);
                    if (value != null)
                        nutrients.put(Recipe.Fields.Fiber, value);
                    break;

                case ReportNutrients.Sugar:
                    value = getNutrientValue(item, name);
                    if (value != null)
                        nutrients.put(Recipe.Fields.Sugars, value);
                    break;

                case ReportNutrients.Protein:
                    value = getNutrientValue(item, name);
                    if (value != null)
                        nutrients.put(Recipe.Fields.Protein, value);
                    break;

                default:
                    break;
            }
        }

        // Add the serving size and text
        try {
            JSONObject first = nutrientsJSON.getJSONObject(0);
            JSONArray measures = first.getJSONArray("measures");
            JSONObject item = measures.getJSONObject(0);
            int qty = item.getInt("qty");
            String label = item.getString("label");
            int grams = item.getInt("eqv");

            String servingsText = Integer.toString(qty) + " " + label;
            nutrients.put(Recipe.Fields.ServingSizeText, servingsText);
            nutrients.put(Recipe.Fields.ServingSizeGrams, grams);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        HashMap<String, HashMap<String, Object>> map = new HashMap<>();
        map.put(Recipe.Fields.NutrientResults, nutrients);
        return map;
    }

    private String getNutrientValue(JSONObject item, String name) {
        try {
            return item.getString("value");
        } catch (JSONException e) {
            return null;
        }
    }

    private void queryDbRecipesFail(String message) {
        m_EmptyMessage.setVisibility(View.VISIBLE);
        m_EmptyMessage.bringToFront();
    }

    private String convertTextToDbQuery(String text) {
        return  text.replace(" ", "+");
    }


    private String getQueryParams(String searchText) {
        String params = "?";
        params += Database.ParameterKeys.ResponseFormat + "=" + Database.ParameterValues.JSON + "&";
        params += Database.ParameterKeys.SearchText + "=" + searchText + "&";
        params += Database.ParameterKeys.MaxResultCount + "=" + Database.ParameterValues.Max +"&";
        params += Database.ParameterKeys.ResultOffset + "=" + Database.ParameterValues.Offset + "&";
        params += Database.ParameterKeys.ApiKey + "=" + Database.ApiKey;
        return params;
    }

    private String getQueryNutrientsParams(DatabaseRecipe dbRecipe) {
        String params = "?";
        params += Database.ParameterKeys.ApiKey + "=" + Database.ApiKey + "&";
        params += Database.ParameterKeys.NDBNumber + "=" + dbRecipe.getNDBNo() + "&";
        params += Database.ParameterKeys.ReportType + "=" + Database.ParameterValues.ReportBasic + "&";
        params += Database.ParameterKeys.ResponseFormat + "=" + Database.ParameterValues.JSON;
        return params;
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
