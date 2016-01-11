package com.example.kristenvondrak.dartmouth.Parse;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by kristenvondrak on 1/10/16.
 */
public class ParseAPI {


    /*
    * Async function that retrieves Recipes for the given parameters.
    * Calls completion block with the retrieved Recipes.
    */
    public static List<Recipe> recipesFromCloudForDate(Calendar calendar, String venueKey, String mealName, String menuName) {
        final List<Recipe> recipesList = new ArrayList<>();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Offering");
        query.whereEqualTo("month", month);
        query.whereEqualTo("day", day);
        query.whereEqualTo("year", year);

        Log.d("month", Integer.toString(month));
        Log.d("day", Integer.toString(day));
        Log.d("year", Integer.toString(year));

        if (venueKey != null) {
            Log.d("^^^^^^^^^^^^^^^", venueKey);
            query.whereEqualTo("venueKey", venueKey);
        }
        if (mealName != null) {
            Log.d("^^^^^^^^^^^^^^^", mealName);
            query.whereEqualTo("mealName", mealName);
        }
        if (menuName != null) {
            Log.d("^^^^^^^^^^^^^^^", menuName);
            query.whereEqualTo("menuName", menuName);
        }



        // query.orderByAscending

        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> offeringsList, ParseException e) {
                if (e == null) {
                    Log.d("^^^^^^^^^^^^^^^", offeringsList.toString());
                    List<ParseQuery<ParseObject>> queryList = new ArrayList<ParseQuery<ParseObject>>();
                    for (ParseObject object : offeringsList) {
                        Log.d("^^^^^^^^^^^^^^^", object.getObjectId());
                        Offering offering = (Offering) object;
                        ParseRelation<ParseObject> relation = offering.getRecipes();
                        ParseQuery q = relation.getQuery();
                        queryList.add(q);
                    }

                    ParseQuery<ParseObject> recipesQuery = ParseQuery.or(queryList);
                    recipesQuery.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
                    recipesQuery.orderByAscending("name");
                    recipesQuery.setLimit(1000);

                    recipesQuery.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> list, ParseException e) {
                            if (e == null) {
                                for (ParseObject object : list) {
                                    Log.d("########", object.getObjectId());
                                    recipesList.add((Recipe) object);
                                }
                            } else {

                            }
                        }
                    });
                } else {
                    // error
                }
            }
        });

        return recipesList;
    }

    public static void logOutParseUser() {
        ParseUser.logOut();
        ParseUser currentUser = ParseUser.getCurrentUser(); // this will now be null
    }

    public static ParseUser getCurrentParseUser() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            // do stuff with the user
        } else {
            // show the signup or login screen
        }
        return currentUser;
    }


    public static void logInParseUser(String email, String password) {
        ParseUser.logInInBackground("joestevens", "secret123", new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    // Hooray! The user is logged in.
                } else {
                    // Signup failed. Look at the ParseException to see what happened.
                }
            }
        });
    }


    private void createNewParseUser(String email, String password) {
        ParseUser user = new ParseUser();
        // Set core properties
        user.setPassword("secret123");
        user.setEmail("email@example.com");
        // Set custom properties
        // Invoke signUpInBackground
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // Hooray! Let them use the app now.
                } else {
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                }
            }
        });
    }
}
