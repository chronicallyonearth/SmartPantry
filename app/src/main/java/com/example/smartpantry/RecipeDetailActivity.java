package com.example.smartpantry;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantry.database.DatabaseHelper;
import com.example.smartpantry.model.Recipe;
import com.example.smartpantry.model.RecipeIngredient;
import com.example.smartpantry.utils.Units;
import com.google.android.material.appbar.MaterialToolbar;

// activity that displays details profile specifications of a recipe, including dynamic compilation
// of a raw text blocks for instructions and bulleted ingredients
public class RecipeDetailActivity extends AppCompatActivity{
    public static final String EXTRA_RECIPE_ID = "recipe_id";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        //configure top toolbar and back navigation
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // extract target key arguments passed from the navigation context
        long recipeId = getIntent().getLongExtra(EXTRA_RECIPE_ID, -1);

        //extract model records safely
        DatabaseHelper db = new DatabaseHelper(this);
        Recipe recipe = db.getRecipe(recipeId);
        db.close();

        //safety assertion for non-existent objects or database
        if (recipe == null){
            Toast.makeText(this, "Recipe not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        //generate a formatted, readable bulleted text block for layout
        StringBuilder list = new StringBuilder();

        for (RecipeIngredient item : recipe.getIngredients()){
            if (list.length() > 0) list.append(System.lineSeparator());

            list.append("• ").append(item.getName()).append(": ")
                    .append(Units.formatQuantity(item.getQuantity()))
                    .append(" ").append(item.getUnit());
        }

        //render processed textual model layers directly into active visual layouts
        ((TextView) findViewById(R.id.textRecipeTitle)).setText(recipe.getName());
        ((TextView) findViewById(R.id.textIngredients)).setText(list.toString());
        ((TextView) findViewById(R.id.textInstructions)).setText(recipe.getInstructions());
    }
}