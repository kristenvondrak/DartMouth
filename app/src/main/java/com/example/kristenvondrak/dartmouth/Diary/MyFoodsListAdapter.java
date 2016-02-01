package com.example.kristenvondrak.dartmouth.Diary;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.kristenvondrak.dartmouth.Menu.NutritionFragment;
import com.example.kristenvondrak.dartmouth.Parse.Recipe;
import com.example.kristenvondrak.dartmouth.R;

import java.util.List;


/**
 * Created by kristenvondrak on 1/25/16.
 */
public class MyFoodsListAdapter extends BaseAdapter{

    private NutritionFragment m_Fragment;
    private List<Recipe> m_List;
    private LayoutInflater m_Inflater;


    public MyFoodsListAdapter(Activity activity, MyFoodsFragment fragment, List<Recipe> list) {
        m_Fragment = fragment;
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


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Recipe recipe = m_List.get(position);

        View rowView = m_Inflater.inflate(R.layout.recipe_list_item, null);
        TextView name =(TextView) rowView.findViewById(R.id.item_name_text_view);
        name.setText(recipe.getName());

        TextView cals =(TextView) rowView.findViewById(R.id.item_cals_text_view);
        cals.setText(recipe.getCalories() + " cals");

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_Fragment.onItemClick(recipe);
            }
        });

        return rowView;
    }


    private void setTextViewValue(View v, int id, String text) {
        ((TextView)v.findViewById(id)).setText(text);
    }


}


