package com.example.kristenvondrak.dartmouth.Menu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.kristenvondrak.dartmouth.Main.Constants;
import com.example.kristenvondrak.dartmouth.Parse.ParseAPI;
import com.example.kristenvondrak.dartmouth.Parse.Recipe;
import com.example.kristenvondrak.dartmouth.Parse.User;
import com.example.kristenvondrak.dartmouth.Parse.UserMeal;
import com.example.kristenvondrak.dartmouth.R;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

/**
 * Created by kristenvondrak on 1/24/16.
 */
public class MenuItemListAdapter extends BaseAdapter {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;
    private static final int TYPE_MAX_COUNT = TYPE_SEPARATOR + 1;


    private LayoutInflater m_Inflater;
    private MenuFragment m_Fragment;


    private ArrayList<Object> m_Data = new ArrayList<Object>();
    private TreeSet<Integer> m_SeparatorsSet = new TreeSet<Integer>();

    public MenuItemListAdapter(Activity activity, MenuFragment fragment) {
        m_Fragment = fragment;
        m_Inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void updateData(List<Recipe> list) {
        m_Data.clear();
        m_SeparatorsSet.clear();

        // Map the categories to recipes
        HashMap<String, List<Recipe>> categoryMap = new HashMap<>();
        for (Recipe r : list) {
            if (categoryMap.containsKey(r.getCategory()))
                categoryMap.get(r.getCategory()).add(r);
            else {
                List<Recipe> set = new ArrayList<>();
                set.add(r);
                categoryMap.put(r.getCategory(), set);
            }
        }

        // Create ordered list of headers (categories) and items (recipes)
        for (String category : categoryMap.keySet()) {
            addSeparatorItem(category);
            for (Recipe recipe : categoryMap.get(category))
                addItem(recipe);
        }

        notifyDataSetChanged();
    }


    public void addItem(Recipe item) {
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

    @Override
    public int getViewTypeCount() {
        return TYPE_MAX_COUNT;
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
                final Recipe recipe = (Recipe) m_Data.get(position);

                // recipe name
                rowView = m_Inflater.inflate(R.layout.menu_list_item, null);
                TextView recipe_name = (TextView) rowView.findViewById(R.id.item_name_text_view);
                recipe_name.setText(recipe.getName());

                // calories
                TextView cals = (TextView) rowView.findViewById(R.id.item_cals_text_view);
                cals.setText(recipe.getCalories() + " cals");

                // on click show nutrients
                rowView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        m_Fragment.onItemClick(recipe);
                    }
                });
                break;

            case TYPE_SEPARATOR:
                String category = (String) m_Data.get(position);

                // category name
                rowView = m_Inflater.inflate(R.layout.menu_list_category, null);
                TextView category_name = (TextView) rowView.findViewById(R.id.category_name);
                category_name.setText(category);
                break;
        }

        return rowView;
    }
}