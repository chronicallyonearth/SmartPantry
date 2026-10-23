package com.example.smartpantry;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.adapter.RecipeAdapter;
import com.example.smartpantry.database.DatabaseHelper;
import com.example.smartpantry.model.Ingredient;
import com.example.smartpantry.model.Recipe;
import com.example.smartpantry.utils.RecipeMatcher;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

//activity that displays a list of recipes the user can cook based on the ingredients currently
// available in their pantry
public class SuggestedRecipesActivity extends AppCompatActivity implements RecipeAdapter.Listener{

    // database ui and layout dependencies
    private DatabaseHelper db;
    private RecipeAdapter adapter;
    private RecyclerView recycler;
    private TextView textEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_suggested_recipes);

        db = new DatabaseHelper(this);

        // configure top toolbar and back nav
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        recycler = findViewById(R.id.recyclerRecipes);
        textEmpty = findViewById(R.id.textEmptyRecipes);

        //initialize and bind recyclerView adapter with a linear layout manager
        adapter = new RecipeAdapter(this);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);
    }

    @Override
    protected void onResume(){
        super.onResume();

        // live data from the database
        List<Ingredient> pantry = db.getAllIngredients();

        //filter out all recipes that match the current inventory limits
        List<Recipe> makeable = RecipeMatcher.getMakeableRecipes(db.getAllRecipes(), pantry);

        //update data items inside the recycler view adapter
        adapter.setItems(makeable);

        //toggle visibility layout panels depending on match results
        if (makeable.isEmpty()){
           textEmpty.setText(pantry.isEmpty() ?
                   "Your pantry is empty. Add ingredients to see recipe suggestions." :
                   "No recipe can be made with " + "what you have right now. "
           + "Add the missing ingredients or increase your quantities.");

           //show empty state placeholder and hide the list view
           textEmpty.setVisibility(View.VISIBLE);
           recycler.setVisibility(View.GONE);
        }else{
            //show list view and hide empty state placeholder
            textEmpty.setVisibility(View.GONE);
            recycler.setVisibility(View.VISIBLE);
        }
    }

    //triggered when a recipe row item is clicked
    @Override
    public void onRecipeClick(Recipe recipe){
        Intent intent = new Intent(this, RecipeDetailActivity.class);

        //pass targeted item identifier to the target display activity
        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.getId());
        startActivity(intent);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        db.close();
    }
}