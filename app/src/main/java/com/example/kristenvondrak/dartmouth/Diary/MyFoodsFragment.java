package com.example.kristenvondrak.dartmouth.Diary;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.kristenvondrak.dartmouth.Main.Constants;
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


public class MyFoodsFragment extends NutritionFragment {

    public enum MODE {ADD_CUSTOM, VIEW_CUSTOM};

    // Main
    private ViewFlipper m_ViewFlipper;
    private ListView m_ListView;
    private List<Recipe> m_RecipesList;
    private RecipeListAdapter m_RecipesListAdapter;


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
        m_Calendar = ((AddUserMealActivity)m_Activity).getCalendar();
        m_SelectedUserMeal = ((AddUserMealActivity)m_Activity).getUserMeal();

        Log.d("*******", "MyFoods cal = " + m_Calendar.toString());
        initializeViews(v);
        initializeListeners();

        // Get recipes created by user from Parse
        m_RecipesList = new ArrayList<>();
        m_RecipesListAdapter = new RecipeListAdapter(m_Activity, this, m_RecipesList);
        m_ListView.setAdapter(m_RecipesListAdapter);

        queryUserRecipes();
        return v;
    }

    private void initializeViews(View v) {

        // Main View
        m_ListView = (ListView) v.findViewById(R.id.myfoods_listview);
        m_ViewFlipper = (ViewFlipper) v.findViewById(R.id.myfoods_viewflipper);

        // Nutrition View
        m_RecipeNutrientsView = v.findViewById(R.id.nutrients);
        m_RecipeName = (TextView) v.findViewById(R.id.name);
        m_RecipeNumberPickerWhole = (NumberPicker) v.findViewById(R.id.servings_picker_number);
        m_RecipeNumberPickerFrac = (NumberPicker) v.findViewById(R.id.servings_picker_fraction);
        m_UserMealSpinner = (Spinner) v.findViewById(R.id.usermeal_spinner);
        m_RecipeAddBtn = ((AddUserMealActivity)m_Activity).getAddBtn();
        m_RecipeCancelBtn = ((AddUserMealActivity)m_Activity).getCancelBtn();

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



    protected  void initializeListeners() {

        m_AddCustomFoodRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_CurrentMode = MODE.ADD_CUSTOM;
                flipToNext();
            }
        });

        // Add button
        m_RecipeAddBtn.setOnClickListener(new View.OnClickListener() {
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
        m_RecipeCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flipToPrev();
            }
        });

        initializeNutritionListeners();
    }


    public void onItemClick(Recipe recipe) {
        m_CurrentMode = MODE.VIEW_CUSTOM;
        resetServingsSelector();
        flipToNext();
        super.onItemClick(recipe);
    }


    @Override
    protected void flipToNext() {
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
        ((AddUserMealActivity)m_Activity).showAlternativeHeader();
    }

    @Override
    protected void flipToPrev() {
        m_ViewFlipper.setInAnimation(m_Activity, R.anim.slide_in_from_left);
        m_ViewFlipper.setOutAnimation(m_Activity, R.anim.slide_out_to_right);
        m_ViewFlipper.showPrevious();
        ((AddUserMealActivity)m_Activity).showMainHeader();
    }

    private void queryUserRecipes() {
        User user = (User) ParseUser.getCurrentUser();
        ParseRelation<ParseObject> pastRecipesRelation = user.getPastRecipes();
        if (pastRecipesRelation == null)
            return;

        ParseQuery query = pastRecipesRelation.getQuery();
        query.whereEqualTo("createdBy", user);
        // TODO: order by most recent
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
                } else {
                    Log.d("ERROR:", e.getMessage());
                    // TODO: manage error
                }
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


    private boolean isEmpty(EditText editText) {
        return editText.getText() == null || editText.getText().toString().trim().equals("");
    }

    private String getText(EditText editText) {
        Log.d("*********", editText.getText().toString());
        return editText.getText().toString();
    }

}