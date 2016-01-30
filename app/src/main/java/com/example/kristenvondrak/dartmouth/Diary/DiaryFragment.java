package com.example.kristenvondrak.dartmouth.Diary;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kristenvondrak.dartmouth.Main.Constants;
import com.example.kristenvondrak.dartmouth.Main.MainActivity;
import com.example.kristenvondrak.dartmouth.Menu.MenuItemListAdapter;
import com.example.kristenvondrak.dartmouth.Parse.DiaryEntry;
import com.example.kristenvondrak.dartmouth.Parse.Recipe;
import com.example.kristenvondrak.dartmouth.Parse.UserMeal;
import com.example.kristenvondrak.dartmouth.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class DiaryFragment extends Fragment {

    private Activity m_Activity;
    public static final String EXTRA_MEALTIME = "EXTRA_MEALTIME";
    public static final String EXTRA_DATE = "EXTRA_DATE";

    // Meals
    private List<UserMeal> m_UserMealsList;
    private DiaryListAdapter m_DiaryListAdapter;
    private ListView m_DiaryListView;
    private ImageView m_AddToMealBtn;

    // Date
    private Calendar m_CurrentDate;
    private TextView m_CurrentDateTextView;
    private ImageView m_NextDateButton;
    private ImageView m_PreviousDateButton;
    private LayoutInflater m_Inflater;

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

        m_CurrentDate = Calendar.getInstance();
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

        m_NextDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_CurrentDate.add(Calendar.DATE, 1);
                update();
            }
        });

        m_PreviousDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_CurrentDate.add(Calendar.DATE, -1);
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
        m_CurrentDateTextView.setText(Constants.getStringFromCal(m_CurrentDate));
        queryUserMeals(m_CurrentDate);
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
                m_DiaryListAdapter.updateData(m_UserMealsList, m_CurrentDate);
                updateCalorieSummary();
            }
        });
    }

/*

m_AddToMealBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                Constants.UserMeals[] meals = Constants.UserMeals.values();
                final String[][] choices = {new String[meals.length]};
                for (int i = 0; i < meals.length; i++) {
                    choices[0][i] = meals[i].name();
                }

                final int[] selected = {0};
                // Set the dialog title
                builder.setTitle("Pick your meal time")
                        // Specify the list array, the items to be selected by default (null for none),
                        // and the listener through which to receive callbacks when items are selected
                        .setSingleChoiceItems(choices[0], selected[0],
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        selected[0] = which;
                                    }

                                })

                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                Intent intent = new Intent(m_Activity, AddUserMealActivity.class);
                                intent.putExtra(EXTRA_MEALTIME, choices[0][selected[0]]);
                                intent.putExtra(EXTRA_DATE, Constants.getStringFromCal(m_CurrentDate));
                                m_Activity.startActivityForResult(intent, MainActivity.DIARY_ACTIVITY_REQUEST);
                                m_Activity.overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });

                ;

                builder.create().show();

            }
        });
    private void queryUserMeals(Calendar cal) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserMeal");
        query.whereGreaterThan("date", Constants.getDateBefore(cal));
        query.whereLessThan("date", Constants.getDateAfter(cal));
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.include("entries.recipe");
        query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.findInBackground(new FindCallback<ParseObject>() {

            public void done(List<ParseObject> meals, ParseException e) {

                if (e == null) {

                    for (ParseObject object : meals) {

                        UserMeal userMeal = (UserMeal) object;
                        ViewGroup v = (ViewGroup) m_UserMealLayout.findViewWithTag(userMeal.getTitle());
                        LinearLayout list = (LinearLayout) v.findViewById(R.id.diary_entries_list);

                        int total_meal_cals = 0;

                        for (DiaryEntry entry : userMeal.getDiaryEntries()) {
                            View rowView = m_Inflater.inflate(R.layout.diary_entry, null);
                            Recipe recipe = entry.getRecipe();

                            TextView name = (TextView) rowView.findViewById(R.id.name);
                            name.setText(recipe.getName());

                            TextView cals = (TextView) rowView.findViewById(R.id.calories);
                            int c = entry.getTotalCalories();
                            cals.setText(Integer.toString(c));
                            total_meal_cals += c;

                            TextView servings = (TextView) rowView.findViewById(R.id.servings);
                            servings.setText(Float.toString(entry.getServingsMultiplier()) + " servings");
                            list.addView(rowView);

                            rowView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Toast.makeText(m_Activity, "entry click!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        TextView meal_cals = (TextView) v.findViewById(R.id.usermeal_cals);
                        meal_cals.setText(Integer.toString(total_meal_cals));
                        m_FoodCals += total_meal_cals;


                        list.invalidate();
                        list.requestLayout();
                        m_UserMealLayout.invalidate();
                        m_UserMealLayout.requestLayout();
                    }
                    updateCalorieSummary();
                } else {
                    Log.d("DiaryFragment", "Error getting user meals: " + e.getMessage());

                }
            }
        });
    }*/


}
