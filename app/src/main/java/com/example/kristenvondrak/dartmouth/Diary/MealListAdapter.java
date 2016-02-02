package com.example.kristenvondrak.dartmouth.Diary;


import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kristenvondrak.dartmouth.Parse.DiaryEntry;
import com.example.kristenvondrak.dartmouth.Parse.Recipe;
import com.example.kristenvondrak.dartmouth.Parse.UserMeal;
import com.example.kristenvondrak.dartmouth.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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

    public static String getDateString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(date);
    }

    public void updateData(List<UserMeal> list) {
        m_Data.clear();
        m_SeparatorsSet.clear();


        // Map the dates to usermeals
        HashMap<String, List<UserMeal>> dateMap = new HashMap<>();
        for (UserMeal meal : list) {
            String date = getDateString(meal.getDate());
            if (dateMap.containsKey(date))
                dateMap.get(date).add(meal);
            else {
                List<UserMeal> set = new ArrayList<>();
                set.add(meal);
                dateMap.put(date, set);
            }
        }

        // Create ordered list of headers (categories) and items (recipes)
        for (String d : dateMap.keySet()) {
            addSeparatorItem(d);
            for (UserMeal m : dateMap.get(d))
                addItem(m);
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

                // TODO: Show meal details on click
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

        /*
        final UserMeal meal = m_List.get(position);

        View rowView = m_Inflater.inflate(R.layout.mymeals_list_item, null);

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
                // Do something

            }
        });
*/
        return rowView;
    }


    /*
    private void onMealClick(UserMeal meal) {


        final TextView name = (TextView) v.findViewById(R.id.usermeal_name);
        final ViewFlipper vf = (ViewFlipper) view.findViewById(R.id.usermeal_viewflipper);
        vf.setInAnimation(m_Activity, R.anim.slide_in_from_bottom);
        vf.setOutAnimation(m_Activity, R.anim.slide_out_to_bottom);
        vf.showNext();

        final RadioGroup rg = (RadioGroup) vf.findViewById(R.id.usermeal_radiogroup);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                String usermeal = (String) (group.findViewById(checkedId)).getTag();
                name.setText(usermeal);
                m_SelectedUserMeal = usermeal;
                vf.setInAnimation(m_Activity, R.anim.slide_in_from_bottom);
                vf.setOutAnimation(m_Activity, R.anim.slide_out_to_bottom);
                vf.showPrevious();
            }
        });




        final AlertDialog.Builder builder = new AlertDialog.Builder(m_Activity);
        final View view = m_Inflater.inflate(R.layout.dialog_mymeals, null);

        // List of the food items-- can check or uncheck to remove from the meal
        LinearLayout list = (LinearLayout) view.findViewById(R.id.meal_entries_list);

        for (DiaryEntry entry : meal.getDiaryEntries()) {
            final View item = m_Inflater.inflate(R.layout.mymeals_entry_list_item, null);
            CheckBox checkBox = (CheckBox) item.findViewById(R.id.checkbox);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // remove/ add to a list??
                }
            });

            TextView name = (TextView) item.findViewById(R.id.item_name);
            name.setText(entry.getRecipe().getName());

            TextView servings = (TextView) item.findViewById(R.id.item_servings);
            servings.setText(Float.toString(entry.getServingsMultiplier()) + " servings");

            TextView cals = (TextView) item.findViewById(R.id.item_cals);
            cals.setText(Integer.toString(entry.getTotalCalories()));

            list.addView(item);
        }

        list.invalidate();
        view.invalidate();
        builder.setView(view);
        final AlertDialog dialog = builder.create();


        // Add button
        TextView addButton = (TextView) view.findViewById(R.id.add_btn);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Iterate through list, if checked, get diary entry and add to list
                // save all diary entries
                // make copy of user meal and save

                //ParseAPI.addDiaryEntry(m_Calendar, ParseUser.getCurrentUser(), recipe,
                  //      (float) (m_ServingsWhole + m_ServingsFraction), m_SelectedUserMeal);

                dialog.dismiss();
                Toast.makeText(m_Activity, "Added to diary (not really)!", Toast.LENGTH_SHORT).show();
            }

        });

        // Cancel Button
        TextView cancelButton = (TextView) view.findViewById(R.id.cancel_btn);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        // UserMeal selector
        final Spinner spinner = (Spinner) view.findViewById(R.id.usermeal_spinner);

        // Create an ArrayAdapter using the string array
        final ArrayList<String> meals = new ArrayList<>();
        for (Constants.UserMeals m : Constants.UserMeals.values()) {
            meals.add(m.name());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(m_Activity, R.layout.meal_spinner_item, meals);
        adapter.setDropDownViewResource(R.layout.meal_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                m_SelectedUserMeal = meals.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Nothing??
            }
        });
        dialog.show();
    }
*/

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

    private void setTextViewValue(View v, int id, String text) {
        ((TextView)v.findViewById(id)).setText(text);
    }


}


