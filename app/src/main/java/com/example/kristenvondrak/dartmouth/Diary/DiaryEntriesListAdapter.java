package com.example.kristenvondrak.dartmouth.Diary;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.kristenvondrak.dartmouth.Parse.Recipe;
import com.example.kristenvondrak.dartmouth.R;

import java.util.List;

/**
 * Created by kristenvondrak on 10/20/15.
 */
public class DiaryEntriesListAdapter extends BaseAdapter{

    private Activity m_Activity;
    private LayoutInflater m_Inflater;
    private List<DiaryEntry> m_List;

    public DiaryEntriesListAdapter(Activity activity, List<DiaryEntry> list) {
        m_Activity = activity;
        m_Inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        m_List = list;
    }


    @Override
    public int getCount() {
        return m_List.size();
    }

    @Override
    public Object getItem(int position) {
        return m_List.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = m_Inflater.inflate(R.layout.diary_entry, null);
        DiaryEntry entry = m_List.get(position);
        Recipe recipe = entry.getRecipe();

        TextView name = (TextView) rowView.findViewById(R.id.name);
        TextView cals = (TextView) rowView.findViewById(R.id.calories);
        TextView servings = (TextView) rowView.findViewById(R.id.servings);

        name.setText(recipe.getName());
        cals.setText(entry.getTotalCalories());
        servings.setText(Float.toString(entry.getServingsMultiplier()) + " servings");

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(m_Activity);
                final View view = m_Inflater.inflate(R.layout.nutrition_dialog, null);
                builder.setView(view);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                final AlertDialog dialog  = builder.create();
                dialog.show();
            }
        });
        return rowView;
    }
}


