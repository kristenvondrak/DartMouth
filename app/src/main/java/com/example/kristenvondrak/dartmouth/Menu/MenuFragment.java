package com.example.kristenvondrak.dartmouth.Menu;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.kristenvondrak.dartmouth.Main.Constants;
import com.example.kristenvondrak.dartmouth.Parse.ParseAPI;
import com.example.kristenvondrak.dartmouth.Parse.Recipe;
import com.example.kristenvondrak.dartmouth.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class MenuFragment extends Fragment {

    private Activity m_Activity;

    // Date
    private Calendar m_CurrentDate;
    private ImageView m_NextDateButton;
    private ImageView m_PreviousDateButton;
    private TextView m_CurrentDateTextView;
    public static final String DATE_FORMAT = "EEEE, LLLL d";

    // Venue Tabs
    private LinearLayout m_VenueTabLinearLayout;
    private View m_CurrentVenue;

    // Meal Time Tabs
    private LinearLayout m_MealTimesTabLinearLayout;
    private View m_CurrentMealTime;

    // Menu Tabs
    private HorizontalScrollView m_MenuTabsScrollView;
    private LinearLayout m_MenuTabsLinearLayout;
    private View m_CurrentMenu;

    // Food Items
    private List<Recipe> m_FoodItemsList;
    private FoodItemListAdapter m_FoodItemListAdapter;
    private ListView m_FoodItemsListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_CurrentDate = Calendar.getInstance();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_menu, container, false);
        m_Activity = getActivity();

        m_NextDateButton = (ImageView) v.findViewById(R.id.next_date_btn);
        m_PreviousDateButton = (ImageView) v.findViewById(R.id.prev_date_btn);
        m_CurrentDateTextView = (TextView) v.findViewById(R.id.date_text_view);
        m_VenueTabLinearLayout = (LinearLayout) v.findViewById(R.id.venue_tabs_ll);
        m_MealTimesTabLinearLayout = (LinearLayout) v.findViewById(R.id.mealtime_tabs_ll);
        m_MenuTabsScrollView = (HorizontalScrollView) v.findViewById(R.id.category_tabs_scroll);
        m_MenuTabsLinearLayout = (LinearLayout) v.findViewById(R.id.category_tabs_ll);
        m_FoodItemsListView = (ListView) v.findViewById(R.id.food_items_list_view);

        m_NextDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_CurrentDate.add(Calendar.DATE, 1);
                changeInTime();
            }
        });

        m_PreviousDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_CurrentDate.add(Calendar.DATE, -1);
                changeInTime();
            }
        });

        for (Constants.Venue venue : Constants.Venue.values()) {
            ViewGroup tab = (ViewGroup)inflater.inflate(R.layout.custom_tab, null);
            ((TextView)tab.findViewById(R.id.tab_text)).setText(venue.name());
            tab.setOnClickListener(venueTabOnClickListener());
            setHighlight(tab, false);
            tab.setTag(venue);
            m_VenueTabLinearLayout.addView(tab);
        }
        m_CurrentVenue = m_VenueTabLinearLayout.getChildAt(0);

        m_FoodItemsList = new ArrayList<>();
        m_FoodItemListAdapter = new FoodItemListAdapter(m_Activity, m_FoodItemsList);
        m_FoodItemsListView.setAdapter(m_FoodItemListAdapter);

        changeInVenue();
        return v;
    }


    public void changeInVenue() {

        // Update meal times
        boolean found = false;
        m_MealTimesTabLinearLayout.removeAllViews();
        LayoutInflater inflater = m_Activity.getLayoutInflater();
        for (Constants.MealTime mealTime : Constants.mealTimesForVenue.get(m_CurrentVenue)) {
            ViewGroup tab = (ViewGroup)inflater.inflate(R.layout.custom_tab, null);
            ((TextView)tab.findViewById(R.id.tab_text)).setText(mealTime.name());
            tab.setOnClickListener(mealTimeTabOnClickListener());

            if (mealTime == m_CurrentMealTime.getTag()) {
                found = true;
                setHighlight(tab, true);
            } else {
                setHighlight(tab, false);
            }
            tab.setTag(mealTime);
            m_MealTimesTabLinearLayout.addView(tab);
           // m_MealTimesTabsList.add(tab);
        }

        if (!found) {
            m_CurrentMealTime = m_MealTimesTabLinearLayout.getChildAt(0);
            setHighlight(m_CurrentMealTime, true);
        }

        // Update menus
        found = false;
        m_MenuTabsLinearLayout.removeAllViews();
        for (Constants.Menu menu : Constants.menusForVenue.get(m_CurrentVenue)) {
            ViewGroup tab = (ViewGroup)inflater.inflate(R.layout.custom_tab, null);
            ((TextView)tab.findViewById(R.id.tab_text)).setText(menu.name());
            tab.setOnClickListener(menuTabOnClickListener());

            if (menu == m_CurrentMenu.getTag()) {
                found = true;
                setHighlight(tab, true);
            } else {
                setHighlight(tab, false);
            }
            tab.setTag(menu);
            m_MenuTabsLinearLayout.addView(tab);
        }


        if (!found) {
            m_CurrentMenu = m_MenuTabsLinearLayout.getChildAt(0);
            setHighlight(m_CurrentMenu, true);
        }

        update();
    }

    public void changeInTime() {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        m_CurrentDateTextView.setText(sdf.format(m_CurrentDate.getTime()));
        update();
    }


    public void update() {
        m_FoodItemsList = ParseAPI.recipesFromCloudForDate(m_CurrentDate, (String)m_CurrentVenue.getTag(),
                            (String) m_CurrentMealTime.getTag(), (String) m_CurrentMenu.getTag());
        m_FoodItemListAdapter.notifyDataSetChanged();
    }

    private View.OnClickListener mealTimeTabOnClickListener() {
        return (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setHighlight(m_CurrentMealTime, false);
                m_CurrentMealTime = v;
                setHighlight(v, true);
                update();
            }
        });
    }

    private View.OnClickListener menuTabOnClickListener() {
        return (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setHighlight(m_CurrentMenu, false);
                m_CurrentMenu = v;
                setHighlight(v, true);
                update();
            }
        });
    }

    private View.OnClickListener venueTabOnClickListener() {
        return (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setHighlight(m_CurrentVenue, false);
                m_CurrentVenue = v;
                setHighlight(v, true);
                changeInVenue();
            }
        });
    }

    public void setHighlight(View v, boolean highlight) {
        if (highlight) {
            v.findViewById(R.id.tab_highlight).setBackgroundColor(getResources().getColor(R.color.tab_highlight));
        } else {
            v.findViewById(R.id.tab_highlight).setBackgroundColor(getResources().getColor(R.color.transparent));
        }
    }



}