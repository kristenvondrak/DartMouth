package com.example.kristenvondrak.dartmouth.Main;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kristenvondrak on 1/9/16.
 */
public class Constants {

    public enum Venue {Foco, Hop, Novack};

    public enum MealTime {Breakfast, Lunch, Dinner, LateNight, AllDay};

    public enum Menu {AllItems, Specials, EverydayItems, Beverage, Cereal, Condiments, GlutenFree,
                        Deli, Grill, GrabGo, Snacks};


    public static final Map<Venue, MealTime[]> mealTimesForVenue;
    static {
        Map<Venue, MealTime[]> map = new HashMap<>();
        map.put(Venue.Foco, new MealTime[]{ MealTime.Breakfast,
                                            MealTime.Lunch,
                                            MealTime.Dinner});


        map.put(Venue.Hop, new MealTime[]{  MealTime.Breakfast,
                                            MealTime.Lunch,
                                            MealTime.Dinner,
                                            MealTime.LateNight});


        map.put(Venue.Novack, new MealTime[]{ MealTime.AllDay});
        mealTimesForVenue = Collections.unmodifiableMap(map);
    }


    public static final Map<Venue, Menu[]> menusForVenue;
    static {
        Map<Venue, Menu[]> map = new HashMap<>();
        map.put(Venue.Foco, new Menu[]{ Menu.AllItems,
                                        Menu.Specials,
                                        Menu.EverydayItems,
                                        Menu.GlutenFree,
                                        Menu.Beverage,
                                        Menu.Condiments});


        map.put(Venue.Hop, new Menu[]{  Menu.AllItems,
                                        Menu.Specials,
                                        Menu.EverydayItems,
                                        Menu.Deli,
                                        Menu.Grill,
                                        Menu.GrabGo,
                                        Menu.Snacks,
                                        Menu.Beverage,
                                        Menu.Condiments});


        map.put(Venue.Novack, new Menu[]{   Menu.AllItems,
                                            Menu.Specials,
                                            Menu.EverydayItems,});

        menusForVenue = Collections.unmodifiableMap(map);
    }

    public static class VenueStrings {
        static String FocoDisplay = "Foco";
        static String FocoParse = "DDS";

        static String HopDisplay = "Hop";
        static String HopParse = "CYC";

        static String NovackDisplay = "Novack";
        static String NovackParse = "NOVACK";
    }


    public static class MealTimeStrings {
        static String BreakfastDisplay = "Breakfast";
        static String BreakfastParse = "Breakfast";

        static String LunchDisplay = "Lunch";
        static String LunchParse = "Lunch";

        static String DinnerDisplay = "Dinner";
        static String DinnerParse = "Dinner";

        static String LateNightDisplay = "Late Night";
        static String LateNightParse = "Late Night";

        static String AllDayDisplay = "All Day";
        static String AllDayParse = "Every Day";

    }

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

}
