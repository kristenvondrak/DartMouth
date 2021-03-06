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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.kristenvondrak.dartmouth.Main.Constants;
import com.example.kristenvondrak.dartmouth.Main.Utils;
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

    public static final String TAG = "DiaryFragment";

    public static final String EXTRA_DIARY_ENTRY_ID = "EXTRA_DIARY_ENTRY_ID";
    public static final String EXTRA_USER_MEAL_ID = "EXTRA_USER_MEAL_ID";
    public static final String EXTRA_MEALTIME = "EXTRA_MEALTIME";
    public static final String EXTRA_DATE = "EXTRA_DATE";

    private Activity m_Activity;

    // Main
    private DiaryListAdapter m_DiaryListAdapter;
    private ListView m_DiaryListView;
    private ProgressBar m_ProgressSpinner;

    // Date
    private Calendar m_Calendar = Calendar.getInstance();
    private TextView m_CurrentDateTextView;
    private ImageView m_NextDateButton;
    private ImageView m_PreviousDateButton;
    private LayoutInflater m_Inflater;
    private DatePickerDialog.OnDateSetListener m_DatePickerListener;

    // Summary calorie
    private static final int DEFAULT_GOAL_CALS = 2000;
    //private TextView m_GoalTextView;
    //private TextView m_FoodTextView;
    //private TextView m_ExerciseTextView;
    //private int m_ExcerciseCals;
    //private int m_GoalCals;
    //private int m_FoodCals;

    private TextView m_BudgetTextView;
    private TextView m_FoodTextView;
    private TextView m_RemainingTextView;
    private TextView m_RemainingHeaderTextView;
    private int m_FoodCals;
    private int m_BudgetCals;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_Activity = getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView");
        m_Inflater = inflater;
        View v = m_Inflater.inflate(R.layout.fragment_diary, container, false);
        initializeViews(v);
        initializeListeners();

        // TODO: store in parse
        m_FoodCals = 0;
        m_BudgetCals = DEFAULT_GOAL_CALS;

        // Create list of meals and set the adapter
        m_DiaryListAdapter = new DiaryListAdapter(m_Activity);
        m_DiaryListView.setAdapter(m_DiaryListAdapter);

        return v;
    }

    private void update() {
        m_CurrentDateTextView.setText(Utils.getDisplayStringFromCal(m_Calendar));
        queryUserMeals(m_Calendar);
    }


    private void initializeViews(View v) {
        m_DiaryListView = (ListView) v.findViewById(R.id.diary_list_view);
        m_RemainingHeaderTextView = (TextView) v.findViewById(R.id.remaining_header);
        m_BudgetTextView = (TextView) v.findViewById(R.id.budget_cals);
        m_FoodTextView = (TextView) v.findViewById(R.id.food_cals);
        m_RemainingTextView = (TextView) v.findViewById(R.id.remaining_cals);
        m_CurrentDateTextView = (TextView) v.findViewById(R.id.date_text_view);
        m_NextDateButton = (ImageView) v.findViewById(R.id.next_date_btn);
        m_PreviousDateButton = (ImageView) v.findViewById(R.id.prev_date_btn);
        m_ProgressSpinner = (ProgressBar) v.findViewById(R.id.progress_spinner);
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

    private void updateCalorieSummary(List<UserMeal> list) {
        // Recalculate the total calories consumed
        m_FoodCals = 0;
        for (UserMeal m : list) {
            for (DiaryEntry e : m.getDiaryEntries()) {
                m_FoodCals += e.getTotalCalories();
            }
        }

        m_BudgetTextView.setText(Integer.toString(m_BudgetCals));
        m_FoodTextView.setText(Integer.toString(m_FoodCals));

        int total = m_BudgetCals - m_FoodCals;
        int color = total >= 0 ? getResources().getColor(R.color.cals_under) : getResources().getColor(R.color.cals_over);
        m_RemainingTextView.setText(Integer.toString(total));
        m_RemainingTextView.setTextColor(color);

        String header = total >= 0?  "Under" : "Over";
        m_RemainingHeaderTextView.setText(header);


    }

    @Override
    public void onResume() {
        super.onResume();
        update();
    }



    private void queryUserMeals(final Calendar cal) {
        Utils.showProgressSpinner(m_ProgressSpinner);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserMeal");
        query.whereGreaterThan("date", Utils.getDateBefore(cal));
        query.whereLessThan("date", Utils.getDateAfter(cal));
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.include("entries.recipe");
        query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.findInBackground(new FindCallback<ParseObject>() {

            public void done(List<ParseObject> meals, ParseException e) {
                List<UserMeal> userMealList = new ArrayList<UserMeal>();
                if (e == null) {
                    for (ParseObject object : meals) {
                        userMealList.add((UserMeal) object);
                    }
                } else {
                    Log.d(TAG, "Error getting user meals: " + e.getMessage());
                }
                m_DiaryListAdapter.updateData(userMealList, cal);
                updateCalorieSummary(userMealList);
                Utils.hideProgressSpinner(m_ProgressSpinner);
            }
        });
    }

}
