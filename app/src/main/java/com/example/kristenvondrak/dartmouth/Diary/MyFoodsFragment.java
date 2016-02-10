package com.example.kristenvondrak.dartmouth.Diary;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.kristenvondrak.dartmouth.Main.Constants;
import com.example.kristenvondrak.dartmouth.Main.SearchHeader;
import com.example.kristenvondrak.dartmouth.Main.Utils;
import com.example.kristenvondrak.dartmouth.Menu.NutritionFragment;
import com.example.kristenvondrak.dartmouth.Parse.ParseAPI;
import com.example.kristenvondrak.dartmouth.Parse.Recipe;
import com.example.kristenvondrak.dartmouth.Parse.User;
import com.example.kristenvondrak.dartmouth.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MyFoodsFragment extends NutritionFragment implements SearchHeader{


    public enum MODE {ADD_CUSTOM, VIEW_CUSTOM};

    // Main
    private AddUserMealActivity m_AddUserMealActivity;
    private ViewFlipper m_ViewFlipper;
    private ListView m_ListView;
    private List<Recipe> m_RecipesList;
    private RecipeListAdapter m_RecipesListAdapter;
    private List<Recipe> m_RestoredList;
    private ProgressBar m_ProgressSpinner;

    // Add Custom Food
    private ScrollView m_AddCustomFoodView;
    private LinearLayout m_AddCustomFoodRow;
    private EditText m_NameEditText;
    private EditText m_FatCalsEditText;
    private EditText m_CaloriesEditText;
    private EditText m_FatEditText;
    private EditText m_SaturatedFatEditText;
    private EditText m_CholesterolEditText;
    private EditText m_SodiumEditText;
    private EditText m_CarbsEditText;
    private EditText m_FiberEditText;
    private EditText m_SugarsEditText;
    private EditText m_ProteinEditText;

    private MODE m_CurrentMode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_myfoods, container, false);

        m_Activity = getActivity();
        m_AddUserMealActivity = (AddUserMealActivity) m_Activity;
        m_Calendar = ((AddUserMealActivity)m_Activity).getCalendar();
        m_SelectedUserMeal = ((AddUserMealActivity)m_Activity).getUserMeal();

        initializeViews(v);
        initializeListeners();

        // Get recipes created by user from Parse
        m_RecipesList = new ArrayList<>();
        m_RecipesListAdapter = new RecipeListAdapter(m_Activity, this, m_RecipesList);
        m_ListView.setAdapter(m_RecipesListAdapter);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        queryUserRecipes();
    }


    // ------------------------------------------------------------------------------------- Views

    private void initializeViews(View v) {

        // Main View
        m_ListView = (ListView) v.findViewById(R.id.myfoods_listview);
        m_ViewFlipper = (ViewFlipper) v.findViewById(R.id.myfoods_viewflipper);
        m_ProgressSpinner = (ProgressBar) v.findViewById(R.id.progress_spinner);

        // Nutrition View
        m_RecipeNutrientsView = v.findViewById(R.id.nutrients);
        m_RecipeName = (TextView) v.findViewById(R.id.name);
        m_NumberPickerWhole = (NumberPicker) v.findViewById(R.id.servings_picker_number);
        m_NumberPickerFrac = (NumberPicker) v.findViewById(R.id.servings_picker_fraction);
        m_UserMealSelector = (LinearLayout) v.findViewById(R.id.usermeal_selector);
        m_AddBtn = ((AddUserMealActivity)m_Activity).getAddBtn();
        m_CancelBtn = ((AddUserMealActivity)m_Activity).getCancelBtn();

        // Add Custom Food View
        m_AddCustomFoodView = (ScrollView) v.findViewById(R.id.view_add_custom_food);
        m_AddCustomFoodRow = (LinearLayout) v.findViewById(R.id.create_new_food_row);
        m_NameEditText = (EditText) v.findViewById(R.id.name_edit_text);
        m_CaloriesEditText = (EditText) v.findViewById(R.id.calories_edit_text);
        m_FatCalsEditText = (EditText) v.findViewById(R.id.fatcals_edit_text);
        m_FatEditText = (EditText) v.findViewById(R.id.fat_edit_text);
        m_SaturatedFatEditText = (EditText) v.findViewById(R.id.saturated_fat_edit_text);
        m_CholesterolEditText = (EditText) v.findViewById(R.id.cholesterol_edit_text);
        m_SodiumEditText = (EditText) v.findViewById(R.id.sodium_edit_text);
        m_CarbsEditText = (EditText) v.findViewById(R.id.carbs_edit_text);
        m_FiberEditText = (EditText) v.findViewById(R.id.fiber_edit_text);
        m_SugarsEditText = (EditText) v.findViewById(R.id.sugars_edit_text);
        m_ProteinEditText = (EditText) v.findViewById(R.id.protein_edit_text);
    }

    @Override
    protected void flipToNext() {
        ((AddUserMealActivity)m_Activity).showAlternativeHeader();
        m_ViewFlipper.setInAnimation(m_Activity, R.anim.slide_in_from_right);
        m_ViewFlipper.setOutAnimation(m_Activity, R.anim.slide_out_to_left);

        switch (m_CurrentMode){
            case ADD_CUSTOM:
                m_AddCustomFoodView.setVisibility(View.VISIBLE);
                m_RecipeNutrientsView.setVisibility(View.GONE);
                break;

            case VIEW_CUSTOM:
                m_AddCustomFoodView.setVisibility(View.GONE);
                m_RecipeNutrientsView.setVisibility(View.VISIBLE);
                break;
        }
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

        m_AddCustomFoodRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_CurrentMode = MODE.ADD_CUSTOM;
                flipToNext();

                // Clear any current search
                if (((AddUserMealActivity)m_Activity).SEARCH_MODE)
                    ((AddUserMealActivity)m_Activity).getCancelSearchBtn().callOnClick();
            }
        });

        // Add button
        m_AddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (m_CurrentMode) {
                    case ADD_CUSTOM:
                        // Check that required fields are filled in
                        if (isEmpty(m_NameEditText)) {
                            //m_NameErrorIcon.setVisibility(View.VISIBLE);
                            //m_NameEditText.setText("");
                            break;
                        } else if (isEmpty(m_CaloriesEditText)) {

                            // Add to diary
                            break;
                        }

                        // Create custom recipe from filled in fields and save to Parse
                        final Recipe recipe = createCustomRecipe();
                        recipe.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    ParseAPI.addDiaryEntry(m_Calendar,
                                            ParseUser.getCurrentUser(), recipe, 1, m_SelectedUserMeal);

                                    // Update the list
                                    queryUserRecipes();
                                } else {
                                    Log.d("Error", e.getMessage());
                                }
                            }
                        });


                        break;
                    case VIEW_CUSTOM:
                        float fraction = Constants.ServingsFracFloats.get(m_ServingsFraction);
                        ParseAPI.addDiaryEntry(m_Calendar, ParseUser.getCurrentUser(), m_SelectedRecipe,
                                m_ServingsWhole + fraction, m_SelectedUserMeal);
                        Toast.makeText(m_Activity, "Added to diary!", Toast.LENGTH_SHORT).show();

                        break;
                }
                flipToPrev();
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
    public void onItemClick(Recipe recipe) {
        m_CurrentMode = MODE.VIEW_CUSTOM;
        flipToNext();
        m_ServingsFraction = 0;
        m_ServingsWhole = 1;

        // Clear any current search
        if (((AddUserMealActivity)m_Activity).SEARCH_MODE)
            ((AddUserMealActivity)m_Activity).getCancelSearchBtn().callOnClick();

        super.onItemClick(recipe);
    }




    // ------------------------------------------------------------------------------------- Parse

    private void queryUserRecipes() {
        Utils.showProgressSpinner(m_ProgressSpinner);
        User user = (User) ParseUser.getCurrentUser();
        ParseRelation<ParseObject> pastRecipesRelation = user.getPastRecipes();
        if (pastRecipesRelation == null)
            return;

        ParseQuery query = pastRecipesRelation.getQuery();
        query.whereEqualTo("createdBy", user);
        // TODO: order by most recent
        query.orderByAscending("updatedAt");
        query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                m_RecipesList.clear();
                if (e == null) {
                    for (ParseObject object : objects) {
                        Recipe recipe = (Recipe) object;
                        m_RecipesList.add(recipe);
                    }
                    m_RecipesListAdapter.notifyDataSetChanged();
                    if (m_AddUserMealActivity.inSearchMode()) {
                        resetSearch();
                        updateSearch(m_RecipesList, m_AddUserMealActivity.getSearchText());
                    }
                } else {
                    Log.d("ERROR:", e.getMessage());
                }
                Utils.hideProgressSpinner(m_ProgressSpinner);
            }
        });
    }

    private Recipe createCustomRecipe() {

        // Nutrients map
        HashMap<String, HashMap<String, Object>> value = new HashMap<>();

        // Get filled in fields
        HashMap<String, Object> nutrients = new HashMap<>();
        nutrients.put("calories", getText(m_CaloriesEditText));

        if (!isEmpty(m_FatCalsEditText)) {
            nutrients.put("calfat", getText(m_FatCalsEditText));
        }

        if (!isEmpty(m_FatEditText)) {
            nutrients.put("fat", getText(m_FatEditText) + "g");
        }

        if (!isEmpty(m_SaturatedFatEditText)) {
            nutrients.put("sfa", getText(m_SaturatedFatEditText) + "g");
        }

        if (!isEmpty(m_CholesterolEditText)) {
            nutrients.put("cholestrol", getText(m_CholesterolEditText) + "mg");
        }

        if (!isEmpty(m_SodiumEditText)) {
            nutrients.put("sodium", getText(m_SodiumEditText) + "mg");
        }

        if (!isEmpty(m_CarbsEditText)) {
            nutrients.put("carbs", getText(m_CarbsEditText) + "g");
        }

        if (!isEmpty(m_FiberEditText)) {
            nutrients.put("fiberdtry", getText(m_FiberEditText) + "g");
        }

        if (!isEmpty(m_SugarsEditText)) {
            nutrients.put("sugars", getText(m_SugarsEditText) + "g");
        }

        if (!isEmpty(m_ProteinEditText)) {
            nutrients.put("protein", getText(m_ProteinEditText) + "g");
        }

        value.put("result", nutrients);

        // Create new recipe
        Recipe recipe = new Recipe();
        recipe.setName(getText(m_NameEditText));
        recipe.setNutrients(value);
        recipe.setCreatedBy(ParseAPI.getCurrentParseUser());
        return recipe;
    }



    // ------------------------------------------------------------------------------------ Search

    @Override
    public void onSearchClick() {
        resetSearch();
    }

    @Override
    public void onCancelSearchClick() {
        clearSearch();
    }

    @Override
    public void onSearchEditTextChanged(String text, int start, int before) {

        // If the part of the input was deleted, search again from original list
        List<Recipe> listToSearch;
        if (start < before) {
            listToSearch = m_RestoredList;

        // Otherwise, search from the restricted list
        } else {
            listToSearch = m_RecipesList;
        }

        updateSearch(listToSearch, text);
    }


    private void resetSearch() {
        m_RestoredList = Utils.copyRecipeList(m_RecipesList);
    }

    private void clearSearch() {
        m_RestoredList.clear();
        queryUserRecipes();
    }

    @Override
    public void updateSearch(List listToSearch, String text) {
        if (text == null)
            return;

        // Fragment initialized with search already open
        if (listToSearch == null) {
            resetSearch();
            listToSearch = m_RecipesList;
        }

        // List of results that contain substring
        ArrayList<Recipe> searchResults = new ArrayList<>();
        for (Object item : listToSearch) {
            Recipe r = (Recipe) item;
            String name = r.getName().toLowerCase();
            if (name.contains(text)) {
                searchResults.add(r);
            }
        }

        // Update the list and notify adapter
        m_RecipesList.clear();
        for (Recipe r : searchResults)
            m_RecipesList.add(r);
        m_RecipesListAdapter.notifyDataSetChanged();

    }




    private boolean isEmpty(EditText editText) {
        return editText.getText() == null || editText.getText().toString().trim().equals("");
    }

    private String getText(EditText editText) {
        return editText.getText().toString();
    }




}