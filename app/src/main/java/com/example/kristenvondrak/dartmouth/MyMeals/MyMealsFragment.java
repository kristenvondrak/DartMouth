package com.example.kristenvondrak.dartmouth.MyMeals;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.kristenvondrak.dartmouth.Diary.AddUserMealActivity;
import com.example.kristenvondrak.dartmouth.Diary.MealEntriesListAdapter;
import com.example.kristenvondrak.dartmouth.Main.SearchHeader;
import com.example.kristenvondrak.dartmouth.Main.Utils;
import com.example.kristenvondrak.dartmouth.Menu.MealSelectorFragment;
import com.example.kristenvondrak.dartmouth.Parse.DiaryEntry;
import com.example.kristenvondrak.dartmouth.Parse.ParseAPI;
import com.example.kristenvondrak.dartmouth.Parse.UserMeal;
import com.example.kristenvondrak.dartmouth.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class MyMealsFragment extends MealSelectorFragment implements SearchHeader {

    // Main View
    private ListView m_MealListView;
    private List<UserMeal> m_RestoredList;
    private ProgressBar m_ProgressSpinner;

    // Single Meal View
    private ListView m_MealEntriesListView;
    private List<DiaryEntry> m_MealEntriesList;
    private MealEntriesListAdapter m_MealEntriesListAdapter;
    private TextView m_AddMealBtn;
    private TextView m_CancelMealBtn;
    private AddUserMealActivity m_Activity;
    private ViewFlipper m_ViewFlipper;
    private List<UserMeal> m_UserMealsList;
    private MealListAdapter m_MealsListAdapter;
    private Calendar m_Calendar;
    private TextView m_TotalCals;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_mymeals, container, false);

        m_Activity = (AddUserMealActivity)getActivity();
        m_SelectedUserMeal = m_Activity.getUserMeal();
        m_Calendar = m_Activity.getCalendar();

        initializeViews(v);
        initializeListeners();

        // Get most recent recipes from Parse
        m_UserMealsList = new ArrayList<>();
        m_MealsListAdapter = new MealListAdapter(m_Activity, this);
        m_MealListView.setAdapter(m_MealsListAdapter);

        m_MealEntriesList = new ArrayList<>();
        m_MealEntriesListAdapter = new MealEntriesListAdapter(m_Activity, m_MealEntriesList, this);
        m_MealEntriesListView.setAdapter(m_MealEntriesListAdapter);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        queryPastMeals();
    }


    // ------------------------------------------------------------------------------------- Views

    private void initializeViews(View v) {
        m_ProgressSpinner = (ProgressBar) v.findViewById(R.id.progress_spinner);
        m_MealListView = (ListView) v.findViewById(R.id.mymeals_listview);
        m_ViewFlipper = (ViewFlipper) v.findViewById(R.id.mymeals_viewflipper);
        m_MealEntriesListView = (ListView) v.findViewById(R.id.meal_entries_list);
        m_UserMealSelector = (LinearLayout) v.findViewById(R.id.usermeal_selector);
        m_TotalCals = (TextView) v.findViewById(R.id.meal_total_cals);
        m_AddMealBtn = m_Activity.getAddBtn();
        m_CancelMealBtn = m_Activity.getCancelBtn();
    }

    private void flipToNext() {
        m_Activity.showAlternativeHeader();
        m_ViewFlipper.setInAnimation(m_Activity, R.anim.slide_in_from_right);
        m_ViewFlipper.setOutAnimation(m_Activity, R.anim.slide_out_to_left);
        m_ViewFlipper.showNext();
    }

    private void flipToPrev() {
        m_Activity.showMainHeader();
        m_ViewFlipper.setInAnimation(m_Activity, R.anim.slide_in_from_left);
        m_ViewFlipper.setOutAnimation(m_Activity, R.anim.slide_out_to_right);
        m_ViewFlipper.showPrevious();
    }

    // --------------------------------------------------------------------------------- Listeners

    private void initializeListeners() {

        m_AddMealBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<DiaryEntry> list = m_MealEntriesListAdapter.getSelectedEntries();
                ParseAPI.addDiaryEntries(m_Calendar, ParseUser.getCurrentUser(), list, m_SelectedUserMeal);
                flipToPrev();
                Toast.makeText(m_Activity, "Added to diary!", Toast.LENGTH_SHORT).show();
            }

        });

        m_CancelMealBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flipToPrev();
            }
        });

        initializeMealSelector();
    }


    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    public void onMealClick(UserMeal meal) {
        // Clear any current search
        if (m_Activity.SEARCH_MODE)
            m_Activity.getCancelSearchBtn().callOnClick();

        // Display the diary entries in the meal
        m_MealEntriesList.clear();
        for (DiaryEntry entry : meal.getDiaryEntries()) {
            m_MealEntriesList.add(entry);
        }
        resetTotalCals(m_MealEntriesList);
        m_MealEntriesListAdapter.resetData();
        m_SelectedUserMeal = meal.getTitle();
        resetMealSelector();
        flipToNext();


    }


    // ------------------------------------------------------------------------------------- Parse

    private void queryPastMeals() {
        Utils.showProgressSpinner(m_ProgressSpinner);
        m_UserMealsList.clear();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserMeal");
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.include("entries.recipe");
        query.orderByDescending("date");
        query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (ParseObject object : objects) {
                        UserMeal meal = (UserMeal) object;
                        m_UserMealsList.add(meal);
                    }
                    m_MealsListAdapter.updateData(m_UserMealsList);
                    if (m_Activity.inSearchMode()) {
                        resetSearch();
                        updateSearch(m_UserMealsList, m_Activity.getSearchText());
                    }

                } else {
                    Log.d("ERROR:", e.getMessage());
                }
                Utils.hideProgressSpinner(m_ProgressSpinner);
            }
        });

    }

    public void resetTotalCals(List<DiaryEntry> list) {
        int totalCals = 0;
        for (DiaryEntry entry :list) {
            totalCals += entry.getTotalCalories();
        }
        m_TotalCals.setText("calories: " + Integer.toString(totalCals));
    }


    // ------------------------------------------------------------------------------------ Search

    @Override
    public void onSearchClick() {
       resetSearch();
    }

    @Override
    public void onCancelSearchClick() {
        clearSearch();
    }

    @Override
    public void onClearSearchClick() {
        clearSearch();
    }

    @Override
    public void onEnterClick() {
        // do nothing since we update as user types
    }

    @Override
    public void onSearchEditTextChanged(String text, int start, int before) {

        // If the part of the input was deleted, search again from original list
        List<UserMeal> listToSearch;
        if (start < before) {
            listToSearch = m_RestoredList;
            // Otherwise, search from the restricted list
        } else {
            listToSearch = m_UserMealsList;
        }

        updateSearch(listToSearch, text);
    }

    private void resetSearch() {
        m_RestoredList = Utils.copyMealsList(m_UserMealsList);
    }

    private void clearSearch() {
        m_RestoredList.clear();
        queryPastMeals();
    }

    public void updateSearch(List listToSearch, String text) {
        if (text == null)
            return;

        // Fragment initialized with search already open
        if (listToSearch == null) {
            resetSearch();
            listToSearch = m_UserMealsList;
        }

        // List of results where meal contains recipe with substring in name
        ArrayList<UserMeal> searchResults = new ArrayList<>();
        for (Object item : listToSearch) {
            UserMeal meal = (UserMeal) item;
            for (DiaryEntry entry : meal.getDiaryEntries()) {
                String name = entry.getRecipe().getName().toLowerCase();
                if (name.contains(text)) {
                    searchResults.add(meal);
                    break;
                }
            }
        }

        // Update the list and notify adapter
        m_UserMealsList = searchResults;
        m_MealsListAdapter.updateData(m_UserMealsList);

    }

}