package com.example.kristenvondrak.dartmouth.Main;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by kristenvondrak on 1/9/16.
 */
public class Constants {

    public static final String DATE_FORMAT_EXTRA = "yyyy,EEE,LLL,d";
    public static final String DATE_FORMAT_DISPLAY = "EEE, LLL d";

    public enum Venue {Foco, Hop, Novack, Collis};

    public enum MealTime {Breakfast, Lunch, Dinner, LateNight, AllDay};

    public enum Menu {AllItems, Specials, EverydayItems, Beverage, Cereal, Condiments, GlutenFree,
                        Deli, Grill, GrabGo, Snacks};


    public enum UserMeals {Breakfast, Lunch, Dinner, Snacks};


    public static final Map<Venue, MealTime[]> mealTimesForVenue = Collections.unmodifiableMap(
            new HashMap<Venue, MealTime[]>() {{
                put(Venue.Foco, new MealTime[]{
                        MealTime.Breakfast,
                        MealTime.Lunch,
                        MealTime.Dinner});


                put(Venue.Hop, new MealTime[]{
                        MealTime.Lunch,
                        MealTime.Dinner,
                        MealTime.LateNight});

                put(Venue.Collis, new MealTime[]{
                        MealTime.Breakfast,
                        MealTime.Lunch,
                        MealTime.Dinner});

                put(Venue.Novack, new MealTime[]{ MealTime.AllDay});

    }});



    public static final Map<Venue, Menu[]> menusForVenue = Collections.unmodifiableMap(
        new HashMap<Venue, Menu[]>() {{
            put(Venue.Foco, new Menu[]{ Menu.AllItems,
                    Menu.Specials,
                    Menu.EverydayItems,
                    Menu.GlutenFree,
                    Menu.Beverage,
                    Menu.Condiments});


            put(Venue.Hop, new Menu[]{  Menu.AllItems,
                    Menu.Specials,
                    Menu.EverydayItems,
                    Menu.Deli,
                    Menu.Grill,
                    Menu.GrabGo,
                    Menu.Snacks,
                    Menu.Beverage,
                    Menu.Condiments});

            put(Venue.Collis, new Menu[]{   Menu.AllItems,
                    Menu.EverydayItems,});


            put(Venue.Novack, new Menu[]{   Menu.AllItems,
                    Menu.Specials,
                    Menu.EverydayItems,});
    }});

    public static final Map<Venue, String> venueDisplayStrings = Collections.unmodifiableMap(
            new HashMap<Venue, String>() {{
                    put(Venue.Foco, "Foco");
                    put(Venue.Hop, "Hop");
                put(Venue.Collis, "Collis");
                    put(Venue.Novack, "Novack");
                }});

    public static final Map<Venue, String> venueParseStrings = Collections.unmodifiableMap(
            new HashMap<Venue, String>() {{
                put(Venue.Foco, "DDS");
                put(Venue.Hop, "CYC");
                put(Venue.Collis, "COLLIS");
                put(Venue.Novack, "NOVACK");
            }});

    public static final Map<MealTime, String> mealTimeDisplayStrings = Collections.unmodifiableMap(
            new HashMap<MealTime, String>() {{
                put(MealTime.Breakfast, "Breakfast");
                put(MealTime.Lunch, "Lunch");
                put(MealTime.Dinner, "Dinner");
                put(MealTime.LateNight, "Late Night");
                put(MealTime.AllDay, "All Day");
            }});

    public static final Map<MealTime, String> mealTimeParseStrings = Collections.unmodifiableMap(
            new HashMap<MealTime, String>() {{
                put(MealTime.Breakfast, "Breakfast");
                put(MealTime.Lunch, "Lunch");
                put(MealTime.Dinner, "Dinner");
                put(MealTime.LateNight, "Late Night");
                put(MealTime.AllDay, "Every Day");
            }});


    public static final Map<Menu, String> menuDisplayStrings = Collections.unmodifiableMap(
            new HashMap<Menu, String>() {{
                put(Menu.AllItems, "All Items");
                put(Menu.Specials, "Today's Specials");
                put(Menu.EverydayItems, "Everyday Items");
                put(Menu.Beverage, "Beverage");
                put(Menu.Cereal, "Cereal");
                put(Menu.Condiments, "Condiments");
                put(Menu.GlutenFree, "Gluten Free");
                put(Menu.EverydayItems, "Everyday Items");
                put(Menu.Deli, "Deli");
                put(Menu.Grill, "Grill");
                put(Menu.GrabGo, "Grab & Go");
                put(Menu.Snacks, "Snacks");
            }});

    public static final Map<Menu, String> menuParseStrings = Collections.unmodifiableMap(
            new HashMap<Menu, String>() {{
                put(Menu.AllItems, null);
                put(Menu.Specials, "Today's Specials");
                put(Menu.EverydayItems, "Everyday Items");
                put(Menu.Beverage, "Beverage");
                put(Menu.Cereal, "Cereal");
                put(Menu.Condiments, "Condiments");
                put(Menu.GlutenFree, "Additional Gluten Free");
                put(Menu.EverydayItems, "Everyday Items");
                put(Menu.Deli, "Courtyard Deli");
                put(Menu.Grill, "Courtyard Grill");
                put(Menu.GrabGo, "Grab & Go");
                put(Menu.Snacks, "Courtyard Snacks");
            }});


    public static class MenuStrings {
        static String AllItemsDisplay = "All Items";

        static String SpecialsDisplay = "Today's Specials";
        static String SpecialsParse = "Today's Specials";

        static String EverydayItemsDisplay = "Everyday Items";
        static String EverydayItemsParse = "Everyday Items";

        static String BeverageDisplay = "Beverage";
        static String BeverageParse = "Beverage";

        static String CerealDisplay = "Cereal";
        static String CerealParse = "Cereal";

        static String CondimentsDisplay = "Condiments";
        static String CondimentsParse = "Condiments";

        static String GlutenFreeDisplay = "Gluten Free";
        static String GlutenFreeParse = "Additional Gluten Free";

        static String DeliDisplay = "Deli";
        static String DeliParse = "Courtyard Deli";

        static String GrillDisplay = "Grill";
        static String GrillParse = "Courtyard Grill";

        static String GrabGoDisplay = "Grab & Go";
        static String GrabGoParse = "Grab & Go";

        static String SnacksDisplay = "Snacks";
        static String SnacksParse = "Courtyard Snacks";
    }

    public static class Validation {
        static int MinimumPasswordLength = 6;
        static int MaximumPasswordLength = 25;
        static String EmailRegex = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}";

        static String InvalidEmailTitle = "Invalid Email";
        static String InvalidEmailMessage = "Please sign up with a valid email.";
        static String InvalidPasswordTitle = "Invalid Password";
        static String InvalidPasswordMessage = "Please enter a password between " +
                "(MinimumPasswordLength) and (MaximumPasswordLength) characters, inclusive.";
        static String NoMatchPasswordsTitle = "Passwords Don't Match";
        static String NoMatchPasswordsMessage = "Please correctly confirm your password.";
        static String SignupErrorTitle = "Signup Error";
        static String SignupErrorDefaultMessage = "Unknown error signing up.";
        static String SigninErrorTitle = "Signin Error";
        static String SigninErrorDefaultMessage = "Unknown error signing in.";
        static String OkActionTitle = "OK";
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
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_DISPLAY, Locale.US);
        return sdf.format(cal.getTime());
    }

    public static Calendar getCalFromStringExtra(String text) throws ParseException {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_EXTRA, Locale.US);
        cal.setTime(sdf.parse(text));
        return cal;
    }

    public static String getStringExtraFromCal(Calendar cal) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_EXTRA, Locale.US);
        return sdf.format(cal.getTime());
    }

}
