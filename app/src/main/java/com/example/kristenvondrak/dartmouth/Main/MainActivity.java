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
import com.example.kristenvondrak.dartmouth.R;


public class MainActivity extends FragmentActivity {
    private Context m_Me;
    private FragmentTabHost m_FragmentTabHost;
    private int m_TabCount = 4;
    public static final int LOGIN_ACTIVITY_REQUEST = 1;
    public static final int ADD_TO_MEAL = 2;
    public static final int EDIT_DIARY_ENTRY = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_Me = this;
        setContentView(R.layout.activity_main);

        m_FragmentTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);

        m_FragmentTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);

        m_FragmentTabHost.addTab(m_FragmentTabHost.newTabSpec("menu")
                        .setIndicator(getTabIndicator(m_FragmentTabHost.getContext(), R.drawable.main_menu, "Menu")),
                MenuFragment.class, null);

        m_FragmentTabHost.addTab(m_FragmentTabHost.newTabSpec("diary")
                        .setIndicator(getTabIndicator(m_FragmentTabHost.getContext(), R.drawable.main_diary, "Diary")),
                DiaryFragment.class, null);

        m_FragmentTabHost.addTab(m_FragmentTabHost.newTabSpec("stats")
                        .setIndicator(getTabIndicator(m_FragmentTabHost.getContext(), R.drawable.main_stats, "Stats")),
                PrefsFragment.class, null);

        m_FragmentTabHost.addTab(m_FragmentTabHost.newTabSpec("prefs")
                        .setIndicator(getTabIndicator(m_FragmentTabHost.getContext(), R.drawable.main_prefs, "Prefs")),
                PrefsFragment.class, null);

        updateTabs();
        m_FragmentTabHost.getTabWidget().setDividerDrawable(null);
        m_FragmentTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                updateTabs();
            }

            ;
        });

    }


    private void updateTabs() {
        for (int i = 0; i < m_TabCount; i++) {
            if (i == m_FragmentTabHost.getCurrentTab())
                changeTabColor(i, true);
            else
                changeTabColor(i, false);
        }
    }
    private void changeTabColor(int tab, boolean selected) {
        TextView tv = (TextView) m_FragmentTabHost.getTabWidget().getChildTabViewAt(tab).findViewById(R.id.tab_text);
        ImageView iv = (ImageView) m_FragmentTabHost.getTabWidget().getChildTabViewAt(tab).findViewById(R.id.tab_image);
        if (selected) {
            tv.setTextColor(getResources().getColor(R.color.main));
            iv.setColorFilter(getResources().getColor(R.color.main));
        } else {
            tv.setTextColor(getResources().getColor(R.color.gray_text));
            iv.setColorFilter(getResources().getColor(R.color.gray_text));
        }

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
        switch (requestCode) {
            case LOGIN_ACTIVITY_REQUEST:
                return;
            case ADD_TO_MEAL:
                return;
            case EDIT_DIARY_ENTRY:
                return;
            default:
                return;
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
