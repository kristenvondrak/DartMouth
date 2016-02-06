package com.example.kristenvondrak.dartmouth.Diary;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.kristenvondrak.dartmouth.Menu.MealSelectorFragment;
import com.example.kristenvondrak.dartmouth.Parse.DiaryEntry;
import com.example.kristenvondrak.dartmouth.Parse.ParseAPI;
import com.example.kristenvondrak.dartmouth.Parse.Recipe;
import com.example.kristenvondrak.dartmouth.Parse.UserMeal;
import com.example.kristenvondrak.dartmouth.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class MyMealsFragment extends MealSelectorFragment {

    // Main View
    private ListView m_MealListView;

    // Single Meal View
    private ListView m_MealEntriesListView;
    private List<DiaryEntry> m_MealEntriesList;
    private MealEntriesListAdapter m_MealEntriesListAdapter;
    private TextView m_AddMealBtn;
    private TextView m_CancelMealBtn;
    private AddUserMealActivity m_Activity;
    private ViewFlipper m_ViewFlipper;
    private List<UserMeal> m_UserMealsList;
    private MealListAdapter m_MealsListAdapter;
    private Calendar m_Calendar;


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
        m_UserMealSelector = (LinearLayout) v.findViewById(R.id.usermeal_selector);
        m_AddMealBtn = m_Activity.getAddBtn();
        m_CancelMealBtn = m_Activity.getCancelBtn();
    }

    private void initializeListeners() {

        m_AddMealBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<DiaryEntry> list = m_MealEntriesListAdapter.getSelectedEntries();
                ParseAPI.addDiaryEntries(m_Calendar, ParseUser.getCurrentUser(), list, m_SelectedUserMeal);
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

        initializeMealSelector();
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
        m_Activity.showAlternativeHeader();
        m_ViewFlipper.setInAnimation(m_Activity, R.anim.slide_in_from_right);
        m_ViewFlipper.setOutAnimation(m_Activity, R.anim.slide_out_to_left);
        m_ViewFlipper.showNext();
    }

    private void flipToPrev() {
        m_Activity.showMainHeader();
        m_ViewFlipper.setInAnimation(m_Activity, R.anim.slide_in_from_left);
        m_ViewFlipper.setOutAnimation(m_Activity, R.anim.slide_out_to_right);
        m_ViewFlipper.showPrevious();
    }

    public void onMealClick(UserMeal meal) {
        m_MealEntriesList.clear();
        for (DiaryEntry entry : meal.getDiaryEntries()) {
            m_MealEntriesList.add(entry);
        }
        m_MealEntriesListAdapter.resetData();
        m_SelectedUserMeal = meal.getTitle();
        resetMealSelector();
        flipToNext();
    }



}