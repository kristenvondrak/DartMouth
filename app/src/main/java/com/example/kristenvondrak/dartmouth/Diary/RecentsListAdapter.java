package com.example.kristenvondrak.dartmouth.Diary;


import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.kristenvondrak.dartmouth.Parse.Recipe;
import com.example.kristenvondrak.dartmouth.R;

import java.util.List;


/**
 * Created by kristenvondrak on 1/25/16.
 */
public class RecentsListAdapter extends BaseAdapter{

    private RecentsFragment m_Fragment;
    private List<Recipe> m_List;
    private LayoutInflater m_Inflater;


    public RecentsListAdapter(Activity activity, RecentsFragment fragment, List<Recipe> list) {
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

        View rowView = m_Inflater.inflate(R.layout.recents_list_item, null);
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


}


