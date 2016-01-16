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
        m_CurrentDate = Calendar.getInstance();
        m_CurrentDateTextView = (TextView) v.findViewById(R.id.date_text_view);
        m_NextDateButton = (ImageView) v.findViewById(R.id.next_date_btn);
        m_PreviousDateButton = (ImageView) v.findViewById(R.id.prev_date_btn);

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

        for (Constants.UserMeals title : Constants.UserMeals.values()) {
            ViewGroup usermeal = (ViewGroup)m_Inflater.inflate(R.layout.diary_usermeal, null);
            View divider = m_Inflater.inflate(R.layout.diary_usermeal_divider, null);
            TextView name = (TextView) usermeal.findViewById(R.id.usermeal_name);
            name.setText(title.name());
            usermeal.setTag(title.name());
            m_UserMealLayout.addView(usermeal);
        }
        m_UserMealLayout.invalidate();
        m_UserMealLayout.requestLayout();

        update();
        return v;
    }

    private void updateDate() {
        update();
    }

    private void update() {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        m_CurrentDateTextView.setText(sdf.format(m_CurrentDate.getTime()));
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
                            ListView listView = (ListView) v.findViewById(R.id.diary_entries_list);
                            DiaryEntriesListAdapter adapter = new DiaryEntriesListAdapter(m_Activity, userMeal.getDiaryEntries());
                            listView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }
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
