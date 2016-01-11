package com.example.kristenvondrak.dartmouth.Diary;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.kristenvondrak.dartmouth.R;


public class DiaryFragment extends Fragment {

    private ListView m_MealListView;
    //private DiaryMealListAdapter m_MealListAdapter;
    //private ArrayList<DiaryEntry> m_DiaryEntries;
    //private DatabaseHandler m_DbHandler;
    private Activity m_Activity;

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
        m_MealListView = (ListView) v.findViewById(R.id.meal_list);

        // Initialize to default



       // m_MealListAdapter = new DiaryMealListAdapter(m_Activity, m_DiaryEntries, m_DbHandler);
        //m_MealListView.setAdapter(m_MealListAdapter);

        int total = 0;
       // for (DiaryEntry de : m_DiaryEntries) {
         //   total += de.getTotalCals();
        //}

        TextView food = (TextView) v.findViewById(R.id.food_total);
        food.setText(Integer.toString(total));

        TextView remaining = (TextView) v.findViewById(R.id.remaining);
        remaining.setText(Integer.toString(1500 - total + 350));

        return v;
    }
}
