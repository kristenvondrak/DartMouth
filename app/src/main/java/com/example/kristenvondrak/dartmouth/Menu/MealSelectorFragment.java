package com.example.kristenvondrak.dartmouth.Menu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.kristenvondrak.dartmouth.Main.Constants;
import com.example.kristenvondrak.dartmouth.Parse.Recipe;
import com.example.kristenvondrak.dartmouth.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class MealSelectorFragment extends Fragment {


    protected LinearLayout m_UserMealSelector;
    protected String m_SelectedUserMeal = Constants.UserMeals.Breakfast.name();
    protected List<String> m_SelectorMealsList;


    protected  void initializeMealSelector() {

        // Selector Dialog with values Breakfast, Lunch, Dinner, Snacks
        m_SelectorMealsList = new ArrayList<>();
        for (Constants.UserMeals m : Constants.UserMeals.values()) {
            m_SelectorMealsList.add(m.name());
        }
        m_UserMealSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMealSelectorDialog();
            }
        });

    }

    protected void resetMealSelector() {
        ((TextView)m_UserMealSelector.findViewById(R.id.usermeal_selector_text)).setText(m_SelectedUserMeal);
    }

    public void showMealSelectorDialog() {
        /*
        CharSequence[] choices = m_SelectorMealsList.toArray(new CharSequence[m_SelectorMealsList.size()]);
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.DialogStyle));
        builder.setTitle("Add to Meal")
                .setSingleChoiceItems(choices, m_SelectorMealsList.indexOf(m_SelectedUserMeal), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        m_SelectedUserMeal = m_SelectorMealsList.get(which);
                        resetMealSelector();
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();*/

        LayoutInflater inflater = getActivity().getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View v = inflater.inflate(R.layout.meal_selector_dialog, null);
        RadioGroup rg = (RadioGroup) v.findViewById(R.id.radio_group);

        for (int i = 0; i < m_SelectorMealsList.size(); i++) {
            RadioButton button = (RadioButton) inflater.inflate(R.layout.meal_selector_radiobutton, null);
            button.setText(m_SelectorMealsList.get(i));
            button.setId(i);
            rg.addView(button);
            if (m_SelectorMealsList.get(i).equals(m_SelectedUserMeal))
                button.setChecked(true);
        }
        builder.setView(v);
        final AlertDialog dialog = builder.create();

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                m_SelectedUserMeal = m_SelectorMealsList.get(checkedId);
                resetMealSelector();
                dialog.dismiss();
            }
        });
        dialog.show();

    }
}