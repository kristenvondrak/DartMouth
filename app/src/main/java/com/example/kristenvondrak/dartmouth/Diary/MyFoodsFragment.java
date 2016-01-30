package com.example.kristenvondrak.dartmouth.Diary;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.kristenvondrak.dartmouth.Parse.Recipe;
import com.example.kristenvondrak.dartmouth.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class MyFoodsFragment extends Fragment {

    private Activity m_Activity;
    private ListView m_ListView;
    private List<Recipe> m_RecipesList;
    private MyFoodsListAdapter m_RecipesListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_myfoods, container, false);

        m_Activity = getActivity();

        initializeViews(v);
        initializeListeners();

        // Get most recent recipes from Parse
        m_RecipesList = new ArrayList<>();
        m_RecipesListAdapter = new MyFoodsListAdapter(m_Activity, this, m_RecipesList);
        m_ListView.setAdapter(m_RecipesListAdapter);
        update();
        return v;
    }

    private void initializeViews(View v) {
       m_ListView = (ListView) v.findViewById(R.id.recents_listview);

    }



    private void initializeListeners() {

    }



    private void updateView(View v) {
        v.invalidate();
        v.requestLayout();
    }


    public void update() {
        new ParseRecipesRequest().execute();
    }




    private class ParseRecipesRequest extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {


            ParseQuery<ParseObject> query = ParseQuery.getQuery("Recipe");
            query.whereEqualTo("createdBy", ParseUser.getCurrentUser());
            query.orderByDescending("createdAt");
            query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
            query.findInBackground(new FindCallback<ParseObject>() {

                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        for (ParseObject object : objects) {
                            Recipe recipe = (Recipe) object;
                            m_RecipesList.add(recipe);
                        }
                    } else {
                        Log.d("ERROR:", e.getMessage());
                        // TODO: manage error
                    }
                }
            });

            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            m_RecipesListAdapter.notifyDataSetChanged();
            if (m_RecipesList.isEmpty()) {
                // set to gone
            }
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}

    }



}