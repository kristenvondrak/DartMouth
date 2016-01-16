package com.example.kristenvondrak.dartmouth.Menu;

public class Nutrients {
    public static int NONE = -1;

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

        int i = 0;
        while (true) {
            if (Character.isDigit(string.charAt(i)))
                break;
            i++;
        }

        for (int j = i; j < string.length(); j++) {
            if (Character.isLetter(string.charAt(j)))
                return (string.substring(j, string.length()));
        }
        return "";
    }
}
