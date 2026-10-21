package com.example.smartpantry.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.smartpantry.model.Ingredient;
import com.example.smartpantry.model.Recipe;
import com.example.smartpantry.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;


// database creation, version management, and CRUD operations for local database
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "smart_pantry.db";
    private static final int DB_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    //database settings before tables are created or accessed
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    //database tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE ingredients ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL, "
                + "quantity REAL NOT NULL, "
                + "unit TEXT NOT NULL, "
                + "expiry_date TEXT) ");

        db.execSQL("CREATE TABLE recipes ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL, "
                + "instructions TEXT NOT NULL)");

        db.execSQL("CREATE TABLE recipe_ingredients("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "recipe_id INTEGER NOT NULL, "
                + "ingredient_name TEXT NOT NULL, "
                + "quantity REAL NOT NULL, "
                + "unit TEXT NOT NULL, "
                + "FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE)");
    }

    //schema upgrades by dropping old tables and recreating them
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS recipe_ingredients");
        db.execSQL("DROP TABLE IF EXISTS recipes");
        db.execSQL("DROP TABLE IF EXISTS ingredients");
        onCreate(db);
    }

    //pantry ingredients and CRUD operations

    public long addIngredient(Ingredient i) {
        return getWritableDatabase().insert("ingredients", null, toValues(i));
    }

    public int updateIngredient(Ingredient i) {
        return getWritableDatabase().update("ingredients", toValues(i),
                "id = ?", new String[]{String.valueOf(i.getId())});
    }

    public int deleteIngredient(long id) {
        return getWritableDatabase().delete("ingredients", "id= ? ",
                new String[]{String.valueOf(id)});
    }

    public Ingredient getIngredient(long id) {
        try (Cursor c = getReadableDatabase().query("ingredients", null,
                "id=?", new String[]{String.valueOf(id)}, null, null,
                null)) {
            return c.moveToFirst() ? fromCursor(c) : null;

        }
    }

    //sort and retrieve all ingredients alphabetically
    public List<Ingredient> getAllIngredients() {
        List<Ingredient> list = new ArrayList<>();
        try (Cursor c = getReadableDatabase().query("ingredients", null, null,
                null, null, null, "name COLLATE NOCASE ASC")) {
            while (c.moveToNext()) list.add(fromCursor(c));
        }
        return list;
    }

    //map ingredient object into SQLite contentvalues
    private ContentValues toValues(Ingredient i) {
        ContentValues v = new ContentValues();

        v.put("name", i.getName());
        v.put("quantity", i.getQuantity());
        v.put("unit", i.getUnit());

        if (i.getExpiryDate() == null)
            v.putNull("expiry_date");
        else
            v.put("expiry_date", i.getExpiryDate());
        return v;
    }

    // reconstruct ingredient from  database cursor
    private Ingredient fromCursor(Cursor c) {
        int expiryCol = c.getColumnIndexOrThrow("expiry_date");
        return new Ingredient(
                c.getLong(c.getColumnIndexOrThrow("id")),
                c.getString(c.getColumnIndexOrThrow("name")),
                c.getDouble(c.getColumnIndexOrThrow("quantity")),
                c.getString(c.getColumnIndexOrThrow("unit")),
                c.isNull(expiryCol) ? null : c.getString(expiryCol)
        );
    }

    //Recipes CRUD operations

    //insert complete recipe with all required ingredients
    public long insertRecipe(Recipe recipe) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();

        try {
            //master recipe info
            ContentValues rv = new ContentValues();
            rv.put("name", recipe.getName());
            rv.put("instructions", recipe.getInstructions());
            long recipeId = db.insertOrThrow("recipes", null, rv);

            //loop and map individual ingredients tied to newly generated recipe ID
            for (RecipeIngredient ri : recipe.getIngredients()) {
                ContentValues iv = new ContentValues();
                iv.put("recipe_id", recipeId);
                iv.put("ingredient_name", ri.getName());
                iv.put("quantity", ri.getQuantity());
                iv.put("unit", ri.getUnit());
                db.insertOrThrow("recipe_ingredients", null, iv);
            }
            db.setTransactionSuccessful();
            return recipeId;
        } finally {
            db.endTransaction();
        }
    }

    //removing the recipe's ingredient rows automatically.
    public int deleteRecipe(long id) {
        return getWritableDatabase().delete("recipes",
                "id= ?", new String[]{String.valueOf(id)});
    }

    //retrieves all recipes from the database with respective ingredients lists
    public List<Recipe> getAllRecipes() {
        List<Recipe> list = new ArrayList<>();

        try (Cursor c = getReadableDatabase().query("recipes", null,
                null, null, null, null,
                "name COLLATE NOCASE ASC")) {

            while (c.moveToNext()) {
                Recipe r = recipeFromCursor(c);
                loadIngredients(r);
                list.add(r);
            }
        }
        return list;
    }

    public int countRecipes() {
        try (Cursor c = getReadableDatabase().rawQuery("SELECT COUNT(*) FROM recipes", null)) {
            c.moveToFirst();
            return c.getInt(0);
        }
    }

    //fetches single recipe by its ID alongside all associated ingredients
    public Recipe getRecipe(long id) {
        try (Cursor c = getReadableDatabase().query("recipes", null,
                "id=?", new String[]{String.valueOf(id)}, null,
                null, null)) {
            if (!c.moveToFirst())
                return null;

            Recipe r = recipeFromCursor(c);
            loadIngredients(r);
            return r;
        }
    }

    //method to map a base recipe object from database cursor row
    private Recipe recipeFromCursor(Cursor c) {
        return new Recipe(
                c.getLong(c.getColumnIndexOrThrow("id")),
                c.getString(c.getColumnIndexOrThrow("name")),
                c.getString(c.getColumnIndexOrThrow("instructions")));
    }

    //populate a recipe object with associated ingredients from recipe_ingredients table
    private void loadIngredients(Recipe recipe) {
        try (Cursor c = getReadableDatabase().query("recipe_ingredients", null,
                "recipe_id = ?", new String[]{String.valueOf(recipe.getId())},
                null, null, "id ASC")) {
            while (c.moveToNext()) {
                recipe.addIngredient(new RecipeIngredient(
                        c.getString(c.getColumnIndexOrThrow("ingredient_name")),
                        c.getDouble(c.getColumnIndexOrThrow("quantity")),
                        c.getString(c.getColumnIndexOrThrow("unit"))));
            }
        }
    }
}