package com.example.kristenvondrak.dartmouth.Diary;


import android.annotation.TargetApi;
import android.app.Activity;

import android.content.Intent;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.example.kristenvondrak.dartmouth.Main.Constants;
import com.example.kristenvondrak.dartmouth.Menu.MenuFragment;
import com.example.kristenvondrak.dartmouth.Parse.Recipe;
import com.example.kristenvondrak.dartmouth.Progress.ProgressFragment;
import com.example.kristenvondrak.dartmouth.R;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class AddUserMealActivity extends ActionBarActivity {
    private Activity m_Me;

    // Tabs
    private TableLayout m_MainTabs;
    private RelativeLayout m_TabDDS;
    private RelativeLayout m_TabRecents;
    private RelativeLayout m_TabFoods;
    private RelativeLayout m_TabMeals;
    private RelativeLayout m_TabDb;

    // Fragments
    private RelativeLayout m_CurrentTab;
    private FragmentManager m_FragmentManager;
    private HashMap<RelativeLayout, Fragment> m_TabToFragmentMap;
    private FrameLayout m_TabContent;

    // Header
    private ViewFlipper m_HeaderViewFlipper;
    private LinearLayout m_BackBtn;
    private ImageView m_SearchBtn;
    private EditText m_SearchEditText;
    private TextView m_CancelSearchBtn;

    // Alternative Header
    private TextView m_CancelBtn;
    private TextView m_AddBtn;

    private String m_SelectedUserMeal;
    private Calendar m_Calendar;

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user_meal);
        m_Me = this;
        initializeViews();
        initializeListeners();

        Intent intent = getIntent();
        m_SelectedUserMeal = intent.getStringExtra(DiaryFragment.EXTRA_MEALTIME);

        m_Calendar = Calendar.getInstance();
        m_Calendar.setTimeInMillis(intent.getLongExtra(DiaryFragment.EXTRA_DATE, m_Calendar.getTimeInMillis()));

        // Initialize tabs and fragments
        m_CurrentTab = m_TabDDS;
        m_TabToFragmentMap = new HashMap<>();
        m_TabToFragmentMap.put(m_TabDDS, new MenuFragment());
        m_TabToFragmentMap.put(m_TabRecents, new RecentsFragment());
        m_TabToFragmentMap.put(m_TabFoods, new MyFoodsFragment()); // Placeholder
        m_TabToFragmentMap.put(m_TabMeals, new MyMealsFragment());
        m_TabToFragmentMap.put(m_TabDb, new ProgressFragment()); // Placeholder
        m_FragmentManager = getSupportFragmentManager();

        for (RelativeLayout tab : m_TabToFragmentMap.keySet()) {
            tab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // Change highlight
                    setTabHighlight(m_CurrentTab, false);
                    setTabHighlight(view, true);
                    m_CurrentTab = (RelativeLayout) view;

                    // Change the content
                    FragmentTransaction ft = m_FragmentManager.beginTransaction();
                    ft.replace(m_TabContent.getId(), m_TabToFragmentMap.get(m_CurrentTab)).commit();
                }
            });
        }

        // Set start tab
        m_CurrentTab.callOnClick();
    }

    private void initializeViews() {
        m_MainTabs = (TableLayout) findViewById(R.id.header_tabs);
        m_TabDDS = (RelativeLayout) findViewById(R.id.tab_dds);
        m_TabRecents = (RelativeLayout) findViewById(R.id.tab_recents);
        m_TabFoods = (RelativeLayout) findViewById(R.id.tab_foods);
        m_TabMeals = (RelativeLayout) findViewById(R.id.tab_meals);
        m_TabDb = (RelativeLayout) findViewById(R.id.tab_db);
        m_TabContent = (FrameLayout) findViewById(R.id.tab_content);
        m_HeaderViewFlipper = (ViewFlipper) findViewById(R.id.header_view_flipper);
        m_SearchBtn = (ImageView) findViewById(R.id.search_btn);
        m_SearchEditText = (EditText) findViewById(R.id.search_edittext);
        m_CancelSearchBtn = (TextView) findViewById(R.id.search_cancel_btn);
        m_BackBtn = (LinearLayout) findViewById(R.id.back_to_diary_btn);
        m_CancelBtn = (TextView) findViewById(R.id.header_cancel_btn);
        m_AddBtn = (TextView) findViewById(R.id.header_add_btn);
    }

    private void initializeListeners() {
        m_BackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_Me.onBackPressed();
            }
        });
    }

    public ViewFlipper getViewFlipper() {
        return m_HeaderViewFlipper;
    }

    public ImageView getSearchBtn() {
        return m_SearchBtn;
    }

    public TextView getCancelSearchBtn() {
        return m_CancelSearchBtn;
    }

    public EditText getSearchEditText() {
        return m_SearchEditText;
    }

    public LinearLayout getBackBtn() {
        return m_BackBtn;
    }

    private void setTabHighlight(View view, boolean highlight) {
        TextView tv = (TextView) view.findViewById(R.id.tab_text);

        if (highlight) {
            tv.setTextColor(getResources().getColor(R.color.white));
            view.setBackgroundColor(getResources().getColor(R.color.main));
        } else {
            tv.setTextColor(getResources().getColor(R.color.main));
            view.setBackgroundColor(getResources().getColor(R.color.white));
        }
    }

    public TableLayout getMainTabs() {
        return m_MainTabs;
    }

    public String getUserMeal() {
        return m_SelectedUserMeal;
    }

    public TextView getCancelBtn() {
        return m_CancelBtn;
    }

    public TextView getAddBtn() {
        return m_AddBtn;
    }

    public Calendar getCalendar() {
        return m_Calendar;
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
    @Override
    public void onBackPressed(){
        this.finish();
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }

    public void showAlternativeHeader() {
        m_BackBtn.setVisibility(View.GONE);
        m_SearchBtn.setVisibility(View.GONE);
        m_MainTabs.setVisibility(View.GONE);

        m_AddBtn.setVisibility(View.VISIBLE);
        m_CancelBtn.setVisibility(View.VISIBLE);
    }

    public void showMainHeader() {
        m_AddBtn.setVisibility(View.GONE);
        m_CancelBtn.setVisibility(View.GONE);

        m_BackBtn.setVisibility(View.VISIBLE);
        m_SearchBtn.setVisibility(View.VISIBLE);
        m_MainTabs.setVisibility(View.VISIBLE);
    }


}
