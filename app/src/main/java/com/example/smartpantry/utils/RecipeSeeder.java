package com.example.smartpantry.utils;

import com.example.smartpantry.database.DatabaseHelper;
import com.example.smartpantry.model.Recipe;
import com.example.smartpantry.model.RecipeIngredient;

//class responsible for populating the database with default recipe data
public final class RecipeSeeder {
    //prevent instantiation
    private RecipeSeeder() {}

    //seed the database with a default list of recipes if none exist
    public static void seedIfEmpty(DatabaseHelper db) {
        if (db.countRecipes() > 0) return;

        //Recipe 1: Pancakes
        add(db, "Pancakes",
                steps("Whisk the eggs, flour and milk into bowl to a lumpy consistency.",
                        "Heat a lightly oiled pan.",
                        "cook a ladle of batter for 2 minutes per side."),
                new RecipeIngredient("Egg", 2, "pieces"),
                new RecipeIngredient("Flour", 200, "g"),
                new RecipeIngredient("Milk", 250, "ml"));

        //Recipe 2: cheese omelette
        add(db, "Cheese Omelette",
                steps("Beat the eggs.",
                        "Pour eggs into a hot buttered pan.",
                        "Add the grated cheese, fold and serve."),
                new RecipeIngredient("Egg", 2, "pieces"),
                new RecipeIngredient("Cheese", 50, "g"));

        //recipe 3: pizza dough
        add(db, "Pizza Dough",
                steps("Dissolve the yeast in warm water for 5 minutes.",
                        "Mix in the flour and knead for 10 minutes.",
                        "Let it rise for an hour, then shape and bake."),
                new RecipeIngredient("Flour", 300, "g"),
                new RecipeIngredient("Yeast", 7, "g"),
                new RecipeIngredient("Water", 200, "ml"));

        //recipe 4: cheese pasta
        add(db, "Cheese Pasta",
                steps("Boil the pasta until tender.",
                        "Drain and stir in the grated cheese until melted."),
                new RecipeIngredient("Pasta", 200, "g"),
                new RecipeIngredient("Cheese", 100, "g"));

        // recipe 5: tomato pasta
        add(db, "Tomato Pasta",
                steps("Boil the pasta until tender.",
                        "Simmer chopped tomatoes in olive oil for 10 minutes.",
                        "toss the pasta through the sauce."),
                new RecipeIngredient("Pasta", 200, "g"),
                new RecipeIngredient("Tomato", 3, "pieces"),
                new RecipeIngredient("Olive oil", 15, "ml"));

        //recipe 6 scrambled eggs
        add(db, "Scrambled Eggs",
                steps("Whisk the eggs with the milk.",
                        "Melt the butter in a pan on low heat.",
                        "Stir gently until just set."),
                new RecipeIngredient("Eggs", 3, "pieces"),
                new RecipeIngredient("Butter", 10, "g"),
                new RecipeIngredient("Milk", 30, "ml"));

        //recipe 7: egg fried rice
        add(db, "Egg fried Rice",
                steps("Scramble the eggs and set them aside.",
                        "Fry the cooked rice until it starts to crisps.",
                        "Return the eggs and add the soy sauce."),
                new RecipeIngredient("Rice", 200, "g"),
                new RecipeIngredient("Egg", 2, "pieces"),
                new RecipeIngredient("Soy sauce", 15, "ml"));

        // recipe 8: kota
        add(db, "Kota",
                steps("Fry potato chips in hot oil until golden and crispy.",
                        "Fry or braai Russian sausage and polony slices until warm and slightly charred.",
                        "Cut a quarter loaf of bread and gently push or scoop the soft interior to make a deep pocket.",
                        "Layer the atchar, polony, russian, cheese and hot chips."),
                new RecipeIngredient("1/4 Loaf", 1, "pieces"),
                new RecipeIngredient("Potato Chips", 100, "g"),
                new RecipeIngredient("Russian sausages", 1, "piece"),
                new RecipeIngredient("Polony Slices", 1, "pieces"),
                new RecipeIngredient("Atchar", 15, "g"));
    }

    // helper method to construct recipe object, attach ingredients, and insert into database
    private static void add(DatabaseHelper db, String name, String instructions, RecipeIngredient... items) {
        Recipe recipe = new Recipe(0, name, instructions);

        for (RecipeIngredient item : items)
            recipe.addIngredient(item);

        db.insertRecipe(recipe);
    }

    // helper methode to combine individual instruction lines into single string
    private static String steps(String... lines) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < lines.length; i++) {
            if (i > 0) {
                sb.append(System.lineSeparator());
            }
            sb.append(lines[i]);
        }
        return sb.toString();
    }
}