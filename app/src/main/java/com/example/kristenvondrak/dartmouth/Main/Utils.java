package com.example.kristenvondrak.dartmouth.Main;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;

import com.example.kristenvondrak.dartmouth.Parse.Recipe;
import com.example.kristenvondrak.dartmouth.Parse.UserMeal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by kristenvondrak on 2/10/16.
 */
public class Utils {


    public static int getServingsFracIndex(float value) {
        for (int i = 0; i < Constants.ServingsFracFloats.size(); i++) {
            if (value == Constants.ServingsFracFloats.get(i))
                return i;
            else if (value < Constants.ServingsFracFloats.get(i)) {
                float d1 = Constants.ServingsFracFloats.get(i) - value;
                float d2 = value - Constants.ServingsFracFloats.get(i - 1);

                return d1 < d2 ? i : i - 1;
            }
        }
        return 0;
    }

    public static Date getDateBefore(Calendar calendar) {
        Calendar c = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        c.set(year, month, day, 23, 59, 59);
        c.add(Calendar.DAY_OF_MONTH, -1);
        return c.getTime();
    }

    public static Date getDateAfter(Calendar calendar) {
        Calendar c = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        c.set(year, month, day, 0, 0, 0);
        c.add(Calendar.DAY_OF_MONTH, 1);
        return c.getTime();
    }

    public static String getDisplayStringFromCal(Calendar cal) {
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT_DISPLAY, Locale.US);
        return sdf.format(cal.getTime());
    }


    public static String getStringExtraFromCal(Calendar cal) {
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT_EXTRA, Locale.US);
        return sdf.format(cal.getTime());
    }

    public static List<Recipe> copyRecipeList(List<Recipe> list) {
        List<Recipe> copy = new ArrayList<>();
        for (Recipe r : list) {
            copy.add(r);
        }
        return copy;
    }

    public static List<UserMeal> copyMealsList(List<UserMeal> list) {
        List<UserMeal> copy = new ArrayList<>();
        for (UserMeal r : list) {
            copy.add(r);
        }
        return copy;
    }

    public static void showProgressSpinner(ProgressBar view) {
        view.setVisibility(View.VISIBLE);
        view.bringToFront();
    }

    public static void hideProgressSpinner(ProgressBar view) {
        view.setVisibility(View.GONE);
        view.bringToFront();
    }

    public static void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm =
                    (InputMethodManager) activity.getSystemService(activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }



}
