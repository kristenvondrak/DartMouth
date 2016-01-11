package com.example.kristenvondrak.dartmouth.Preferences;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kristenvondrak.dartmouth.R;


public class PrefsFragment extends Fragment {
    TextView tv;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_prefs, container, false);

       /* tv = (TextView) v.findViewById(R.id.text);
        DatabaseHandler db = new DatabaseHandler(getActivity());
        List<String> venues = db.getVenueIDs();
        String text = "Total venues: " + Integer.toString(venues.size()) + "\n";
        for (String i : venues) {
            text += i + "\n";
        }
        tv.setText(text);*/
        return v;
    }
}