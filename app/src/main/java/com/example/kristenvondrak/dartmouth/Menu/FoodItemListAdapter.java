package com.example.kristenvondrak.dartmouth.Menu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.NumberPicker;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kristenvondrak.dartmouth.Diary.DiaryEntry;
import com.example.kristenvondrak.dartmouth.Diary.UserMeal;
import com.example.kristenvondrak.dartmouth.Main.Constants;
import com.example.kristenvondrak.dartmouth.Parse.Recipe;
import com.example.kristenvondrak.dartmouth.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by kristenvondrak on 10/20/15.
 */
public class FoodItemListAdapter extends BaseAdapter{
    private Activity m_Activity;
    private List<Recipe> m_List;
    private LayoutInflater m_Inflater;
    private double m_ServingsWhole = 1;
    private double m_ServingsFraction = 0;
    private Calendar m_Calendar;
    private Constants.MealTime m_MealTime;
    private TextView m_SelectedUserMeal;
    private TableRow m_UserMealTabs;

    public FoodItemListAdapter(Activity activity, List<Recipe> list, Calendar day) {
        m_Activity = activity;
        m_List = list;
        m_Calendar = day;
        m_Inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

    public class Holder {
        TextView textView;
        // Add other attributes here
    }

    public void setMealTime(Constants.MealTime mealtime) {
        m_MealTime = mealtime;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder=new Holder();
        final Recipe recipe = m_List.get(position);

        View rowView = m_Inflater.inflate(R.layout.menu_list_item, null);
        holder.textView =(TextView) rowView.findViewById(R.id.item_name_text_view);
        holder.textView.setText(recipe.getName());

        // GET NUTRIENTS for given recipe
       // final Nutrients nutrients = m_DbHandler.getNutrients(Integer.toString(m_List.get(position).getDID()));
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(m_Activity);
                LayoutInflater inflater = m_Activity.getLayoutInflater();
                final View view = inflater.inflate(R.layout.dialog_nutrition, null);
                TableRow tabs = (TableRow)view.findViewById(R.id.usermeal_tabs);
                TextView tv = (TextView)view.findViewById(R.id.name);
                tv.setText(recipe.getName());

                setTextViewValue(view, R.id.calories, recipe.getCalories());
                setTextViewValue(view, R.id.fat_calories, recipe.getFatCalories());
                setTextViewValue(view, R.id.total_fat,  recipe.getTotalFat());
                setTextViewValue(view, R.id.saturated_fat, recipe.getSaturatedFat());
                setTextViewValue(view, R.id.cholesterol, recipe.getCholestrol());
                setTextViewValue(view, R.id.sodium, recipe.getSodium());
                setTextViewValue(view, R.id.carbs, recipe.getTotalCarbs());
                setTextViewValue(view, R.id.fiber, recipe.getFiber());
                setTextViewValue(view, R.id.sugars, recipe.getSugars());
                setTextViewValue(view, R.id.protein, recipe.getProtein());


                final NumberPicker number = (NumberPicker) view.findViewById(R.id.servings_picker_number);
                final NumberPicker fraction = (NumberPicker) view.findViewById(R.id.servings_picker_fraction);


                // Selector wheel with values -, 1, 2 ... 99
                final String[] numbers = new String[100];
                numbers[0] = "-";
                for (int i = 1; i < numbers.length; i++) {
                    numbers[i] = String.valueOf(i);
                }
                number.setMinValue(0);
                number.setMaxValue(numbers.length - 1);
                number.setDisplayedValues(numbers);
                number.setWrapSelectorWheel(false);
                number.setValue((int) m_ServingsWhole);
                number.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        if (newVal == 0)
                            m_ServingsWhole = 0;
                        else
                            m_ServingsWhole = Integer.parseInt(numbers[number.getValue()]);

                        updateNutrients(view, recipe);
                    }
                });

                // Selector wheel with values -, 1/8, 1/4, 1/3, 1/2, 2/3, 3/4
                final String[] fractions = {"-", "1/8", "1/4", "1/3", "1/2", "2/3", "3/4"};
                fraction.setMinValue(0);
                fraction.setMaxValue(fractions.length - 1);
                fraction.setDisplayedValues(fractions);
                fraction.setWrapSelectorWheel(false);
                fraction.setOnValueChangedListener(new NumberPicker.OnValueChangeListener(){
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        if (newVal == 0)
                            m_ServingsFraction = 0;
                        else
                            m_ServingsFraction = parseFraction(fractions[newVal]);

                        updateNutrients(view, recipe);
                    }
                });


                // Create tabs for selecting the user meal
                boolean found = false;
                for (Constants.UserMeals meal : Constants.UserMeals.values()) {
                    ViewGroup tab = (ViewGroup)inflater.inflate(R.layout.custom_tab, null);
                    TextView name = (TextView)tab.findViewById(R.id.tab_text);
                    name.setText(meal.name());
                    name.setTextColor(m_Activity.getResources().getColor(R.color.tab_highlight));
                    name.setTag(meal.name());

                    if (meal.name().equals(m_MealTime.name())) {
                        found = true;
                        m_SelectedUserMeal = name;
                        m_SelectedUserMeal.setTypeface(Typeface.DEFAULT_BOLD);
                    }
                    tab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            m_SelectedUserMeal.setTypeface(Typeface.DEFAULT);
                            TextView tv = (TextView)v.findViewById(R.id.tab_text);
                            tv.setTypeface(Typeface.DEFAULT_BOLD);
                            m_SelectedUserMeal = tv;
                        }
                    });
                    tabs.addView(tab);
                }

                if (!found) {
                    m_SelectedUserMeal = (TextView)tabs.getChildAt(0).findViewById(R.id.tab_text);
                    m_SelectedUserMeal.setTypeface(Typeface.DEFAULT_BOLD);
                }

                tabs.invalidate();
                view.invalidate();
                builder.setView(view);
                builder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        // Create new diary entry and save to parse
                        final DiaryEntry diaryEntry = new DiaryEntry();
                        diaryEntry.setDate(m_Calendar.getTime());
                        diaryEntry.setUser(ParseUser.getCurrentUser());
                        diaryEntry.setRecipe(recipe);
                        diaryEntry.setServingsMultiplier((float)(m_ServingsWhole + m_ServingsFraction));
                        diaryEntry.saveInBackground();

                        // Check if user meal exists
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserMeal");
                        query.whereGreaterThan("date", Constants.getDateBefore(m_Calendar));
                        query.whereLessThan("date", Constants.getDateAfter(m_Calendar));
                        query.whereEqualTo("user", ParseUser.getCurrentUser());
                        query.whereEqualTo("title", m_SelectedUserMeal.getTag());
                        Log.d("Food Item List Adapter: Date before",Constants.getDateBefore(m_Calendar).toString());
                        Log.d("Food Item List Adapter: Date after",Constants.getDateAfter(m_Calendar).toString());

                        query.findInBackground(new FindCallback<ParseObject>() {
                            public void done(List<ParseObject> meals, ParseException e) {
                                if (e == null && meals.size() > 0) {
                                    // Add diary entry to already existing UserMeal
                                    UserMeal meal = (UserMeal) meals.get(0);
                                    meal.addDiaryEntry(diaryEntry);
                                    meal.saveInBackground();
                                } else {
                                    // Add new UserMeal to parse
                                    UserMeal newmeal = new UserMeal();
                                    newmeal.put("date", m_Calendar.getTime());
                                    newmeal.put("user", ParseUser.getCurrentUser());
                                    newmeal.put("title", m_SelectedUserMeal.getTag());
                                    List<DiaryEntry> list = new ArrayList<DiaryEntry>();
                                    list.add(diaryEntry);
                                    newmeal.put("entries", list);
                                    newmeal.saveInBackground();
                                }
                            }
                        });
                        Toast.makeText(m_Activity, "Added to diary!", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

                final AlertDialog dialog  = builder.create();
                dialog.show();
            }
        });

        return rowView;
    }


    private void setTextViewValue(View v, int id, String text) {
        ((TextView)v.findViewById(id)).setText(text);
    }

    private String getNewValue(String value, double multiplier) {
        int v = (int)(Nutrients.convertToDouble(value) * multiplier);
        String u = Nutrients.getUnits(value);

        if (v < 0)
            return "";

        return Integer.toString(v) + u;
    }

    private void updateNutrients(View view, Recipe recipe) {

        double num_servings = m_ServingsFraction + m_ServingsWhole;

        setTextViewValue(view, R.id.calories, getNewValue(recipe.getCalories(), num_servings));
        setTextViewValue(view, R.id.fat_calories,  getNewValue(recipe.getFatCalories(), num_servings));
        setTextViewValue(view, R.id.total_fat,  getNewValue(recipe.getTotalFat(), num_servings));
        setTextViewValue(view, R.id.saturated_fat,  getNewValue(recipe.getSaturatedFat(), num_servings));
        setTextViewValue(view, R.id.cholesterol,  getNewValue(recipe.getCholestrol(), num_servings));
        setTextViewValue(view, R.id.sodium, getNewValue(recipe.getSodium(), num_servings));
        setTextViewValue(view, R.id.carbs,  getNewValue(recipe.getTotalCarbs(), num_servings));
        setTextViewValue(view, R.id.fiber,  getNewValue(recipe.getFiber(), num_servings));
        setTextViewValue(view, R.id.sugars,  getNewValue(recipe.getSugars(), num_servings));
        setTextViewValue(view, R.id.protein,  getNewValue(recipe.getProtein(), num_servings));
    }

    private double parseFraction(String string) {
        String[] parts = string.split("/");
        return Double.parseDouble(parts[0]) / Double.parseDouble(parts[1]);
    }
}

