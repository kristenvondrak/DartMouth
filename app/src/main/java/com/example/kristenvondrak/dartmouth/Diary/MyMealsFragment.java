package com.example.kristenvondrak.dartmouth.Diary;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.kristenvondrak.dartmouth.Main.Constants;
import com.example.kristenvondrak.dartmouth.Parse.DiaryEntry;
import com.example.kristenvondrak.dartmouth.Parse.ParseAPI;
import com.example.kristenvondrak.dartmouth.Parse.Recipe;
import com.example.kristenvondrak.dartmouth.Parse.UserMeal;
import com.example.kristenvondrak.dartmouth.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;


public class MyMealsFragment extends Fragment {

    // Main View
    private ListView m_MealListView;

    // Single Meal View
    private ListView m_MealEntriesListView;
    private List<DiaryEntry> m_MealEntriesList;
    private MealEntriesListAdapter m_MealEntriesListAdapter;
    private TextView m_AddMealBtn;
    private TextView m_CancelMealBtn;
    private Spinner m_MealTimeSpinner;

    private AddUserMealActivity m_Activity;
    private ViewFlipper m_ViewFlipper;
    private List<UserMeal> m_UserMealsList;
    private MealListAdapter m_MealsListAdapter;
    private String m_SelectedUserMeal;
    private Calendar m_Calendar;

    private List<String> m_SpinnerMealsList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_mymeals, container, false);

        m_Activity = (AddUserMealActivity)getActivity();
        m_SelectedUserMeal = m_Activity.getUserMeal();
        m_Calendar = m_Activity.getCalendar();

        initializeViews(v);
        initializeListeners();

        // Get most recent recipes from Parse
        m_UserMealsList = new ArrayList<>();
        m_MealsListAdapter = new MealListAdapter(m_Activity, this);
        m_MealListView.setAdapter(m_MealsListAdapter);

        m_MealEntriesList = new ArrayList<>();
        m_MealEntriesListAdapter = new MealEntriesListAdapter(m_Activity, m_MealEntriesList);
        m_MealEntriesListView.setAdapter(m_MealEntriesListAdapter);

        queryPastMeals();
        return v;
    }

    private void initializeViews(View v) {
        m_MealListView = (ListView) v.findViewById(R.id.mymeals_listview);
        m_ViewFlipper = (ViewFlipper) v.findViewById(R.id.mymeals_viewflipper);
        m_MealEntriesListView = (ListView) v.findViewById(R.id.meal_entries_list);
        m_MealTimeSpinner = (Spinner) v.findViewById(R.id.usermeal_spinner);
        m_AddMealBtn = m_Activity.getAddBtn();
        m_CancelMealBtn = m_Activity.getCancelBtn();
    }

    private void initializeListeners() {

        m_AddMealBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<DiaryEntry> list = m_MealEntriesListAdapter.getSelectedEntries();
                for (DiaryEntry entry : list) {
                    ParseAPI.addDiaryEntry(m_Calendar, ParseUser.getCurrentUser(), entry.getRecipe(),
                            entry.getServingsMultiplier(), m_SelectedUserMeal);
                }
                flipToPrev();
                Toast.makeText(m_Activity, "Added to diary!", Toast.LENGTH_SHORT).show();
            }

        });

        m_CancelMealBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flipToPrev();
            }
        });

        // Create an ArrayAdapter using the string array
        m_SpinnerMealsList = new ArrayList<>();
        for (Constants.UserMeals m : Constants.UserMeals.values()) {
            m_SpinnerMealsList.add(m.name());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(m_Activity, R.layout.meal_spinner_item, m_SpinnerMealsList);
        adapter.setDropDownViewResource(R.layout.meal_spinner_dropdown_item);
        m_MealTimeSpinner.setSelection(m_SpinnerMealsList.indexOf(m_SelectedUserMeal));
        m_MealTimeSpinner.setAdapter(adapter);
        m_MealTimeSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

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


    private void queryPastMeals() {
        m_UserMealsList.clear();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserMeal");
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.include("entries.recipe");
        query.orderByDescending("date");
        query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (ParseObject object : objects) {
                        UserMeal meal = (UserMeal) object;
                        m_UserMealsList.add(meal);
                    }
                    m_MealsListAdapter.updateData(m_UserMealsList);
                } else {
                    Log.d("ERROR:", e.getMessage());
                    // TODO: manage error
                }
            }
        });

    }

    private void flipToNext() {
        m_ViewFlipper.setInAnimation(m_Activity, R.anim.slide_in_from_right);
        m_ViewFlipper.setOutAnimation(m_Activity, R.anim.slide_out_to_left);
        m_ViewFlipper.showNext();

        m_Activity.showAlternativeHeader();
    }

    private void flipToPrev() {
        m_ViewFlipper.setInAnimation(m_Activity, R.anim.slide_in_from_left);
        m_ViewFlipper.setOutAnimation(m_Activity, R.anim.slide_out_to_right);
        m_ViewFlipper.showPrevious();

        m_Activity.showMainHeader();
    }

    public void onMealClick(UserMeal meal) {
        m_MealEntriesList.clear();
        for (DiaryEntry entry : meal.getDiaryEntries()) {
            m_MealEntriesList.add(entry);
        }
        m_MealEntriesListAdapter.resetData();
        m_SelectedUserMeal = meal.getTitle();
        resetMealSpinner();
        flipToNext();
    }

    public void resetMealSpinner() {
        m_MealTimeSpinner.setSelection(m_SpinnerMealsList.indexOf(m_SelectedUserMeal));
    }

}