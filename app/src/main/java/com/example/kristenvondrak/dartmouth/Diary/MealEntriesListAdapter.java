package com.example.kristenvondrak.dartmouth.Diary;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.kristenvondrak.dartmouth.MyMeals.MyMealsFragment;
import com.example.kristenvondrak.dartmouth.Parse.DiaryEntry;
import com.example.kristenvondrak.dartmouth.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by kristenvondrak on 1/25/16.
 */
public class MealEntriesListAdapter extends BaseAdapter{

    private MyMealsFragment m_Fragment;
    private List<Integer> m_UncheckedEntries = new ArrayList<>();
    private LayoutInflater m_Inflater;
    private List<DiaryEntry> m_List = new ArrayList<>();


    public MealEntriesListAdapter(Activity activity, List<DiaryEntry> list, MyMealsFragment fragment) {
        m_Fragment = fragment;
        m_List = list;
        m_Inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    public int getCount() {
        return m_List.size();
    }

    public Object getItem(int position) {
        return m_List.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public void resetData() {
        m_UncheckedEntries.clear();
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View rowView =  m_Inflater.inflate(R.layout.mymeals_entry_list_item, null);
        DiaryEntry entry = m_List.get(position);

        CheckBox checkBox = (CheckBox) rowView.findViewById(R.id.checkbox);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked)
                    m_UncheckedEntries.add(position);
                else
                    m_UncheckedEntries.remove((Integer)position);

                m_Fragment.resetTotalCals(getSelectedEntries());
            }
        });

        TextView name = (TextView) rowView.findViewById(R.id.item_name);
        name.setText(entry.getRecipe().getName());

        TextView servings = (TextView) rowView.findViewById(R.id.item_servings);
        servings.setText(Float.toString(entry.getServingsMultiplier()) + " servings");

        TextView cals = (TextView) rowView.findViewById(R.id.item_cals);
        cals.setText(Integer.toString(entry.getTotalCalories()));

        return rowView;
    }


    public List<DiaryEntry> getSelectedEntries() {
        List<DiaryEntry> list = new ArrayList<>();
        for (int i = 0; i < m_List.size(); i++) {
            if (!m_UncheckedEntries.contains(i))
                list.add(m_List.get(i));
        }
        return list;
    }


}


