package com.example.kristenvondrak.dartmouth.Menu;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.kristenvondrak.dartmouth.Main.Constants;
import com.example.kristenvondrak.dartmouth.Parse.Recipe;
import com.example.kristenvondrak.dartmouth.Parse.UserMeal;
import com.example.kristenvondrak.dartmouth.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class NutritionFragment extends Fragment {

    protected View m_RecipeNutrientsView;
    protected TextView m_RecipeName;
    protected NumberPicker m_RecipeNumberPickerWhole;
    protected NumberPicker m_RecipeNumberPickerFrac;
    protected Spinner m_UserMealSpinner;
    protected TextView m_RecipeAddBtn;
    protected TextView m_RecipeCancelBtn;
    protected int m_ServingsWhole = 1;
    protected int m_ServingsFraction = 0;
    protected String m_SelectedUserMeal = Constants.UserMeals.Breakfast.name();
    protected Recipe m_SelectedRecipe;
    protected Calendar m_Calendar;
    protected Activity m_Activity;
    protected List<String> m_SpinnerMealsList;


    protected  void initializeNutritionListeners() {

        // Selector wheel with values -, 1, 2 ... 99
        final String[] numbers = new String[100];
        numbers[0] = "-";
        for (int i = 1; i < numbers.length; i++) {
            numbers[i] = String.valueOf(i);
        }
        m_RecipeNumberPickerWhole.setMinValue(0);
        m_RecipeNumberPickerWhole.setMaxValue(numbers.length - 1);
        m_RecipeNumberPickerWhole.setDisplayedValues(numbers);
        m_RecipeNumberPickerWhole.setWrapSelectorWheel(false);
        m_RecipeNumberPickerWhole.setValue(m_ServingsWhole);
        Log.d("*****", "whole value " + Integer.toString(m_ServingsWhole));
        m_RecipeNumberPickerWhole.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                m_ServingsWhole = newVal;
                updateNutrients();
            }
        });

        // Selector wheel with values -, 1/8, 1/4, 1/3, 1/2, 2/3, 3/4
        m_RecipeNumberPickerFrac.setMinValue(0);
        m_RecipeNumberPickerFrac.setMaxValue(Constants.ServingsFracDisplay.length - 1);
        m_RecipeNumberPickerFrac.setDisplayedValues(Constants.ServingsFracDisplay);
        m_RecipeNumberPickerFrac.setWrapSelectorWheel(false);
        m_RecipeNumberPickerFrac.setValue(m_ServingsFraction);
        Log.d("*****", "frac value " + Integer.toString(m_ServingsFraction));
        m_RecipeNumberPickerFrac.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                m_ServingsFraction = newVal;
                updateNutrients();
            }
        });


        // UserMeal selector --Create an ArrayAdapter using the string array
        m_SpinnerMealsList = new ArrayList<>();
        for (Constants.UserMeals m : Constants.UserMeals.values()) {
            m_SpinnerMealsList.add(m.name());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(m_Activity, R.layout.meal_spinner_item, m_SpinnerMealsList);
        adapter.setDropDownViewResource(R.layout.meal_spinner_dropdown_item);
        m_UserMealSpinner.setSelection(m_SpinnerMealsList.indexOf(m_SelectedUserMeal));
        Log.d("*****", "user meal " + m_SelectedUserMeal);
        m_UserMealSpinner.setAdapter(adapter);
        m_UserMealSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                m_SelectedUserMeal = m_SpinnerMealsList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Nothing??
            }
        });

    }


    public void onItemClick(Recipe recipe) {
        m_SelectedRecipe = recipe;
        m_RecipeName.setText(recipe.getName());

        m_ServingsWhole = 1;
        m_RecipeNumberPickerWhole.setValue(1);

        m_ServingsFraction = 0;
        m_RecipeNumberPickerFrac.setValue(0);

        updateNutrients();
    }


    private void setTextViewValue(View v, int id, String text) {
        if (text == null)
            return;

        ((TextView)v.findViewById(id)).setText(text);
    }

    private String getNewValue(String value, double multiplier) {
        int v = (int)(Nutrients.convertToDouble(value) * multiplier);
        if (v < 0 || value == null)
            return "-";

        String u = Nutrients.getUnits(value);


        return Integer.toString(v) + " " +  u;
    }


    private void updateNutrients() {

        double num_servings = Constants.ServingsFracFloats.get(m_ServingsFraction) + m_ServingsWhole;

        setTextViewValue(m_RecipeNutrientsView, R.id.calories, getNewValue(m_SelectedRecipe.getCalories(), num_servings));
        setTextViewValue(m_RecipeNutrientsView, R.id.fat_calories,  getNewValue(m_SelectedRecipe.getFatCalories(), num_servings));
        setTextViewValue(m_RecipeNutrientsView, R.id.total_fat,  getNewValue(m_SelectedRecipe.getTotalFat(), num_servings));
        setTextViewValue(m_RecipeNutrientsView, R.id.saturated_fat,  getNewValue(m_SelectedRecipe.getSaturatedFat(), num_servings));
        setTextViewValue(m_RecipeNutrientsView, R.id.cholesterol,  getNewValue(m_SelectedRecipe.getCholestrol(), num_servings));
        setTextViewValue(m_RecipeNutrientsView, R.id.sodium, getNewValue(m_SelectedRecipe.getSodium(), num_servings));
        setTextViewValue(m_RecipeNutrientsView, R.id.carbs,  getNewValue(m_SelectedRecipe.getTotalCarbs(), num_servings));
        setTextViewValue(m_RecipeNutrientsView, R.id.fiber,  getNewValue(m_SelectedRecipe.getFiber(), num_servings));
        setTextViewValue(m_RecipeNutrientsView, R.id.sugars, getNewValue(m_SelectedRecipe.getSugars(), num_servings));
        setTextViewValue(m_RecipeNutrientsView, R.id.protein, getNewValue(m_SelectedRecipe.getProtein(), num_servings));
        //setTextViewValue(m_RecipeNutrientsView, R.id.serving_size, getNewValue(m_SelectedRecipe.getServingSize(), 1));
    }

    private double parseFraction(String string) {
        String[] parts = string.split("/");
        return Double.parseDouble(parts[0]) / Double.parseDouble(parts[1]);
    }

    protected void flipToPrev() {

    }

    protected void flipToNext() {

    }

    protected void resetMealSpinner() {
        m_UserMealSpinner.setSelection(m_SpinnerMealsList.indexOf(m_SelectedUserMeal));
    }

    protected void resetServingsSelector() {
        m_RecipeNumberPickerWhole.setValue(m_ServingsWhole);
        m_RecipeNumberPickerFrac.setValue(m_ServingsFraction);

    }



}