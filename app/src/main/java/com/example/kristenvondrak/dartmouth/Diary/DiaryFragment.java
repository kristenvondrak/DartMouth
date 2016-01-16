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
import android.widget.ListView;
import android.widget.TextView;

import com.example.kristenvondrak.dartmouth.Main.Constants;
import com.example.kristenvondrak.dartmouth.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class DiaryFragment extends Fragment {

    private Activity m_Activity;

    // Meals
    private ListView m_MealListView;
    private DiaryMealListAdapter m_MealMapAdapter;
    private HashMap<String, UserMeal> m_MealsMap;

    // Date
    private Calendar m_CurrentDate;
    private TextView m_CurrentDateTextView;
    private ImageView m_NextDateButton;
    private ImageView m_PreviousDateButton;

    public static final String DATE_FORMAT = "EEE, LLL d";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_Activity = getActivity();


        // need to get the history items from the db for given day
            // add to diary entry
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_diary, container, false);

        /*
        m_MealListView = (ListView) v.findViewById(R.id.meal_list_view);
        m_CurrentDateTextView = (TextView) v.findViewById(R.id.date_text_view);
        m_NextDateButton = (ImageView) v.findViewById(R.id.next_date_btn);
        m_PreviousDateButton = (ImageView) v.findViewById(R.id.prev_date_btn);

        // Breakfast, Lunch, Dinner, Snacks, Late Night
        m_MealsMap = new HashMap<>();
        for (Constants.MealTime m : Constants.MealTime.values()) {
            String title = Constants.diaryTitleForMealTime.get(m);
            m_MealsMap.put(title, null);
        }

        m_MealMapAdapter = new DiaryMealListAdapter(m_Activity, m_MealsMap);

        m_CurrentDate = Calendar.getInstance();
        m_NextDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_CurrentDate.add(Calendar.DATE, 1);
                updateDate();
            }
        });

        m_PreviousDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_CurrentDate.add(Calendar.DATE, -1);
                updateDate();
            }
        });

        updateDate();
        // Initialize to default



       // m_MealListAdapter = new DiaryMealListAdapter(m_Activity, m_MealsList);
       // m_MealListView.setAdapter(m_MealListAdapter);

        //int total = 0;
       // for (DiaryEntry de : m_DiaryEntries) {
         //   total += de.getTotalCals();
        //}

        //TextView food = (TextView) v.findViewById(R.id.food_total);
        //food.setText(Integer.toString(total));

        //TextView remaining = (TextView) v.findViewById(R.id.remaining);
        //remaining.setText(Integer.toString(1500 - total + 350));

        */

        return v;
    }

    private void updateDate() {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        m_CurrentDateTextView.setText(sdf.format(m_CurrentDate.getTime()));
        update();
    }

    private void update() {
        String day = Integer.toString(m_CurrentDate.get(Calendar.DAY_OF_MONTH));
        String month = Integer.toString(m_CurrentDate.get(Calendar.MONTH) + 1);
        String year = Integer.toString(m_CurrentDate.get(Calendar.YEAR));
        new ParseDiaryDayRequest().execute(day, month, year);
    }

    private class ParseDiaryDayRequest extends AsyncTask<String, Void, String> {

        final List<UserMeal> recipesList = new ArrayList<>();

        @Override
        protected String doInBackground(String... params) {

            ParseQuery<ParseObject> query = ParseQuery.getQuery("UserMeal");
            query.whereGreaterThan("date", Constants.getDateBefore(m_CurrentDate));
            query.whereLessThan("date", Constants.getDateAfter(m_CurrentDate));
            query.whereEqualTo("user", ParseUser.getCurrentUser());
            query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> meals, ParseException e) {
                    if (e == null) {
                        for (ParseObject object : meals) {
                            UserMeal userMeal = (UserMeal) object;
                            m_MealsMap.put(userMeal.getTitle(), userMeal);
                        }
                        m_MealMapAdapter.notifyDataSetChanged();
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
