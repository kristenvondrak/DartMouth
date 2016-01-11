package com.example.kristenvondrak.dartmouth.Main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.example.kristenvondrak.dartmouth.Diary.DiaryFragment;
import com.example.kristenvondrak.dartmouth.Menu.MenuFragment;
import com.example.kristenvondrak.dartmouth.Parse.ParseAPI;
import com.example.kristenvondrak.dartmouth.Preferences.PrefsFragment;
import com.example.kristenvondrak.dartmouth.Progress.ProgressFragment;
import com.example.kristenvondrak.dartmouth.R;


public class MainActivity extends FragmentActivity {
    private Context m_Me;
    private FragmentTabHost m_FragmentTabHost;
    private int m_TabCount = 4;
    static final int LOGIN_ACTIVITY_REQUEST = 1;  // The request code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_Me = this;
        setContentView(R.layout.activity_main);


        if (ParseAPI.getCurrentParseUser() == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            this.startActivityForResult(intent, LOGIN_ACTIVITY_REQUEST);
        }


        m_FragmentTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        m_FragmentTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);

        m_FragmentTabHost.addTab(m_FragmentTabHost.newTabSpec("diary")
                        .setIndicator(getTabIndicator(m_FragmentTabHost.getContext(), R.drawable.diary_selector, "Diary")),
                DiaryFragment.class, null);
        m_FragmentTabHost.addTab(m_FragmentTabHost.newTabSpec("progress")
                        .setIndicator(getTabIndicator(m_FragmentTabHost.getContext(), R.drawable.progress_selector, "Progress")),
                ProgressFragment.class, null);
        m_FragmentTabHost.addTab(m_FragmentTabHost.newTabSpec("menu")
                        .setIndicator(getTabIndicator(m_FragmentTabHost.getContext(), R.drawable.menu_selector, "Menu")),
                MenuFragment.class, null);
        m_FragmentTabHost.addTab(m_FragmentTabHost.newTabSpec("prefs")
                        .setIndicator(getTabIndicator(m_FragmentTabHost.getContext(), R.drawable.prefs_selector, "Profile")),
                PrefsFragment.class, null);

        changeTabTextColor(m_FragmentTabHost.getCurrentTab(), Color.WHITE);
        m_FragmentTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                for (int i = 0; i < m_TabCount; i++) {
                    int color = getResources().getColor(R.color.light_gray);
                    if (i == m_FragmentTabHost.getCurrentTab())
                        color = Color.WHITE;
                    changeTabTextColor(i, color);
                }
            }

            ;
        });

        // Get the new menu info
    }


    private void changeTabTextColor(int tab, int color) {
        TextView tv = (TextView) m_FragmentTabHost.getTabWidget().getChildTabViewAt(tab).findViewById(R.id.tab_text);
        tv.setTextColor(color);
    }

    private View getTabIndicator(Context context, int img, String text) {
        View view = LayoutInflater.from(context).inflate(R.layout.main_tab_layout, null);
        ImageView iv = (ImageView) view.findViewById(R.id.tab_image);
        iv.setImageResource(img);
        TextView tv = (TextView) view.findViewById(R.id.tab_text);
        tv.setText(text);
        return view;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == LOGIN_ACTIVITY_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.

                // Do something with the contact here (bigger example below)
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.


        return super.onOptionsItemSelected(item);
    }
}
