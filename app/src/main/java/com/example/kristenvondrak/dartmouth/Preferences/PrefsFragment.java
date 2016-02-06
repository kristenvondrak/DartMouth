package com.example.kristenvondrak.dartmouth.Preferences;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kristenvondrak.dartmouth.Main.LoginActivity;
import com.example.kristenvondrak.dartmouth.Main.SignupActivity;
import com.example.kristenvondrak.dartmouth.Parse.ParseAPI;
import com.example.kristenvondrak.dartmouth.R;


public class PrefsFragment extends Fragment {
    TextView tv;
    Activity me;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_prefs, container, false);
        me = getActivity();
        tv = (TextView) v.findViewById(R.id.text);
        tv.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               ParseAPI.logOutParseUser();
               Intent intent = new Intent(me, LoginActivity.class);
               me.startActivity(intent);
           }
        });
        return v;
    }
}