package com.example.kristenvondrak.dartmouth.Menu;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.kristenvondrak.dartmouth.Diary.AddUserMealActivity;
import com.example.kristenvondrak.dartmouth.Main.Constants;
import com.example.kristenvondrak.dartmouth.Main.SearchHeader;
import com.example.kristenvondrak.dartmouth.Main.Utils;
import com.example.kristenvondrak.dartmouth.Parse.Offering;
import com.example.kristenvondrak.dartmouth.Parse.ParseAPI;
import com.example.kristenvondrak.dartmouth.Parse.Recipe;
import com.example.kristenvondrak.dartmouth.Parse.UserMeal;
import com.example.kristenvondrak.dartmouth.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class MenuFragment extends NutritionFragment implements SearchHeader{

    private enum MODE {MENU, DIARY};

    // Header
    private ViewFlipper m_HeaderViewFlipper;

    // Date
    private ImageView m_NextDateButton;
    private ImageView m_PreviousDateButton;
    private TextView m_CurrentDateTextView;
    private ImageView m_CalendarButton;
    private DatePickerDialog.OnDateSetListener m_DatePickerListener;

    // Search
    private boolean SEARCH_MODE = false;
    private ImageView m_SearchBtn;
    private EditText m_SearchEditText;
    private TextView m_CancelSearchBtn;
    private List<Recipe> m_RestoredList;

    // Main
    private ViewFlipper m_MenuContent;
    private ProgressBar m_ProgressSpinner;
    private MODE m_Mode;

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
    private List<Recipe> m_MenuItemsList;
    private MenuItemListAdapter m_MenuItemListAdapter;
    private ListView m_MenuItemsListView;
    private LinearLayout m_EmptyMenuText;

    // Diary Specific
    private LinearLayout m_BackToDiaryBtn;
    private TableLayout m_DiaryTabs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_menu, container, false);

        m_Activity = getActivity();
        m_Calendar = Calendar.getInstance();

        setMode();
        initializeViews(v);
        initializeListeners();

        // Create the venue tabs
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

        // Update the date
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT_DISPLAY, Locale.US);
        m_CurrentDateTextView.setText(sdf.format(m_Calendar.getTime()));

        // Create list of recipes and set the adapter
        m_MenuItemsList = new ArrayList<>();
        m_MenuItemListAdapter = new MenuItemListAdapter(m_Activity, this);
        m_MenuItemsListView.setAdapter(m_MenuItemListAdapter);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        changeInVenue();
    }



    // ------------------------------------------------------------------------- Views

    private void initializeViews(View v) {

        // Main
        m_ProgressSpinner = (ProgressBar) v.findViewById(R.id.progress_spinner);
        m_VenueTabs = (TableRow) v.findViewById(R.id.venue_tabs_row);
        m_MealTimesTabs = (TableRow) v.findViewById(R.id.mealtime_tabs_row);
        m_MenuTabsLinearLayout = (LinearLayout) v.findViewById(R.id.category_tabs_ll);
        m_MenuItemsListView = (ListView) v.findViewById(R.id.food_items_list_view);
        m_EmptyMenuText = (LinearLayout) v.findViewById(R.id.empty_food_list);
        m_MenuContent = (ViewFlipper) v.findViewById(R.id.menu_contents_viewflipper);

        // Nutrition
        m_RecipeNutrientsView = v.findViewById(R.id.nutrients);
        m_RecipeName = (TextView) v.findViewById(R.id.name);
        m_NumberPickerWhole = (NumberPicker) v.findViewById(R.id.servings_picker_number);
        m_NumberPickerFrac = (NumberPicker) v.findViewById(R.id.servings_picker_fraction);
        m_UserMealSelector = (LinearLayout) v.findViewById(R.id.usermeal_selector);
        m_HeaderViewFlipper = (ViewFlipper) v.findViewById(R.id.menu_header_view_flipper);

        // Date
        m_NextDateButton = (ImageView) v.findViewById(R.id.next_date_btn);
        m_PreviousDateButton = (ImageView) v.findViewById(R.id.prev_date_btn);
        m_CurrentDateTextView = (TextView) v.findViewById(R.id.date_text_view);
        m_CalendarButton = (ImageView) v.findViewById(R.id.header_calendar_btn);

        // Header
        m_AddBtn = (TextView) v.findViewById(R.id.header_add_btn);
        m_CancelBtn = (TextView) v.findViewById(R.id.header_cancel_btn);
        m_SearchBtn = (ImageView) v.findViewById(R.id.header_search_btn);
        m_CancelSearchBtn = (TextView) v.findViewById(R.id.header_search_cancel_btn);
        m_SearchEditText = (EditText)v.findViewById(R.id.header_search_edittext);

        if (m_Mode == MODE.DIARY) {

            // Do not show date
            m_HeaderViewFlipper.setVisibility(View.GONE);

            // Different search views
            AddUserMealActivity activity = (AddUserMealActivity) m_Activity;
            m_Calendar = activity.getCalendar();
            m_SelectedUserMeal = activity.getUserMeal();
            m_HeaderViewFlipper = activity.getViewFlipper();
            m_SearchBtn = activity.getSearchBtn();
            m_CancelSearchBtn = activity.getCancelSearchBtn();
            m_SearchEditText = activity.getSearchEditText();
            m_AddBtn = activity.getAddBtn();
            m_CancelBtn = activity.getCancelBtn();
            m_BackToDiaryBtn = activity.getBackBtn();
            m_DiaryTabs = activity.getMainTabs();
        }

        hideAddBtns();
    }


    private void flipHeaderToNext() {
        m_HeaderViewFlipper.setInAnimation(m_Activity, R.anim.slide_in_from_right);
        m_HeaderViewFlipper.setOutAnimation(m_Activity, R.anim.slide_out_to_left);
        m_HeaderViewFlipper.showNext();
    }

    private void flipHeaderToPrev() {
        m_HeaderViewFlipper.setInAnimation(m_Activity, R.anim.slide_in_from_left);
        m_HeaderViewFlipper.setOutAnimation(m_Activity, R.anim.slide_out_to_right);
        m_HeaderViewFlipper.showPrevious();
    }

    @Override
    protected void flipToPrev() {
        hideAddBtns();
        m_MenuContent.setInAnimation(m_Activity, R.anim.slide_in_from_left);
        m_MenuContent.setOutAnimation(m_Activity, R.anim.slide_out_to_right);
        m_MenuContent.showPrevious();
    }

    @Override
    protected void flipToNext() {
        showAddBtns();
        m_MenuContent.setInAnimation(m_Activity, R.anim.slide_in_from_right);
        m_MenuContent.setOutAnimation(m_Activity, R.anim.slide_out_to_left);
        m_MenuContent.showNext();
    }

    private void showAddBtns() {
        m_NextDateButton.setVisibility(View.GONE);
        m_PreviousDateButton.setVisibility(View.GONE);
        m_CalendarButton.setVisibility(View.GONE);
        m_SearchBtn.setVisibility(View.GONE);
        m_AddBtn.setVisibility(View.VISIBLE);
        m_CancelBtn.setVisibility(View.VISIBLE);

        if (m_Mode == MODE.DIARY) {
            m_BackToDiaryBtn.setVisibility(View.GONE);
            m_DiaryTabs.setVisibility(View.GONE);
        }

    }

    private void hideAddBtns() {
        m_NextDateButton.setVisibility(View.VISIBLE);
        m_PreviousDateButton.setVisibility(View.VISIBLE);
        m_SearchBtn.setVisibility(View.VISIBLE);
        m_CalendarButton.setVisibility(View.VISIBLE);
        m_AddBtn.setVisibility(View.GONE);
        m_CancelBtn.setVisibility(View.GONE);

        if (m_Mode == MODE.DIARY) {
            m_BackToDiaryBtn.setVisibility(View.VISIBLE);
            m_DiaryTabs.setVisibility(View.VISIBLE);
        }

    }


    public void setHighlight(View v, boolean highlight) {
        TextView tv = (TextView)v.findViewById(R.id.tab_text);
        View h = v.findViewById(R.id.tab_highlight);
        if (highlight) {
            h.setBackgroundColor(getResources().getColor(R.color.main));
            tv.setTypeface(Typeface.DEFAULT_BOLD);
            tv.setTextColor(getResources().getColor(R.color.main));
        } else {
            h.setBackgroundColor(getResources().getColor(R.color.transparent));
            tv.setTypeface(Typeface.DEFAULT);
            tv.setTextColor(getResources().getColor(R.color.gray_text));
        }
    }

    private void updateView(View v) {
        v.invalidate();
        v.requestLayout();
    }



    // ---------------------------------------------------------------------------------- Listeners
    private void initializeListeners() {

       m_DatePickerListener = new DatePickerDialog.OnDateSetListener() {

            // when dialog box is closed, below method will be called.
            public void onDateSet(DatePicker view, int selectedYear,
                                  int selectedMonth, int selectedDay) {
                m_Calendar.set(Calendar.YEAR, selectedYear);
                m_Calendar.set(Calendar.MONTH, selectedMonth);
                m_Calendar.set(Calendar.DAY_OF_MONTH, selectedDay);
                changeInTime();
            }
        };


        m_CalendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(m_Activity, m_DatePickerListener, m_Calendar.get(Calendar.YEAR),
                        m_Calendar.get(Calendar.MONTH), m_Calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        m_CurrentDateTextView.setOnClickListener(new DoubleClickListener() {
            @Override
            public void onSingleClick(View v) {
                // do nothing
            }

            @Override
            public void onDoubleClick(View v) {
                m_Calendar = Calendar.getInstance();
                changeInTime();
            }
        });

        m_NextDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_Calendar.add(Calendar.DATE, 1);
                changeInTime();
            }
        });

        m_PreviousDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_Calendar.add(Calendar.DATE, -1);
                changeInTime();
            }
        });


        // Add button
        m_AddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float fraction = Constants.ServingsFracFloats.get(m_ServingsFraction);
                ParseAPI.addDiaryEntry( m_Calendar,
                                        ParseUser.getCurrentUser(),
                                        m_SelectedRecipe,
                                        m_ServingsWhole + fraction, m_SelectedUserMeal);

                flipToPrev();
                Toast.makeText(m_Activity, "Added to diary!", Toast.LENGTH_SHORT).show();
            }

        });

        // Cancel Button
        m_CancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flipToPrev();
            }
        });


        // Because we reuse this fragment in the diary AND menu, we don't want
        // to reset the listeners initialized from the AddUserMealActivity
        // So only init listeners if in MainActivity
        // TODO: make this cleaner
        if (m_Mode == MODE.MENU) {

            m_SearchBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    flipHeaderToNext();
                    onSearchClick();
                }
            });

            m_CancelSearchBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Show main header
                    flipHeaderToPrev();

                    // Hide the keyboard
                    Utils.hideKeyboard(m_Activity);

                    // Clear the previous search
                    m_SearchEditText.setText("");
                    onCancelSearchClick();
                }
            });

            m_SearchEditText.addTextChangedListener(new TextWatcher() {

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // Get input
                    Object input = m_SearchEditText.getText();
                    if (input == null) return;
                    String text = input.toString().toLowerCase().trim();

                    onSearchEditTextChanged(text, start, before);
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }


        for (int i = 0; i < m_MenuTabsLinearLayout.getChildCount(); i++) {
            final int index = i;
            final View tv = m_MenuTabsLinearLayout.getChildAt(i).findViewById(R.id.tab_text);
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
        initializeNutritionListeners();

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

    public abstract class DoubleClickListener implements View.OnClickListener {

        private static final long DOUBLE_CLICK_TIME_DELTA = 300; //milliseconds

        long lastClickTime = 0;

        @Override
        public void onClick(View v) {
            long clickTime = System.currentTimeMillis();
            if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA){
                onDoubleClick(v);
            } else {
                onSingleClick(v);
            }
            lastClickTime = clickTime;
        }

        public abstract void onSingleClick(View v);
        public abstract void onDoubleClick(View v);
    }




    // ------------------------------------------------------------------------- Main Data/Parse

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

    public void changeInTime() {
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT_DISPLAY, Locale.US);
        m_CurrentDateTextView.setText(sdf.format(m_Calendar.getTime()));
        update();
    }


    public void update() {
        Utils.showProgressSpinner(m_ProgressSpinner);

        String venue = Constants.venueParseStrings
                .get(Constants.Venue.valueOf(m_CurrentVenue.getTag().toString()));

        String[] mealtime = Constants.mealTimeParseStrings
                .get(Constants.MealTime.valueOf(m_CurrentMealTime.getTag().toString()));

        String menu = Constants.menuParseStrings
                .get(Constants.Menu.valueOf(m_CurrentMenu.getTag().toString()));

        int day = m_Calendar.get(Calendar.DAY_OF_MONTH);
        int month = m_Calendar.get(Calendar.MONTH) + 1;
        int year = m_Calendar.get(Calendar.YEAR);

        queryParseRecipes(day, month, year, venue, mealtime, menu);
    }


    private void queryParseRecipes(int day, int month, int year, String venueKey,
                                   String[] mealNames, String menuName) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Offering");
        query.whereEqualTo("month", month);
        query.whereEqualTo("day", day);
        query.whereEqualTo("year", year);


        if (venueKey != null) {
            query.whereEqualTo("venueKey", venueKey);
        }
        if (mealNames != null) {
            query.whereContainedIn("mealName", Arrays.asList(mealNames));
        }
        if (menuName != null) {
            query.whereEqualTo("menuName", menuName);
        }

        // query.orderByAscending
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> offeringsList, ParseException e) {
                if (e == null) {
                    m_MenuItemsList.clear();

                    List<ParseQuery<ParseObject>> queryList = new ArrayList<ParseQuery<ParseObject>>();
                    for (ParseObject object : offeringsList) {
                        Offering offering = (Offering) object;
                        ParseRelation<ParseObject> relation = offering.getRecipes();
                        ParseQuery q = relation.getQuery();
                        queryList.add(q);
                    }

                    if (queryList.isEmpty()) {
                        notifyMenuListAdapter();
                        return;
                    }

                    ParseQuery<ParseObject> recipesQuery = ParseQuery.or(queryList);
                    recipesQuery.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
                    recipesQuery.orderByAscending("name");
                    recipesQuery.setLimit(1000);


                    recipesQuery.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> list, ParseException e) {

                            // Create the new list of recipes
                            if (e == null) {
                                for (ParseObject object : list)
                                    m_MenuItemsList.add((Recipe) object);
                            }

                            // If the search bar is currently open, filter the results
                            if (SEARCH_MODE) {
                                resetSearch();
                                updateSearch(m_MenuItemsList, getSearchText());
                            } else {
                                // Notify the adapter and update the view
                                notifyMenuListAdapter();
                            }
                        }
                    });
                } else {
                    // do something with the error
                }
            }
        });
    }

    @Override
    public void onItemClick(Recipe recipe) {
        if (SEARCH_MODE) {
            clearSearch();
        }
        m_ServingsWhole = 1;
        m_ServingsFraction = 0;

        // Get user meal (breakfast, lunch, dinner, snacks) from meal time
        Constants.MealTime mealTime = Constants.MealTime.valueOf(m_CurrentMealTime.getTag().toString());
        Constants.UserMeals userMeal = Constants.userMealForMealTime.get(mealTime);
        m_SelectedUserMeal = userMeal.name();

        super.onItemClick(recipe);
        flipToNext();
    }

    private void notifyMenuListAdapter() {
        Utils.hideProgressSpinner(m_ProgressSpinner);
        m_MenuItemListAdapter.updateData(m_MenuItemsList);

        // If no recipes found, show message
        if (m_MenuItemsList.isEmpty()) {
            m_EmptyMenuText.setVisibility(View.VISIBLE);
        } else {
            m_EmptyMenuText.setVisibility(View.GONE);
        }
    }


    private void setMode() {
        String callingActivity = m_Activity.getLocalClassName();
        String diaryActivity = AddUserMealActivity.class.getName();
        m_Mode = diaryActivity.contains(callingActivity) ? MODE.DIARY : MODE.MENU;
    }


    // ------------------------------------------------------------------------------------ Search

    @Override
    public final void onSearchClick() {
        resetSearch();
    }

    @Override
    public final void onCancelSearchClick() {
        clearSearch();
    }

    @Override
    public final void onSearchEditTextChanged(String text, int start, int before) {

        // If the part of the input was deleted, search again from original list
        List<Recipe> listToSearch;
        if (start < before) {
            listToSearch = m_RestoredList;
            // Otherwise, search from restricted list
        } else {
            listToSearch = m_MenuItemsList;
        }

        // Substring match
        updateSearch(listToSearch, text);

    }

    private void resetSearch() {
        SEARCH_MODE = true;
        m_RestoredList = Utils.copyRecipeList(m_MenuItemsList);
    }

    private void clearSearch() {
        SEARCH_MODE = false;
        m_RestoredList.clear();

        // Exit search and restore previous items
        update();
    }

    @Override
    public void updateSearch(List listToSearch, String text) {

        if (text == null)
            return;

        // Fragment initialized with search already open
        if (listToSearch == null) {
            resetSearch();
            listToSearch = m_MenuItemsList;
        }

        // List of results that contain substring
        ArrayList<Recipe> searchResults = new ArrayList<>();
        for (Object item : listToSearch) {
            Recipe r = (Recipe) item;
            String name = r.getName().toLowerCase();
            if (name.contains(text)) {
                searchResults.add(r);
            }
        }

        // Update the list and notify adapter
        m_MenuItemsList = searchResults;
        notifyMenuListAdapter();
    }

    private String getSearchText() {
        String text;
        try {
            text = (m_Mode == MODE.DIARY) ?
                    ((AddUserMealActivity)m_Activity).getSearchText() :
                    m_SearchEditText.getText().toString().toLowerCase().trim();
        } catch (NullPointerException e) {
            text = "";
        }
        return text;
    }
}