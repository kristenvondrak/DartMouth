package com.example.kristenvondrak.dartmouth.Diary;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kristenvondrak.dartmouth.Main.Constants;
import com.example.kristenvondrak.dartmouth.Main.Utils;
import com.example.kristenvondrak.dartmouth.Menu.NutritionFragment;
import com.example.kristenvondrak.dartmouth.R;


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
                ((EditDiaryEntryActivity) m_Activity).showDeleteDialog();
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
        m_NumberPickerWhole = (NumberPicker) v.findViewById(R.id.servings_picker_number);
        m_NumberPickerFrac = (NumberPicker) v.findViewById(R.id.servings_picker_fraction);
        m_UserMealSelector = (LinearLayout) v.findViewById(R.id.usermeal_selector);

        // Cannot switch meal when editing diary entry
        v.findViewById(R.id.usermeal_selector).setVisibility(View.GONE);
    }

    public void update() {
        float servings = ((EditDiaryEntryActivity)m_Activity).getServingsMultiplier();
        m_ServingsWhole = (int) Math.floor(servings);
        m_ServingsFraction = Utils.getServingsFracIndex(servings - m_ServingsWhole);
        super.onItemClick(((EditDiaryEntryActivity) m_Activity).getRecipe());
    }


    public float getServingsMultiplier() {
        return m_ServingsWhole + Constants.ServingsFracFloats.get(m_ServingsFraction);
    }


}