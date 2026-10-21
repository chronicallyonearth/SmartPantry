package com.example.smartpantry.model;

import java.util.ArrayList;
import java.util.List;

public class Recipe{
    private final long id;
    private final String name;
    private final String instructions;
    private final List<RecipeIngredient> ingredients = new ArrayList<>();

    public Recipe(long id, String name, String instructions){
        this.id=id;
        this.name=name;
        this.instructions=instructions;
    }

    public void addIngredient(RecipeIngredient item) {ingredients.add(item);}

    public long getId(){return id;}
    public String getName(){return name;}
    public String getInstructions(){return instructions;}
    public List<RecipeIngredient> getIngredients(){return ingredients;}

}
