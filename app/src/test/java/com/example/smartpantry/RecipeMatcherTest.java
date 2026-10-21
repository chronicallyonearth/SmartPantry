package com.example.smartpantry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.smartpantry.model.Ingredient;
import com.example.smartpantry.model.Recipe;
import com.example.smartpantry.model.RecipeIngredient;
import com.example.smartpantry.utils.RecipeMatcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

/**
 * Unit tests to verify that the RecipeMatcher logic works accurately
 * across different pantry scenarios.
 */
public class RecipeMatcherTest {

    // Helper method: Creates a mock 'Pancakes' recipe requiring 2 eggs, 200g flour, and 250ml milk
    private Recipe pancakes() {
        Recipe r = new Recipe(1, "Pancakes", "Mix and fry.");
        r.addIngredient(new RecipeIngredient("Egg", 2, "pieces"));
        r.addIngredient(new RecipeIngredient("Flour", 200, "g"));
        r.addIngredient(new RecipeIngredient("Milk", 250, "ml"));
        return r;
    }

    // Helper method: Quickly builds an Ingredient object with custom values to avoid repetitive code
    private Ingredient ing(String name, double qty, String unit) {
        return new Ingredient(0, name, qty, unit, null);
    }

    // Helper method: Converts a variable list of ingredients into a standard List array
    private List<Ingredient> pantry(Ingredient... items) {
        return new ArrayList<>(Arrays.asList(items));
    }

    // Test: Verifies that if you have the exact or more quantities of every required item, you can make the recipe
    @Test public void everythingPresent_matches() {
        assertTrue(RecipeMatcher.canMakeRecipe(pancakes(),
                pantry(ing("eggs", 2, "pieces"), ing("flour", 500, "g"),
                        ing("milk", 250, "ml"))));
    }

    // Test: Verifies that if an entire required ingredient is missing completely (Milk), it is rejected
    @Test public void missingIngredient_rejects() {
        assertFalse(RecipeMatcher.canMakeRecipe(pancakes(),
                pantry(ing("eggs", 2, "pieces"), ing("flour", 500, "g"))));
    }

    // Test: Verifies that if you have an ingredient but the quantity is too low (100g instead of 200g flour), it fails
    @Test public void tooLittleFlour_rejects() {
        assertFalse(RecipeMatcher.canMakeRecipe(pancakes(),
                pantry(ing("eggs", 2, "pieces"), ing("flour", 100, "g"),
                        ing("milk", 250, "ml"))));
    }

    // Test: Verifies that having extra inventory or random unrelated items (cheese) won't block you from cooking
    @Test public void extraIngredients_doNotBlock() {
        assertTrue(RecipeMatcher.canMakeRecipe(pancakes(),
                pantry(ing("eggs", 6, "pieces"), ing("flour", 500, "g"),
                        ing("milk", 1000, "ml"), ing("cheese", 100, "g"))));
    }

    // Test: Verifies that string normalization strips spaces/plurals so " TOMATOES " matches "Tomato"
    @Test public void pluralAndCaseDifferences_match() {
        Recipe r = new Recipe(2, "Salad", "Chop.");
        r.addIngredient(new RecipeIngredient("Tomato", 3, "pieces"));
        assertTrue(RecipeMatcher.canMakeRecipe(r, pantry(ing(" TOMATOES ",
                4, "pieces"))));
    }

    // Test: Verifies that weight units convert cleanly (e.g. 1kg converts to 1000g and satisfies a 1000g requirement)
    @Test public void compatibleUnits_areConverted() {
        Recipe r = new Recipe(3, "Bread", "Bake.");
        r.addIngredient(new RecipeIngredient("Flour", 1000, "g"));

        // 1 kg is enough (equals 1000g)
        assertTrue(RecipeMatcher.canMakeRecipe(r, pantry(ing("Flour", 1, "kg"))));
        // 0.5 kg is not enough (equals 500g)
        assertFalse(RecipeMatcher.canMakeRecipe(r, pantry(ing("Flour", 0.5, "kg"))));
    }

    // Test: Verifies that incompatible unit categories (Mass vs Volume) don't mix, even if numbers are equal
    @Test public void incompatibleUnits_areNotInterchangeable() {
        Recipe r = new Recipe(4, "Drink", "Pour.");
        r.addIngredient(new RecipeIngredient("Milk", 200, "ml"));

        // 200g is mass, recipe expects ml (volume). Should fail.
        assertFalse(RecipeMatcher.canMakeRecipe(r, pantry(ing("Milk", 200, "g"))));
    }

    // Test: Verifies that split pantry entries are added together (100g + 150g = 250g, satisfying a 200g need)
    @Test public void duplicatePantryRows_areSummed() {
        Recipe r = new Recipe(5, "Cake", "Bake.");
        r.addIngredient(new RecipeIngredient("Flour", 200, "g"));
        assertTrue(RecipeMatcher.canMakeRecipe(r, pantry(ing("Flour", 100,
                "g"), ing("flour", 150, "g"))));
    }

    // Test: Verifies that an completely empty pantry returns 0 valid recipes
    @Test public void emptyPantry_matchesNothing() {
        List<Recipe> all = Arrays.asList(pancakes());
        assertEquals(0, RecipeMatcher.getMakeableRecipes(all, new
                ArrayList<Ingredient>()).size());
    }
}
