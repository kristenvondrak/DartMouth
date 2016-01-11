package com.example.kristenvondrak.dartmouth.Menu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kristenvondrak.dartmouth.Parse.Recipe;
import com.example.kristenvondrak.dartmouth.R;

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

    public FoodItemListAdapter(Activity activity, List<Recipe> list) {
        m_Activity = activity;
        m_List = list;
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
                final View view = inflater.inflate(R.layout.nutrition_dialog, null);

                TextView tv = (TextView)view.findViewById(R.id.name);
                tv.setText(recipe.getName());

                for (int i = 0; i < Nutrients.grams.length; i++) {
                    String grams = Nutrients.get(recipe, i);
                    tv = (TextView)view.findViewById(Nutrients.grams[i]);
                    tv.setText(Nutrients.addSpace(grams));

                    /**
                    if (Nutrients.percents[i] > 0 && Nutrients.stripChars(grams) > 0) {
                        tv = (TextView)view.findViewById(Nutrients.percents[i]);
                        int percent = (int)(((double)Nutrients.stripChars(grams) / Nutrients.daily_grams[i]) * 100);
                        tv.setText(Integer.toString(percent) + "%");
                    }*/
                }


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

                view.invalidate();
                builder.setView(view);
                builder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // add to parse
                        Toast.makeText(m_Activity, "Item added!", Toast.LENGTH_SHORT).show();
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

    private void updateNutrients(View view, Recipe recipe) {
        TextView tv;
        double num_servings = m_ServingsFraction + m_ServingsWhole;
        for (int i = 0; i < Nutrients.grams.length; i++) {
            int value = (int)(Nutrients.convertToDouble(Nutrients.get(recipe, i)) * num_servings);
            String units = Nutrients.getUnits(Nutrients.get(recipe, i));

            tv = (TextView)view.findViewById(Nutrients.grams[i]);
            if (value < 0)
                tv.setText("");
            else
                tv.setText(Integer.toString(value) + " " + units);

            if (Nutrients.percents[i] > 0) {
                tv = (TextView)view.findViewById(Nutrients.percents[i]);
                int percent;
                if (value <= 0)
                    percent = 0;
                else if (value >= Nutrients.daily_grams[i])
                    percent = 100;
                else
                    percent = (int)(((double)value/(double)Nutrients.daily_grams[i])* 100) ;
                tv.setText(Integer.toString(percent) + "%");
            }
        }

    }

    private double parseFraction(String string) {
        String[] parts = string.split("/");
        return Double.parseDouble(parts[0]) / Double.parseDouble(parts[1]);
    }
}

