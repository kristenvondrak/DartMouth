package com.example.kristenvondrak.dartmouth.Diary;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.kristenvondrak.dartmouth.Main.Constants;
import com.example.kristenvondrak.dartmouth.R;

import java.util.Map;

/**
 * Created by kristenvondrak on 10/20/15.
 */
public class DiaryMealListAdapter extends BaseAdapter{
    private Activity m_Activity;
    private Map<String, UserMeal> m_Map;
    private LayoutInflater m_Inflater;
    private Constants.MealTime[] m_List;

    public DiaryMealListAdapter(Activity activity, Map<String, UserMeal> map) {
        m_Activity = activity;
        m_Map = map;
        m_Inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        m_List = Constants.MealTime.values();
    }

    @Override
    public int getCount() {
        return m_List.length;
    }

    @Override
    public Object getItem(int position) {
        return m_Map.get(m_List[position]);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public class Holder {
        TextView name;
        TextView cals;
        LinearLayout list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Holder holder=new Holder();
        View rowView = m_Inflater.inflate(R.layout.diary_meal_list_item, null);

        holder.name = (TextView) rowView.findViewById(R.id.meal_name);
        String name = m_List[position].name();
        holder.name.setText(name + ":");

        holder.cals = (TextView) rowView.findViewById(R.id.meal_cals);
        holder.cals.setText("0");



        holder.list = (LinearLayout) rowView.findViewById(R.id.items_list);
        holder.list.removeAllViews();

        final UserMeal meal = (UserMeal)getItem(position);
        if (meal != null) {
            //  holder.cals.setText(Integer.toString(meal.getTotalNutrients().getCals

            for (DiaryEntry entry : meal.getDiaryEntries()) {
                View line = m_Inflater.inflate(R.layout.diary_list_item, null);

                TextView e_name = (TextView) line.findViewById(R.id.name);
                e_name.setText(entry.getRecipe().getName());

                TextView e_cals = (TextView) line.findViewById(R.id.calories);
                e_cals.setText(Integer.toString(entry.getTotalCalories()));

                TextView e_servings = (TextView) line.findViewById(R.id.servings);
                e_servings.setText(Float.toString(entry.getServingsMultiplier()) + " servings");
                holder.list.addView(line);
            }

        }
       /*

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(m_Activity);
                LayoutInflater inflater = m_Activity.getLayoutInflater();
                final View view = inflater.inflate(R.layout.nutrition_dialog, null);
                builder.setView(view);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                final AlertDialog dialog  = builder.create();
                dialog.show();
            }
        });*/
        return rowView;
    }
}


