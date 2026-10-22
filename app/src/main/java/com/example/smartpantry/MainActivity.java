package com.example.smartpantry;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.smartpantry.adapter.IngredientAdapter;
import com.example.smartpantry.database.DatabaseHelper;
import com.example.smartpantry.model.Ingredient;
import com.example.smartpantry.utils.RecipeSeeder;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements IngredientAdapter.Listener {

    private DatabaseHelper db;
    private IngredientAdapter adapter;
    private RecyclerView recycler;
    private TextView textEmpty, textBanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(this);
        RecipeSeeder.seedIfEmpty(db);

        //binding views from XML layout
        recycler = findViewById(R.id.recyclerIngredients);
        textEmpty = findViewById(R.id.textEmpty);
        textBanner = findViewById(R.id.textExpiryBanner);

        adapter = new IngredientAdapter(this);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        findViewById(R.id.fabAdd).setOnClickListener(v ->
                startActivity(new Intent(this, AddEditIngredientActivity.class)));
        findViewById(R.id.buttonRecipes).setOnClickListener(v ->
                startActivity(new Intent(this, SuggestedRecipesActivity.class)));
        findViewById(R.id.buttonSettings).setOnClickListener(v ->
                startActivity(new Intent(this, SettingsActivity.class)));
    }

    @Override
    protected void onResume(){
        super.onResume();
        refresh();
    }

    //fetching ingredients from db, pdate the list, and check for expiring items
    private void refresh(){
        List<Ingredient> list = db.getAllIngredients();
        adapter.setItems(list);

        boolean empty = list.isEmpty();
        textEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        recycler.setVisibility(empty ? View.GONE : View.VISIBLE);

        SharedPreferences prefs = getSharedPreferences(SettingsActivity.PREFS_NAME, MODE_PRIVATE);
        boolean alertsOn = prefs.getBoolean(SettingsActivity.KEY_EXPIRY_ALERT, true);
        int count = alertsOn ? countExpiringSoon(list) : 0;

        if (count > 0){
            textBanner.setText(count + (count == 1 ? " item expires" : " items expire") +
                    " within 3 days (or already expired).");
            textBanner.setVisibility(View.VISIBLE);
        }else{
            textBanner.setVisibility(View.GONE);
        }
    }

    //counting ingredients expiring in 3 days or have already expired
    private int countExpiringSoon(List<Ingredient> list) {
        LocalDate limit = LocalDate.now().plusDays(3);
        int count = 0;

        for(Ingredient item : list){
            String expiry = item.getExpiryDate();
            if (expiry == null || expiry.isEmpty())
                continue;

            try{
                if (!LocalDate.parse(expiry).isAfter(limit))
                    count++;
            }catch (DateTimeParseException ignored){
                //skip malformed dates instead of crashing.
            }
        }
        return count;
    }

    @Override
    public void onEdit(Ingredient ingredient){
        Intent intent = new Intent(this, AddEditIngredientActivity.class);
        intent.putExtra(AddEditIngredientActivity.EXTRA_INGREDIENT_ID, ingredient.getId());
        startActivity(intent);
    }

    @Override
    public void onDelete(Ingredient ingredient){
        new MaterialAlertDialogBuilder(this).setTitle("Delete ingredient?")
                .setMessage("Remove" + ingredient.getName() + " from your pantry?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (dialog, which) ->{
                    db.deleteIngredient(ingredient.getId());
                    refresh();
                })
                .show();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        db.close();
    }
}