package com.example.kristenvondrak.dartmouth.MyMeals;


import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kristenvondrak.dartmouth.Main.Constants;
import com.example.kristenvondrak.dartmouth.Parse.DiaryEntry;
import com.example.kristenvondrak.dartmouth.Parse.Recipe;
import com.example.kristenvondrak.dartmouth.Parse.UserMeal;
import com.example.kristenvondrak.dartmouth.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


/**
 * Created by kristenvondrak on 1/25/16.
 */
public class MealListAdapter extends BaseAdapter{

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;

    public static final String DATE_FORMAT = "EEEE, LLL d";

    private Activity m_Activity;
    private MyMealsFragment m_Fragment;
    private LayoutInflater m_Inflater;


    private ArrayList<Object> m_Data = new ArrayList<Object>();
    private TreeSet<Integer> m_SeparatorsSet = new TreeSet<Integer>();


    public MealListAdapter(Activity activity, MyMealsFragment fragment) {
        m_Activity = activity;
        m_Fragment = fragment;
        m_Inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void updateData(List<UserMeal> list) {
        m_Data.clear();
        m_SeparatorsSet.clear();

        // Map the dates to usermeals
        HashMap<String, HashMap<String, UserMeal>> dateMap = new HashMap<>();
        for (UserMeal meal : list) {
            String date = getDateString(meal.getDate());
            if (dateMap.containsKey(date)) {
                dateMap.get(date).put(meal.getTitle(), meal);
            } else {
                HashMap<String, UserMeal> mealMap = new HashMap<>();
                mealMap.put(meal.getTitle(), meal);
                dateMap.put(date, mealMap);
            }
        }

        // Create ordered list of headers (categories) and items (recipes)
        for (String d : getOrderedDates(dateMap.keySet())) {
            addSeparatorItem(d);
            for (Constants.UserMeals m : Constants.UserMeals.values()) {
                if (dateMap.get(d).containsKey(m.name()))
                    addItem(dateMap.get(d).get(m.name()));
            }
        }

        notifyDataSetChanged();
    }

    public void addItem(UserMeal item) {
        m_Data.add(item);
        notifyDataSetChanged();
    }

    public void addSeparatorItem(final String item) {
        m_Data.add(item);
        // save separator position
        m_SeparatorsSet.add(m_Data.size() - 1);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return m_SeparatorsSet.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
    }


    public int getCount() {
        return m_Data.size();
    }

    public Object getItem(int position) {
        return m_Data.get(position);
    }

    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        View rowView = null;
        int type = getItemViewType(position);

        switch (type) {
            case TYPE_ITEM:
                final UserMeal meal = (UserMeal) m_Data.get(position);
                rowView = m_Inflater.inflate(R.layout.mymeals_list_item, null);

                // Meal name: Breakfast, Lunch, Dinner, Snacks
                TextView name =(TextView) rowView.findViewById(R.id.meal_name);
                name.setText(meal.getTitle());

                // Meal icon
                ImageView icon = (ImageView) rowView.findViewById(R.id.meal_icon);
                icon.setImageDrawable(getMealIcon(meal));

                // List of recipes in the meal
                TextView items =(TextView) rowView.findViewById(R.id.meal_items_list);
                String list = "";
                int total = meal.getDiaryEntries().size();
                for (int i = 0; i < total; i++) {
                    DiaryEntry entry = meal.getDiaryEntries().get(i);
                    Recipe r = entry.getRecipe();
                    list += r.getName();
                    if (i != total - 1)
                        list += ", ";
                }
                items.setText(list);


                rowView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        m_Fragment.onMealClick(meal);
                    }
                });

                break;

            case TYPE_SEPARATOR:
                String date = (String) m_Data.get(position);
                rowView = m_Inflater.inflate(R.layout.mymeals_date_header, null);
                TextView text = (TextView) rowView.findViewById(R.id.date_text);
                text.setText(date);
                break;
        }

        return rowView;
    }


    private Drawable getMealIcon(UserMeal meal) {
        switch (meal.getTitle()) {
            case "Breakfast":
                return m_Activity.getResources().getDrawable(R.drawable.sunrise_filled);
            case "Lunch":
                return m_Activity.getResources().getDrawable(R.drawable.sun_filled);
            case "Dinner":
                return m_Activity.getResources().getDrawable(R.drawable.sunset_filled);
            default:
                return m_Activity.getResources().getDrawable(R.drawable.clock);
        }
    }


    public static String getDateString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(date);
    }

    public List<String> getOrderedDates(Set<String> dates) {
        List<String> list = new ArrayList<>();
        for (String s : dates)
            list.add(s);

        Collections.sort(list, new Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
                try {
                    Date dateA = sdf.parse(a);
                    Date dateB = sdf.parse(b);
                    return dateB.compareTo(dateA);
                } catch (ParseException e) {
                    return 0;
                }
            }
        });

        return list;
    }



}


