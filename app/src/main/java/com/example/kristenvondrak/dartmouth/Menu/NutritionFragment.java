package com.example.kristenvondrak.dartmouth.Menu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.example.kristenvondrak.dartmouth.Main.Constants;
import com.example.kristenvondrak.dartmouth.Parse.Recipe;
import com.example.kristenvondrak.dartmouth.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class NutritionFragment extends MealSelectorFragment {

    protected View m_RecipeNutrientsView;
    protected TextView m_RecipeName;
    protected NumberPicker m_NumberPickerWhole;
    protected NumberPicker m_NumberPickerFrac;
    protected TextView m_AddBtn;
    protected TextView m_CancelBtn;
    protected int m_ServingsWhole = 1;
    protected int m_ServingsFraction = 0;
    protected Recipe m_SelectedRecipe;
    protected Calendar m_Calendar;
    protected Activity m_Activity;


    protected  void initializeNutritionListeners() {

        // Selector wheel with values -, 1, 2 ... 99
        final String[] numbers = new String[100];
        numbers[0] = "-";
        for (int i = 1; i < numbers.length; i++) {
            numbers[i] = String.valueOf(i);
        }
        m_NumberPickerWhole.setMinValue(0);
        m_NumberPickerWhole.setMaxValue(numbers.length - 1);
        m_NumberPickerWhole.setDisplayedValues(numbers);
        m_NumberPickerWhole.setWrapSelectorWheel(false);
        m_NumberPickerWhole.setValue(m_ServingsWhole);
        m_NumberPickerWhole.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                m_ServingsWhole = newVal;
                updateNutrients();
            }
        });

        // Selector wheel with values -, 1/8, 1/4, 1/3, 1/2, 2/3, 3/4
        m_NumberPickerFrac.setMinValue(0);
        m_NumberPickerFrac.setMaxValue(Constants.ServingsFracDisplay.length - 1);
        m_NumberPickerFrac.setDisplayedValues(Constants.ServingsFracDisplay);
        m_NumberPickerFrac.setWrapSelectorWheel(false);
        m_NumberPickerFrac.setValue(m_ServingsFraction);
        m_NumberPickerFrac.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                m_ServingsFraction = newVal;
                updateNutrients();
            }
        });

        initializeMealSelector();
    }


    public void onItemClick(Recipe recipe) {
        m_SelectedRecipe = recipe;
        m_RecipeName.setText(recipe.getName());
        resetServingsSelector();
        resetMealSelector();
        updateNutrients();
    }


    private void updateNutrients() {

        double num_servings = Constants.ServingsFracFloats.get(m_ServingsFraction) + m_ServingsWhole;

        setTextViewValue(m_RecipeNutrientsView, R.id.calories, getNewValue(m_SelectedRecipe.getCalories(), num_servings));
        setTextViewValue(m_RecipeNutrientsView, R.id.fat_calories, getNewValue(m_SelectedRecipe.getFatCalories(), num_servings));
        setTextViewValue(m_RecipeNutrientsView, R.id.total_fat, getNewValue(m_SelectedRecipe.getTotalFat(), num_servings));
        setTextViewValue(m_RecipeNutrientsView, R.id.saturated_fat, getNewValue(m_SelectedRecipe.getSaturatedFat(), num_servings));
        setTextViewValue(m_RecipeNutrientsView, R.id.cholesterol, getNewValue(m_SelectedRecipe.getCholestrol(), num_servings));
        setTextViewValue(m_RecipeNutrientsView, R.id.sodium, getNewValue(m_SelectedRecipe.getSodium(), num_servings));
        setTextViewValue(m_RecipeNutrientsView, R.id.carbs, getNewValue(m_SelectedRecipe.getTotalCarbs(), num_servings));
        setTextViewValue(m_RecipeNutrientsView, R.id.fiber, getNewValue(m_SelectedRecipe.getFiber(), num_servings));
        setTextViewValue(m_RecipeNutrientsView, R.id.sugars, getNewValue(m_SelectedRecipe.getSugars(), num_servings));
        setTextViewValue(m_RecipeNutrientsView, R.id.protein, getNewValue(m_SelectedRecipe.getProtein(), num_servings));
        //setTextViewValue(m_RecipeNutrientsView, R.id.serving_size, getNewValue(m_SelectedRecipe.getServingSize(), 1));
    }


    protected void flipToPrev() {

    }

    protected void flipToNext() {

    }


    protected void resetServingsSelector() {
        m_NumberPickerWhole.setValue(m_ServingsWhole);
        m_NumberPickerFrac.setValue(m_ServingsFraction);

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

}