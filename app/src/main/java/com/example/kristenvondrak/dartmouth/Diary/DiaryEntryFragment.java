package com.example.kristenvondrak.dartmouth.Diary;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.kristenvondrak.dartmouth.Menu.NutritionFragment;
import com.example.kristenvondrak.dartmouth.Parse.DiaryEntry;
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

import java.util.ArrayList;
import java.util.List;


public class DiaryEntryFragment extends NutritionFragment {

    private LayoutInflater m_Inflater;
    private ImageView m_DeleteBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        m_Inflater = inflater;
        m_Activity = getActivity();
        View v =  m_Inflater.inflate(R.layout.nutrition, container, false);

        initializeViews(v);
        initializeListeners();

        update();
        return v;
    }

    private void initializeListeners() {
        m_DeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(m_Activity, "Deleted from diary!", Toast.LENGTH_SHORT).show();
                ((EditDiaryEntryActivity)m_Activity).deleteDiaryEntry();
            }

        });
        initializeNutritionListeners();
    }

    private void initializeViews(View v) {
        // Add delete button
        m_DeleteBtn = (ImageView) v.findViewById(R.id.delete_btn);
        m_DeleteBtn.setVisibility(View.VISIBLE);

        // Nutrition Info
        m_RecipeNutrientsView = v.findViewById(R.id.nutrients);
        m_RecipeName = (TextView) v.findViewById(R.id.name);
        m_RecipeNumberPickerWhole = (NumberPicker) v.findViewById(R.id.servings_picker_number);
        m_RecipeNumberPickerFrac = (NumberPicker) v.findViewById(R.id.servings_picker_fraction);
        m_UserMealSpinner = (Spinner) v.findViewById(R.id.usermeal_spinner);

        // Cannot switch meal when editing diary entry
        v.findViewById(R.id.usermeal_selector).setVisibility(View.GONE);
    }

    public void update() {
        super.onItemClick(((EditDiaryEntryActivity) m_Activity).getRecipe());
    }


    public float getServingsMultiplier() {
        return (float) (m_ServingsWhole + m_ServingsFraction);
    }
}