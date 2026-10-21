package com.example.smartpantry.utils;

import com.example.smartpantry.model.Ingredient;
import com.example.smartpantry.model.Recipe;
import com.example.smartpantry.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


//utility class that matches a user's pantry against recipes to see what can be cooked
public final class RecipeMatcher{
    // to prevent rounding errors during comparisons
    public static final double EPSILON = 1e-9;

    private RecipeMatcher(){}

    //making ingredient names lowercase and clearing spaces, and stripping plural endings
    public static String normalizeName(String raw){
        if(raw == null) return "";

        String s = raw.trim().toLowerCase(Locale.ROOT).replaceAll(" +", " ");

        if(s.length() > 3 && s.endsWith("ies")){
            return s.substring(0, s.length()-3) + "y"; // berries -> berry
        }

        if (s.length() > 4 && (s.endsWith("oes") || s.endsWith("ches") || s.endsWith("shes")
                || s.endsWith("xes") || s.endsWith("sses"))){
            return s.substring(0, s.length() -2); //e.g., tomatoes -> tomato
        }

        if (s.length() > 3 && s.endsWith("s") && !s.endsWith("ss")){
            return s.substring(0, s.length() -1); //e.g., eggs -> egg
        }
        return s;
    }

    //checking if pantry contains enough ingredients to make a specific recipe
    public static boolean canMakeRecipe(Recipe recipe, List<Ingredient> pantry){
       // a recipe with no ingredients is null, and cannot be made
        if (recipe == null || recipe.getIngredients().isEmpty())
            return false;

        //loop through every ingredient required by recipe
        //get the unit category using the  Units utility class
        for (RecipeIngredient need : recipe.getIngredients()){
            int category = Units.category(need.getUnit());
            if (category == Units.UNKNOWN) return false;

            //calculate total amount of specific ingredient
            //standardise recipe's required quantity to its base unit
            double have = totalAvailable(pantry, need.getName(), category);
            double required = Units.toBase(need.getQuantity(), need.getUnit());

            //missing ingredients gives have == 0, so it is rejected here too.
            if (have + EPSILON < required) return false;
            }
        return true; //every ingredient passed
        }

        //filters a list of all recipes and returns only ones that can be made with current pantry
    public static List<Recipe> getMakeableRecipes(List<Recipe> all, List<Ingredient> pantry){
        List<Recipe> result = new ArrayList<>();

        //add makeable recipes to the result list
        for (Recipe recipe: all){
            if (canMakeRecipe(recipe, pantry)) result.add(recipe);
        }
        return result;
    }

    //method to calculate total base quantity of an ingredient
    private static double totalAvailable(List<Ingredient> pantry, String name, int category){
        String wanted = normalizeName(name);
        double total = 0;

        for (Ingredient item: pantry){
            //if standardised names match, convert the pantry item to its base unit and add it to the total
            if(normalizeName(item.getName()).equals(wanted) &&
                    Units.category(item.getUnit()) == category){
                total += Units.toBase(item.getQuantity(), item.getUnit());
            }
        }
        return total;
    }
}