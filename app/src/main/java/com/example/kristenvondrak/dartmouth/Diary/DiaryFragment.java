package com.example.kristenvondrak.dartmouth.Diary;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.kristenvondrak.dartmouth.Main.Constants;
import com.example.kristenvondrak.dartmouth.Parse.DiaryEntry;
import com.example.kristenvondrak.dartmouth.Parse.UserMeal;
import com.example.kristenvondrak.dartmouth.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class DiaryFragment extends Fragment {

    public static final String EXTRA_DIARY_ENTRY_ID = "EXTRA_DIARY_ENTRY_ID";
    public static final String EXTRA_MEALTIME = "EXTRA_MEALTIME";
    public static final String EXTRA_DATE = "EXTRA_DATE";

    private Activity m_Activity;

    // Meals
    private List<UserMeal> m_UserMealsList;
    private DiaryListAdapter m_DiaryListAdapter;
    private ListView m_DiaryListView;
    private ImageView m_AddToMealBtn;

    // Date
    private Calendar m_Calendar;
    private TextView m_CurrentDateTextView;
    private ImageView m_NextDateButton;
    private ImageView m_PreviousDateButton;
    private LayoutInflater m_Inflater;
    private DatePickerDialog.OnDateSetListener m_DatePickerListener;

    // Summary calorie
    private static final int DEFAULT_GOAL_CALS = 2000;
    private TextView m_GoalTextView;
    private TextView m_FoodTextView;
    private TextView m_ExerciseTextView;
    private TextView m_RemainingTextView;
    private int m_ExcerciseCals;
    private int m_GoalCals;
    private int m_FoodCals;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_Activity = getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        m_Inflater = inflater;
        View v = m_Inflater.inflate(R.layout.fragment_diary, container, false);
        initializeViews(v);
        initializeListeners();

        m_Calendar = Calendar.getInstance();
        // TODO: store in parse
        m_ExcerciseCals = 0;
        m_FoodCals = 0;
        m_GoalCals = DEFAULT_GOAL_CALS;

        // Create list of meals and set the adapter
        m_UserMealsList = new ArrayList<>();
        m_DiaryListAdapter = new DiaryListAdapter(m_Activity);
        m_DiaryListView.setAdapter(m_DiaryListAdapter);

        update();
        return v;
    }


    private void initializeViews(View v) {
        m_DiaryListView = (ListView) v.findViewById(R.id.diary_list_view);
        m_AddToMealBtn = (ImageView) v.findViewById(R.id.add_to_meal_btn);
        m_GoalTextView = (TextView) v.findViewById(R.id.total_goal_cals);
        m_FoodTextView = (TextView) v.findViewById(R.id.total_food_cals);
        m_ExerciseTextView = (TextView) v.findViewById(R.id.total_exercise_cals);
        m_RemainingTextView = (TextView) v.findViewById(R.id.total_remaining_cals);
        m_CurrentDateTextView = (TextView) v.findViewById(R.id.date_text_view);
        m_NextDateButton = (ImageView) v.findViewById(R.id.next_date_btn);
        m_PreviousDateButton = (ImageView) v.findViewById(R.id.prev_date_btn);
    }


    private void initializeListeners() {

        m_DatePickerListener = new DatePickerDialog.OnDateSetListener() {

            // when dialog box is closed, below method will be called.
            public void onDateSet(DatePicker view, int selectedYear,
                                  int selectedMonth, int selectedDay) {
                m_Calendar.set(Calendar.YEAR, selectedYear);
                m_Calendar.set(Calendar.MONTH, selectedMonth);
                m_Calendar.set(Calendar.DAY_OF_MONTH, selectedDay);
                update();
            }
        };


        m_CurrentDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(m_Activity, m_DatePickerListener, m_Calendar.get(Calendar.YEAR),
                        m_Calendar.get(Calendar.MONTH), m_Calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        m_NextDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_Calendar.add(Calendar.DATE, 1);
                update();
            }
        });

        m_PreviousDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_Calendar.add(Calendar.DATE, -1);
                update();
            }
        });

    }

    private void updateCalorieSummary() {
        // Recalculate the total calories consumed
        m_FoodCals = 0;
        for (UserMeal m : m_UserMealsList) {
            for (DiaryEntry e : m.getDiaryEntries()) {
                m_FoodCals += e.getTotalCalories();
            }
        }
        m_ExerciseTextView.setText(Integer.toString(m_ExcerciseCals));
        m_GoalTextView.setText(Integer.toString(m_GoalCals));
        m_FoodTextView.setText(Integer.toString(m_FoodCals));
        m_RemainingTextView.setText(Integer.toString(m_GoalCals - m_FoodCals + m_ExcerciseCals));
    }

    private void update() {
        m_CurrentDateTextView.setText(Constants.getDisplayStringFromCal(m_Calendar));
        queryUserMeals(m_Calendar);
    }


    @Override
    public void onResume() {
        super.onResume();
        update();
    }



    private void queryUserMeals(Calendar cal) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserMeal");
        query.whereGreaterThan("date", Constants.getDateBefore(cal));
        query.whereLessThan("date", Constants.getDateAfter(cal));
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.include("entries.recipe");
        query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.findInBackground(new FindCallback<ParseObject>() {

            public void done(List<ParseObject> meals, ParseException e) {
                m_UserMealsList.clear();
                if (e == null) {
                    for (ParseObject object : meals) {
                        m_UserMealsList.add((UserMeal) object);
                    }
                } else {
                    Log.d("DiaryFragment", "Error getting user meals: " + e.getMessage());

                }
                m_DiaryListAdapter.updateData(m_UserMealsList, m_Calendar);
                updateCalorieSummary();
            }
        });
    }

}
