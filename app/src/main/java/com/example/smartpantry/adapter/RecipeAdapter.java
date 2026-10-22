package com.example.smartpantry.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.R;
import com.example.smartpantry.model.Recipe;

import java.util.ArrayList;
import java.util.List;

//adapter for binding list of recipe data models to item_recipe.xml layout
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    //delegate card selection click back to activity
    public interface Listener {
        void onRecipeClick(Recipe recipe);
    }

    private final List<Recipe> items = new ArrayList<>();
    private final Listener listener;

    //constructor requiring a click listener to handle item selection
    public RecipeAdapter(Listener listener) {
        this.listener = listener;
    }

    //replaces old dataset with new list of recipes and refreshes UI
    public void setItems(List<Recipe> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //inflate recipe card layout file into a live view object
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //retrieve recipe object corresponding to current visible row position
        final Recipe recipe = items.get(position);

        //bind data properties to respective view components inside the card
        holder.name.setText(recipe.getName());
        holder.meta.setText(recipe.getIngredients().size() + " ingredients. Tap for details.");

        //attach action listener to the card root view to open recipe details
        holder.itemView.setOnClickListener(v -> listener.onRecipeClick(recipe));
    }

    //return total number of recipe elements available for display
    @Override
    public int getItemCount() {
        return items.size();
    }

    //viewholder pattern object, should help with performance when scrolling fast,
    // hold reference linkages to child views in item_recipe.xml
    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView name, meta;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            //cache lookup bindings to prevent costly findViewById calls during scrolling
            name = itemView.findViewById(R.id.textRecipeName);
            meta = itemView.findViewById(R.id.textRecipeMeta);
        }
    }
}