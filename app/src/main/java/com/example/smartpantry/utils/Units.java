package com.example.smartpantry.utils;

import java.util.Locale;

//Utility class for handling measurement units (Mass, Volume, Count)

public final class Units{
    public static final String[] ALL = {"g", "kg", "ml", "L", "pieces"};
    public static final int UNKNOWN = 0, MASS=1, VOLUME=2, COUNT=3;

    //private constructor to prevent creating instances
    private Units(){}

    //to determine the category of a given string
    public static int category(String unit){
        if (unit==null) return UNKNOWN;

        //removes spaces and making text lowercase to handle "Kg", " ml "...
        switch (unit.trim().toLowerCase(Locale.ROOT)){
            case "g":
            case "kg":
                return MASS;
            case "ml":
            case "l":
                return VOLUME;
            case "piece":
            case "pieces":
            case "pcs":
                return COUNT;
            default:
                return UNKNOWN;
        }
    }

    //converting to the base unit of the category: g, ml or one piece
    public static double toBase(double quantity, String unit){
        //if unit is null use empty string, otherwise clean it up
        //if its kg or litres, multiply by 1000 to convert to grams or ml
        String u = unit == null ? "" : unit.trim().toLowerCase(Locale.ROOT);
        return(u.equals("kg")) || u.equals("l") ? quantity * 1000.0 :quantity;
    }

    //formatting so that things like 2.0 become "2", or 0.5 becomes "0.5"
    public static String formatQuantity(double q){
        //checking if number is a whole number
        if (q==Math.rint(q))
            return String.valueOf((long) q);

        return String.format(Locale.US, "%.2f", q);
    }
}