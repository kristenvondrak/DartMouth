package com.example.kristenvondrak.dartmouth.Diary;


import android.annotation.TargetApi;
import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.example.kristenvondrak.dartmouth.Database.DatabaseFragment;
import com.example.kristenvondrak.dartmouth.Main.SearchHeader;
import com.example.kristenvondrak.dartmouth.Main.Utils;
import com.example.kristenvondrak.dartmouth.Menu.MenuFragment;
import com.example.kristenvondrak.dartmouth.MyFoods.MyFoodsFragment;
import com.example.kristenvondrak.dartmouth.MyMeals.MyMealsFragment;
import com.example.kristenvondrak.dartmouth.Stats.ProgressFragment;
import com.example.kristenvondrak.dartmouth.R;
import com.example.kristenvondrak.dartmouth.Recents.RecentsFragment;

import java.util.Calendar;
import java.util.HashMap;

public class AddUserMealActivity extends ActionBarActivity {

    private Activity m_Activity;
    public boolean SEARCH_MODE = false;

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
    private ImageView m_ClearSearchBtn;

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
        m_Activity = this;
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
        m_TabToFragmentMap.put(m_TabFoods, new MyFoodsFragment());
        m_TabToFragmentMap.put(m_TabMeals, new MyMealsFragment());
        m_TabToFragmentMap.put(m_TabDb, new DatabaseFragment());
        m_FragmentManager = getSupportFragmentManager();

        for (RelativeLayout tab : m_TabToFragmentMap.keySet()) {
            tab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // Change highlight
                    setTabHighlight(m_CurrentTab, false);
                    setTabHighlight(view, true);
                    m_CurrentTab = (RelativeLayout) view;

                    // Exit search
                    if (SEARCH_MODE)
                        exitSearch();


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
        m_SearchBtn = (ImageView) findViewById(R.id.header_search_btn);
        m_SearchEditText = (EditText) findViewById(R.id.header_search_edittext);
        m_CancelSearchBtn = (TextView) findViewById(R.id.header_search_cancel_btn);
        m_ClearSearchBtn = (ImageView) findViewById(R.id.header_clear_search_btn);
        m_BackBtn = (LinearLayout) findViewById(R.id.header_back_to_diary_btn);
        m_CancelBtn = (TextView) findViewById(R.id.header_cancel_btn);
        m_AddBtn = (TextView) findViewById(R.id.header_add_btn);
    }

    private void initializeListeners() {
        m_BackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_Activity.onBackPressed();
            }
        });

        m_SearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSearch();

                // Fragment specific
                SearchHeader currentFragment = (SearchHeader) m_TabToFragmentMap.get(m_CurrentTab);
                currentFragment.onSearchClick();
            }
        });

        m_CancelSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitSearch();

                // Fragment specific
                SearchHeader currentFragment = (SearchHeader) m_TabToFragmentMap.get(m_CurrentTab);
                currentFragment.onCancelSearchClick();
            }
        });

        m_ClearSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear the text but do not exit search
                m_SearchEditText.setText("");

                // Fragment specific
                SearchHeader currentFragment = (SearchHeader) m_TabToFragmentMap.get(m_CurrentTab);
                currentFragment.onClearSearchClick();
            }
        });

        m_SearchEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = getSearchText();
                if (text != null) {
                    m_ClearSearchBtn.setVisibility(View.VISIBLE);
                    SearchHeader currentFragment = (SearchHeader) m_TabToFragmentMap.get(m_CurrentTab);
                    currentFragment.onSearchEditTextChanged(text, start, before);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        m_SearchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    SearchHeader currentFragment = (SearchHeader) m_TabToFragmentMap.get(m_CurrentTab);
                    currentFragment.onEnterClick();
                    Utils.hideKeyboard(m_Activity);
                    return true;
                }
                return false;
            }
        });

    }

    private void flipHeaderToNext() {
        m_HeaderViewFlipper.setInAnimation(this, R.anim.slide_in_from_right);
        m_HeaderViewFlipper.setOutAnimation(this, R.anim.slide_out_to_left);
        m_HeaderViewFlipper.showNext();
    }

    private void flipHeaderToPrev() {
        m_HeaderViewFlipper.setInAnimation(this, R.anim.slide_in_from_left);
        m_HeaderViewFlipper.setOutAnimation(this, R.anim.slide_out_to_right);
        m_HeaderViewFlipper.showPrevious();
    }

    public void cancelSearchIfExists() {
        if (SEARCH_MODE)
            m_CancelSearchBtn.callOnClick();

    }

    private void exitSearch() {
        Utils.hideKeyboard(m_Activity);

        SEARCH_MODE = false;
        m_SearchEditText.setText("");
        m_ClearSearchBtn.setVisibility(View.GONE);
        flipHeaderToPrev();
    }

    private void startSearch() {
        SEARCH_MODE = true;
        flipHeaderToNext();
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

    public ViewFlipper getViewFlipper() {
        return m_HeaderViewFlipper;
    }

    public ImageView getSearchBtn() {
        return m_SearchBtn;
    }

    public TextView getCancelSearchBtn() {
        return m_CancelSearchBtn;
    }

    public ImageView getClearSearchBtn() {
        return m_ClearSearchBtn;
    }

    public EditText getSearchEditText() {
        return m_SearchEditText;
    }

    public LinearLayout getBackBtn() { return m_BackBtn;}

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
        overridePendingTransition(R.anim.slide_out_to_bottom, R.anim.none);
    }

    public boolean inSearchMode() {
        return SEARCH_MODE;
    }

    public String getSearchText() {
        Object input = m_SearchEditText.getText();
        if (input == null || input.toString().trim().equals(""))
            return null;
        else
            return input.toString().toLowerCase().trim();
    }

}
