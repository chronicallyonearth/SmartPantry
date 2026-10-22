package com.example.smartpantry.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.R;
import com.example.smartpantry.model.Ingredient;
import com.example.smartpantry.utils.Units;

import java.util.ArrayList;
import java.util.List;

//adapter responsible for binding ingredient data to RecyclerView UI items
public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.ViewHolder> {
    //edit and delete action events
    public interface Listener {
        void onEdit(Ingredient ingredient);
        void onDelete(Ingredient ingredient);
    }

    private final List<Ingredient> items = new ArrayList<>();
    private final Listener listener;

    //constructor for a click listener to handle item actions
    public IngredientAdapter(Listener listener) {
        this.listener = listener;
    }

    //replace old dataset with new list of ingredients and refreshes UI
    public void setItems(List<Ingredient> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate single item xml file into live view object
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ingredient, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //retrieve data object corresponding to current visible row position
        final Ingredient item = items.get(position);

        //binding core data properties to respective view components
        holder.name.setText(item.getName());
        holder.quantity.setText(Units.formatQuantity(item.getQuantity()) + " " + item.getUnit());

        String expiry = item.getExpiryDate();

        //handle expiry label visibility state based on whether a date exists
        if (expiry == null || expiry.isEmpty()) {
            holder.expiry.setVisibility(View.GONE); //hide view and release layout space
        } else {
            holder.expiry.setVisibility(View.VISIBLE);
            holder.expiry.setText("Expires: " + expiry);
        }

        //attach action listeners to invoke callback methods when buttons are tapped
        holder.edit.setOnClickListener(v -> listener.onEdit(item));
        holder.delete.setOnClickListener(v -> listener.onDelete(item));
    }

    //return total number of data elements available to list
    @Override
    public int getItemCount() {
        return items.size();
    }

    //viewholder pattern object, should help with performance when scrolling fast,
    // hold reference linkages to child views in item_ingredients.xml
    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView name, quantity, expiry;
        final Button edit, delete;

        //
        ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.textName);
            quantity = itemView.findViewById(R.id.textQuantity);
            expiry = itemView.findViewById(R.id.textExpiry);
            edit = itemView.findViewById(R.id.buttonEdit);
            delete = itemView.findViewById(R.id.buttonDelete);
        }
    }
}