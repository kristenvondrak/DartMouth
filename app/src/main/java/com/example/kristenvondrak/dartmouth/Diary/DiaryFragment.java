package com.example.kristenvondrak.dartmouth.Diary;


import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.kristenvondrak.dartmouth.Main.Constants;
import com.example.kristenvondrak.dartmouth.Parse.Recipe;
import com.example.kristenvondrak.dartmouth.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class DiaryFragment extends Fragment {

    private Activity m_Activity;

    // Meals
    private LinearLayout m_UserMealLayout;

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

    public static final String DATE_FORMAT = "EEE, LLL d";

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
        m_UserMealLayout = (LinearLayout) v.findViewById(R.id.usermeal_layout);
        m_GoalTextView = (TextView) v.findViewById(R.id.total_goal_cals);
        m_FoodTextView = (TextView) v.findViewById(R.id.total_food_cals);
        m_ExerciseTextView = (TextView) v.findViewById(R.id.total_exercise_cals);
        m_RemainingTextView = (TextView) v.findViewById(R.id.total_remaining_cals);
        m_CurrentDateTextView = (TextView) v.findViewById(R.id.date_text_view);
        m_NextDateButton = (ImageView) v.findViewById(R.id.next_date_btn);
        m_PreviousDateButton = (ImageView) v.findViewById(R.id.prev_date_btn);
        m_CurrentDate = Calendar.getInstance();

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

        m_ExcerciseCals = 0;
        m_FoodCals = 0;
        m_GoalCals = DEFAULT_GOAL_CALS;

        addUserMeals();
        update();
        return v;
    }

    private void addUserMeals() {
        for (int i = 0; i < Constants.UserMeals.values().length; i++) {
            String title = Constants.UserMeals.values()[i].name();
            ViewGroup usermeal = (ViewGroup)m_Inflater.inflate(R.layout.diary_usermeal, null);

            TextView name = (TextView) usermeal.findViewById(R.id.usermeal_name);
            name.setText(title + ":");

            TextView cals = (TextView) usermeal.findViewById(R.id.usermeal_cals);
            cals.setText("0");

            usermeal.setTag(title);
            m_UserMealLayout.addView(usermeal);

            if (i < Constants.UserMeals.values().length - 1) {
                View divider = m_Inflater.inflate(R.layout.diary_usermeal_divider, null);
                m_UserMealLayout.addView(divider);
            }
        }
        m_UserMealLayout.invalidate();
        m_UserMealLayout.requestLayout();
    }

    private void resetUserMeals() {
        m_FoodCals = 0;
        m_UserMealLayout.removeAllViews();
        addUserMeals();
    }

    private void updateCalorieSummary() {
        m_ExerciseTextView.setText(Integer.toString(m_ExcerciseCals));
        m_GoalTextView.setText(Integer.toString(m_GoalCals));
        m_FoodTextView.setText(Integer.toString(m_FoodCals));
        m_RemainingTextView.setText(Integer.toString(m_GoalCals - m_FoodCals + m_ExcerciseCals));
        Log.d("^^^^^^^^^", Integer.toString(m_GoalCals - m_FoodCals + m_ExcerciseCals));
    }

    private void update() {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        m_CurrentDateTextView.setText(sdf.format(m_CurrentDate.getTime()));
        resetUserMeals();
        new ParseDiaryDayRequest().execute();
    }

    private class ParseDiaryDayRequest extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            ParseQuery<ParseObject> query = ParseQuery.getQuery("UserMeal");
            query.whereGreaterThan("date", Constants.getDateBefore(m_CurrentDate));
            query.whereLessThan("date", Constants.getDateAfter(m_CurrentDate));
            query.whereEqualTo("user", ParseUser.getCurrentUser());
            query.include("entries.recipe");
            query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
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
                            }
                            TextView meal_cals = (TextView) v.findViewById(R.id.usermeal_cals);
                            meal_cals.setText(Integer.toString(total_meal_cals));
                            m_FoodCals += total_meal_cals;
                            Log.d("^^^^", Integer.toString(m_FoodCals));

                            list.invalidate();
                            list.requestLayout();
                        }
                        updateCalorieSummary();
                    } else {
                        Log.d("DiaryFragment", "Error getting user meals: " + e.getMessage());
                    }
                }
            });

            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {

        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }





    /*



    View v = inflater.inflate(R.layout.fragment_diary, container, false);
        m_MealListView = (ListView) v.findViewById(R.id.meal_list);

        // Initialize to default
        m_DiaryEntries = new ArrayList<>();
        int [] array = {DiaryEntry.breakfast, DiaryEntry.lunch, DiaryEntry.dinner, DiaryEntry.snacks};
        for (int i : array) {
            DiaryEntry entry = new DiaryEntry(i);
            m_DiaryEntries.add(entry);
        }


        SharedPreferences prefs = m_Activity.getSharedPreferences("DIARY", Context.MODE_PRIVATE);
        Map<String,?> keys = prefs.getAll();
        for(Map.Entry<String,?> entry : keys.entrySet()){
            String value = entry.getValue().toString();
            Log.d("Diary Fragment", value);
            String[] info = value.split(";");
            String name = info[0];
            int meal = Integer.parseInt(info[1]);
            int cals = Integer.parseInt(info[2]);
            double servings = Double.parseDouble(info[3]);
            DummyHistory d = new DummyHistory(name, cals, servings, meal);
            for (DiaryEntry de : m_DiaryEntries) {
                if (de.getId() == d.getMeal()) {
                    de.addHistoryItem(d);
                    Log.d("***", "Added to " + Integer.toString(d.getMeal()));
                    break;
                }
            }
        }

        m_MealListAdapter = new DiaryMealListAdapter(m_Activity, m_DiaryEntries, m_DbHandler);
        m_MealListView.setAdapter(m_MealListAdapter);

        int total = 0;
        for (DiaryEntry de : m_DiaryEntries) {
            total += de.getTotalCals();
        }

        TextView food = (TextView) v.findViewById(R.id.food_total);
        food.setText(Integer.toString(total));

        TextView remaining = (TextView) v.findViewById(R.id.remaining);
        remaining.setText(Integer.toString(1500 - total + 350));

        return v;
     */
}
