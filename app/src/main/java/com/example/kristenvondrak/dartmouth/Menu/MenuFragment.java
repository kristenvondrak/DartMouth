package com.example.kristenvondrak.dartmouth.Menu;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.example.kristenvondrak.dartmouth.Main.Constants;
import com.example.kristenvondrak.dartmouth.Parse.Offering;
import com.example.kristenvondrak.dartmouth.Parse.Recipe;
import com.example.kristenvondrak.dartmouth.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class MenuFragment extends Fragment {

    private Activity m_Activity;

    // Header
    private ViewFlipper m_HeaderViewFlipper;

    // Date
    private Calendar m_CurrentDate;
    private ImageView m_NextDateButton;
    private ImageView m_PreviousDateButton;
    private TextView m_CurrentDateTextView;
    public static final String DATE_FORMAT = "EEE, LLL d";
    private ImageView m_SearchBtn;

    // Search
    private EditText m_SearchEditText;
    private TextView m_CancelSearchBtn;

    // Venue Tabs
    private TableRow m_VenueTabs;
    private View m_CurrentVenue;

    // Meal Time Tabs
    private TableRow m_MealTimesTabs;
    private View m_CurrentMealTime;

    // Menu Tabs
    private LinearLayout m_MenuTabsLinearLayout;
    private View m_CurrentMenu;

    // Food Items
    private List<Recipe> m_FoodItemsList;
    private FoodItemListAdapter m_FoodItemListAdapter;
    private ListView m_FoodItemsListView;
    private TextView m_EmptyFoodItemsText;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_menu, container, false);
        m_Activity = getActivity();
        initializeViews(v);
        initializeListeners();

        for (Constants.Venue venue : Constants.Venue.values()) {
            ViewGroup tab = (ViewGroup)inflater.inflate(R.layout.custom_tab, null);
            ((TextView)tab.findViewById(R.id.tab_text)).setText(Constants.venueDisplayStrings.get(venue));
            tab.setOnClickListener(venueTabOnClickListener());
            setHighlight(tab, false);
            tab.setTag(venue.name());
            m_VenueTabs.addView(tab);
        }

        updateView(m_VenueTabs);
        m_CurrentVenue = m_VenueTabs.getChildAt(0);
        setHighlight(m_CurrentVenue, true);
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        m_CurrentDateTextView.setText(sdf.format(m_CurrentDate.getTime()));

        m_FoodItemsList = new ArrayList<>();
        m_FoodItemListAdapter = new FoodItemListAdapter(m_Activity, m_FoodItemsList, m_CurrentDate);
        m_FoodItemsListView.setAdapter(m_FoodItemListAdapter);

        changeInVenue();
        return v;
    }

    private void initializeViews(View v) {
        m_NextDateButton = (ImageView) v.findViewById(R.id.next_date_btn);
        m_PreviousDateButton = (ImageView) v.findViewById(R.id.prev_date_btn);
        m_CurrentDateTextView = (TextView) v.findViewById(R.id.date_text_view);
        m_VenueTabs = (TableRow) v.findViewById(R.id.venue_tabs_row);
        m_MealTimesTabs = (TableRow) v.findViewById(R.id.mealtime_tabs_row);
        m_MenuTabsLinearLayout = (LinearLayout) v.findViewById(R.id.category_tabs_ll);
        m_FoodItemsListView = (ListView) v.findViewById(R.id.food_items_list_view);
        m_HeaderViewFlipper = (ViewFlipper) v.findViewById(R.id.menu_header_view_flipper);
        m_SearchBtn = (ImageView) v.findViewById(R.id.date_search_btn);
        m_CancelSearchBtn = (TextView) v.findViewById(R.id.search_cancel_btn);
        m_SearchEditText = (EditText) v.findViewById(R.id.search_edittext);
        m_EmptyFoodItemsText = (TextView) v.findViewById(R.id.empty_food_list);
        m_CurrentDate = Calendar.getInstance();
    }

    private void initializeListeners() {
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

        m_SearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Display next screen
                m_HeaderViewFlipper.setInAnimation(m_Activity, R.anim.slide_in_from_right);
                m_HeaderViewFlipper.setOutAnimation(m_Activity, R.anim.slide_out_to_left);
                m_HeaderViewFlipper.showNext();
            }
        });

        m_CancelSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Exit search and restore previous items
                update();

                // Hide the keyboard
                View view = m_Activity.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm =
                            (InputMethodManager)m_Activity.getSystemService(m_Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    // TODO: do we want to restore the items before search??
                }
                // Clear the search
                m_SearchEditText.setText("");

                // Display previous screen
                m_HeaderViewFlipper.setInAnimation(m_Activity, R.anim.slide_in_from_left);
                m_HeaderViewFlipper.setOutAnimation(m_Activity, R.anim.slide_out_to_right);
                m_HeaderViewFlipper.showPrevious();

            }
        });

        m_SearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Object input = m_SearchEditText.getText();
                if (input != null) {
                    ArrayList<Recipe> searchResults = new ArrayList<>();
                    String text = input.toString().toLowerCase().trim();
                    for (Recipe r : m_FoodItemsList) {
                        String name = r.getName().toLowerCase();
                        if (name.contains(text)) {
                            searchResults.add(r);
                        }
                    }

                    m_FoodItemsList.clear();
                    for (Recipe r: searchResults) {
                        m_FoodItemsList.add(r);
                    }
                    m_FoodItemListAdapter.notifyDataSetChanged();

                    setHighlight(m_CurrentMenu, false);
                    m_CurrentMenu = m_MenuTabsLinearLayout.getChildAt(0);
                    setHighlight(m_CurrentMenu, true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        for (int i = 0; i < m_MenuTabsLinearLayout.getChildCount(); i++) {
            final int index = i;
            final View tv =  m_MenuTabsLinearLayout.getChildAt(i).findViewById(R.id.tab_text);
            tv.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                                                                   @Override
                                                                   public void onGlobalLayout() {
                                                                       View v = m_MenuTabsLinearLayout.getChildAt(index).findViewById(R.id.tab_highlight);
                                                                       v.setMinimumWidth(tv.getWidth());
                                                                       v.invalidate();
                                                                       v.requestLayout();

                                                                   }
                                                               });
        }

    }

    public void changeInVenue() {

        // Update meal times
        boolean found = false;
        m_MealTimesTabs.removeAllViews();
        LayoutInflater inflater = m_Activity.getLayoutInflater();

        Constants.Venue newVenue = Constants.Venue.valueOf(m_CurrentVenue.getTag().toString());
        for (Constants.MealTime mealTime : Constants.mealTimesForVenue.get(newVenue)) {
            ViewGroup tab = (ViewGroup)inflater.inflate(R.layout.custom_tab, null);
            ((TextView)tab.findViewById(R.id.tab_text)).setText(Constants.mealTimeDisplayStrings.get(mealTime));
            tab.setOnClickListener(mealTimeTabOnClickListener());

            if (m_CurrentMealTime != null && mealTime.name().equalsIgnoreCase(m_CurrentMealTime.getTag().toString())) {
                found = true;
                m_CurrentMealTime = tab;
                setHighlight(tab, true);
            } else {
                setHighlight(tab, false);
            }
            tab.setTag(mealTime);
            m_MealTimesTabs.addView(tab);
        }

        if (!found) {
            m_CurrentMealTime = m_MealTimesTabs.getChildAt(0);
            setHighlight(m_CurrentMealTime, true);
        }
        updateView(m_MealTimesTabs);

        // Update menus
        found = false;
        m_MenuTabsLinearLayout.removeAllViews();
        for (Constants.Menu menu : Constants.menusForVenue.get(newVenue)) {
            final ViewGroup tab = (ViewGroup)inflater.inflate(R.layout.custom_tab_scrollview, null);
            ((TextView)tab.findViewById(R.id.tab_text)).setText(Constants.menuDisplayStrings.get(menu));
            tab.setOnClickListener(menuTabOnClickListener());

            if (m_CurrentMenu != null && menu.name().equals(m_CurrentMenu.getTag().toString())) {
                m_CurrentMenu = tab;
                found = true;
                setHighlight(tab, true);
            } else {
                setHighlight(tab, false);
            }
            tab.setTag(menu);

            // Need this
            tab.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    View v = tab.findViewById(R.id.tab_highlight);
                    int width = (tab.findViewById(R.id.tab_text)).getWidth();
                    v.setMinimumWidth((int)(width * 1.15));
                    v.invalidate();
                    v.requestLayout();

                }
            });

            m_MenuTabsLinearLayout.addView(tab);
        }

        if (!found) {
            m_CurrentMenu = m_MenuTabsLinearLayout.getChildAt(0);
            setHighlight(m_CurrentMenu, true);
        }
        updateView(m_MenuTabsLinearLayout);

        update();
    }

    private void updateView(View v) {
        v.invalidate();
        v.requestLayout();
    }


    public void changeInTime() {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        m_CurrentDateTextView.setText(sdf.format(m_CurrentDate.getTime()));
        update();
    }


    public void update() {
        m_FoodItemListAdapter.setMealTime((Constants.MealTime.valueOf(m_CurrentMealTime.getTag().toString())));
        String venue = Constants.venueParseStrings.get(Constants.Venue.valueOf(m_CurrentVenue.getTag().toString()));
        String mealtime = Constants.mealTimeParseStrings.get(Constants.MealTime.valueOf(m_CurrentMealTime.getTag().toString()));
        String menu = Constants.menuParseStrings.get(Constants.Menu.valueOf(m_CurrentMenu.getTag().toString()));

        String day = Integer.toString(m_CurrentDate.get(Calendar.DAY_OF_MONTH));
        String month = Integer.toString(m_CurrentDate.get(Calendar.MONTH) + 1);
        String year = Integer.toString(m_CurrentDate.get(Calendar.YEAR));

        new ParseRecipesRequest().execute(day, month, year, venue, mealtime, menu);

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
            ((TextView)v.findViewById(R.id.tab_text)).setTypeface(Typeface.DEFAULT_BOLD);
        } else {
            v.findViewById(R.id.tab_highlight).setBackgroundColor(getResources().getColor(R.color.transparent));
            ((TextView)v.findViewById(R.id.tab_text)).setTypeface(Typeface.DEFAULT);
        }
        v.findViewById(R.id.tab_highlight).invalidate();
        v.findViewById(R.id.tab_text).invalidate();
        v.findViewById(R.id.tab_highlight).requestLayout();
        v.findViewById(R.id.tab_text).requestLayout();
    }

    private class ParseRecipesRequest extends AsyncTask<String, Void, String> {

        final List<Recipe> recipesList = new ArrayList<>();

        @Override
        protected String doInBackground(String... params) {

            int day = Integer.parseInt(params[0]);
            int month = Integer.parseInt(params[1]);
            int year = Integer.parseInt(params[2]);
            String venueKey = params[3];
            String mealName = params[4];
            String menuName = params[5];

            ParseQuery<ParseObject> query = ParseQuery.getQuery("Offering");
            query.whereEqualTo("month", month);
            query.whereEqualTo("day", day);
            query.whereEqualTo("year", year);


            if (venueKey != null) {
                query.whereEqualTo("venueKey", venueKey);
            }
            if (mealName != null) {
                query.whereEqualTo("mealName", mealName);
            }
            if (menuName != null) {
                query.whereEqualTo("menuName", menuName);
            }

            // query.orderByAscending
            query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> offeringsList, ParseException e) {
                    if (e == null) {
                        List<ParseQuery<ParseObject>> queryList = new ArrayList<ParseQuery<ParseObject>>();
                        for (ParseObject object : offeringsList) {
                            Offering offering = (Offering) object;
                            ParseRelation<ParseObject> relation = offering.getRecipes();
                            ParseQuery q = relation.getQuery();
                            queryList.add(q);
                        }

                        if (queryList.isEmpty()) {
                            updateRecipeList();
                            return;
                        }

                        ParseQuery<ParseObject> recipesQuery = ParseQuery.or(queryList);
                        recipesQuery.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
                        recipesQuery.orderByAscending("name");
                        recipesQuery.setLimit(1000);

                        recipesQuery.findInBackground(new FindCallback<ParseObject>() {
                            public void done(List<ParseObject> list, ParseException e) {
                                if (e == null) {
                                    for (ParseObject object : list) {
                                        recipesList.add((Recipe) object);
                                    }
                                }
                                updateRecipeList();
                                Log.d("onPostExecute",Integer.toString(m_FoodItemsList.size()));
                            }
                        });
                    } else {
                        // error
                    }
                }
            });
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {

        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}

        private void updateRecipeList() {
            m_FoodItemsList.clear();
            for (Recipe r : recipesList)
                m_FoodItemsList.add(r);
            m_FoodItemListAdapter.notifyDataSetChanged();

            if (m_FoodItemsList.isEmpty()) {
                m_EmptyFoodItemsText.setVisibility(View.VISIBLE);
            } else {
                m_EmptyFoodItemsText.setVisibility(View.GONE);
            }
        }
    }


}