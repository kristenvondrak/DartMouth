package com.example.kristenvondrak.dartmouth.Menu;

import com.example.kristenvondrak.dartmouth.Parse.Recipe;
import com.example.kristenvondrak.dartmouth.R;

public class Nutrients {


    public static final int NONE = -1;
    public static final int SATURATED_FAT = 0;
    public static final int THIAMIN = 1;
    public static final int ZINC = 2;
    public static final int VITAMIN_C = 3;
    public static final int DIETARY_FIBER = 4;
    public static final int SERVING_SIZE = 5;
    public static final int PHOSPHORUS = 6;
    public static final int VITAMIN_B12 = 7;
    public static final int POTASSIUM = 8;
    public static final int FAT = 9;
    public static final int CALCIUM = 10;
    public static final int MONOUNSATURATED_FAT = 11;
    public static final int IRON = 12;
    public static final int SUGARS = 13;
    public static final int PROTEIN = 14;
    public static final int VITAMIN_B6 = 15;
    public static final int TRANSFAT = 16;
    public static final int NIACIN = 17;
    public static final int TOTAL_CARBOHYDRATE = 18;
    public static final int CHOLESTEROL = 19;
    public static final int SODIUM = 20;
    public static final int CALORIES = 21;
    public static final int RIBOFLAVIN = 22;
    public static final int FOLACIN = 23;
    public static final int POLYUNSATURATED_FAT = 24;
    public static final int FAT_CALS = 25;

    public static final int ALLERGENS = 26;
    public static final int DIETARY_PREFS = 27;

    public static final String[] names = {
            "Saturated Fat",
            "Thiamin",
            "Zinc",
            "Vitamin C",
            "Dietary Fiber",
            "Serving Size (g)",
            "Phosphorus",
            "Vitamin B12",
            "Potassium",
            "Fat",
            "Calcium",
            "Monounsaturated Fat",
            "Iron",
            "Sugars",
            "Protein",
            "Vitamin B6",
            "Trans Fat",
            "Niacin",
            "Total Carbohydrate",
            "Cholesterol",
            "Sodium",
            "Calories",
            "Riboflavin",
            "Folacin",
            "Polyunsaturated Fat",
            "Calories from Fat"};

    public static final int[] grams = {
            R.id.saturated_fat,
            R.id.thiamin,
            R.id.zinc,
            R.id.vitamin_c,
            R.id.dietary_fiber,
            R.id.serving_size,
            R.id.phosphorus,
            R.id.vitamin_b12,
            R.id.potassium,
            R.id.fat,
            R.id.calcium,
            R.id.mono_fat,
            R.id.iron,
            R.id.sugars,
            R.id.protein,
            R.id.vitamin_b6,
            R.id.transfat,
            R.id.niacin,
            R.id.carbs,
            R.id.cholesterol,
            R.id.sodium,
            R.id.calories,
            R.id.riboflavin,
            R.id.folacin,
            R.id.poly_fat,
            R.id.fat_calories
    };

    public static final int[] percents = {
            R.id.saturated_fat_percent,
            -1,
            -1,
            -1,
            R.id.dietary_fiber_percent,
            -1,
            -1,
            -1,
            -1,
            R.id.fat_percent,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            R.id.carbs_percent,
            R.id.cholesterol_percent,
            R.id.sodium_percent,
            -1,
            -1,
            -1,
            -1,
            -1
    };

    public static final int[] daily_grams = {
            20,
            -1,
            -1,
            -1,
            25,
            -1,
            -1,
            -1,
            -1,
            65,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            300,
            300,
            2400,
            -1,
            -1,
            -1,
            -1,
            -1
    };


    public static String get(Recipe recipe, int nutrient_id) {
        switch (nutrient_id) {
            case SATURATED_FAT:
                return recipe.getSfa();
            case THIAMIN:
                return recipe.getThiamin();
            case ZINC:
                return recipe.getZinc();
            case VITAMIN_C:
                return recipe.getVitc();
            case DIETARY_FIBER:
                return recipe.getFiber();
            case SERVING_SIZE:
                return recipe.getServingSize();
            case PHOSPHORUS:
                return recipe.getPhosphorus();
            case VITAMIN_B12:
                return recipe.getVitb12();
            case POTASSIUM:
                return recipe.getPotassium();
            case FAT:
                return recipe.getTotalFat();
            case CALCIUM:
                return recipe.getCalcium();
            case MONOUNSATURATED_FAT:
                return recipe.getMufa();
            case IRON:
                return recipe.getIron();
            case SUGARS:
                return recipe.getSugars();
            case PROTEIN:
                return recipe.getProtein();
            case VITAMIN_B6:
                return recipe.getVitb6();
            case TRANSFAT:
                return recipe.getTransFat();
            case NIACIN:
                return recipe.getNiacin();
            case TOTAL_CARBOHYDRATE:
                return recipe.getTotalCarbs();
            case CHOLESTEROL:
                return recipe.getCholestrol();
            case SODIUM:
                return recipe.getSodium();
            case CALORIES:
                return recipe.getCalories();
            case RIBOFLAVIN:
                return recipe.getRiboflavin();
            case FOLACIN:
                return recipe.getFolacin();
            case POLYUNSATURATED_FAT:
                return recipe.getPufa();
            case FAT_CALS:
                return recipe.getFatCalories();
            case ALLERGENS:
                return "";
            case DIETARY_PREFS:
                return "";
            default:
                return null;
        }
    }

    public static int stripChars(String string) {
        String digits = "";
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            if (Character.isDigit(c) || c == '.'){
                digits += string.charAt(i);
            }
        }
        if (digits.equals(""))
            return NONE;

        return (int) Double.parseDouble(digits);
    }

    public static String addSpace(String string) {
        for (int i = 0; i < string.length(); i++) {
            if (!Character.isDigit(string.charAt(i)))
                return string.substring(0, i) + " " + string.substring(i, string.length());
        }
        return string;
    }

    public static double convertToDouble(String string) {
        String s = "";
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            if (Character.isDigit(c) || c == '.')
                s += c;
        }
        if (s.equals(""))
            return NONE;

        return Double.parseDouble(s);
    }

    public static String getUnits(String string) {
        for (int i = 0; i < string.length(); i++) {
            if (Character.isLetter(string.charAt(i)))
                return (string.substring(i, string.length()));
        }
        return "";
    }
}
