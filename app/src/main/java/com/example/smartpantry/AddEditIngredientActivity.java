package com.example.smartpantry;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantry.database.DatabaseHelper;
import com.example.smartpantry.model.Ingredient;
import com.example.smartpantry.utils.Units;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Locale;

public class AddEditIngredientActivity extends AppCompatActivity {
    //extra key used to pass an existing ingredients ID via Intent
    public static final String EXTRA_INGREDIENT_ID = "ingredient_id";

    private DatabaseHelper db;
    private TextInputLayout layoutName, layoutQuantity;
    private TextInputEditText editName, editQuantity, editExpiry;
    private Spinner spinnerUnit;

    // holds the ID of the ingredient being edited, -1 to create new ingredient
    private long ingredientId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);

        db = new DatabaseHelper(this);

        // setup top action bar with back/navigation button
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        layoutName = findViewById(R.id.inputLayoutName);
        layoutQuantity = findViewById(R.id.inputLayoutQuantity);
        editName = findViewById(R.id.editName);
        editQuantity = findViewById(R.id.editQuantity);
        editExpiry = findViewById(R.id.editExpiry);
        spinnerUnit = findViewById(R.id.spinnerUnit);

        // populate the unit dropdown spinner with available units
        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, Units.ALL);

        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnit.setAdapter(unitAdapter);

        // check if an ingredient ID is passed in the intent
        ingredientId = getIntent().getLongExtra(EXTRA_INGREDIENT_ID, -1);

        if (ingredientId != 1){
            Ingredient existing = db.getIngredient(ingredientId);

            if (existing == null){
                Toast.makeText(this, "Ingredient not found", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            // populate fields with existing data
            toolbar.setTitle("Edit Ingredient");
            editName.setText(existing.getName());

            editQuantity.setText(Units.formatQuantity(existing.getQuantity()));

            int unitIndex = Arrays.asList(Units.ALL).indexOf(existing.getUnit());

            if (unitIndex >= 0)
                spinnerUnit.setSelection(unitIndex);

            if (existing.getExpiryDate() != null)
                editExpiry.setText(existing.getExpiryDate());
        }

        // set up click listeners for date picking, clearing, cancelling, cancelling and saving
        editExpiry.setOnClickListener(v -> showDatePicker());
        findViewById(R.id.buttonClearDate).setOnClickListener(v -> editExpiry.setText(""));
        findViewById(R.id.buttonCancel).setOnClickListener(v -> finish());
        findViewById(R.id.buttonSave).setOnClickListener(v -> save());
    }

    // opens a DatePickerDialog to select an expiry date
    private void showDatePicker(){
        LocalDate initial = LocalDate.now();
        String current = text(editExpiry);

        if(!current.isEmpty()){
            try{
                initial = LocalDate.parse(current);
            }catch (DateTimeParseException ignored){
                //fall back to today
            }
        }

        new DatePickerDialog(this, (view, year, month, day) ->
                editExpiry.setText(String.format(Locale.US, "%04d-%02d-%02d",
                        year, month + 1, day)), initial.getYear(),
                initial.getMonthValue() - 1, initial.getDayOfMonth()).show();
    }

    //validate user input and saves or updates the ingredient in the database
    private void save(){
        boolean valid = true;

        String name = text(editName);

        if (name.isEmpty()){
            layoutName.setError("Please enter an ingredient name");
            valid = false;
        }else{
            layoutName.setError(null);
        }

        double quantity = 0;

        try{
            quantity = Double.parseDouble(text(editQuantity));

            if (Double.isNaN(quantity) || Double.isInfinite(quantity) || quantity <= 0){
                layoutQuantity.setError("Quantity must be greater than 0");
                valid = false;
            }else{
                layoutQuantity.setError(null);
            }
        } catch (NumberFormatException e) {
            layoutQuantity.setError("Enter a valid number");
            valid = false;
        }

        if (!valid)
            return;

        // retrieve unit and expiry date
        String unit = (String) spinnerUnit.getSelectedItem();
        String expiry = text(editExpiry);

        //ingredient model object
        Ingredient item = new Ingredient(
                ingredientId == -1 ? 0 : ingredientId, name, quantity, unit,
                expiry.isEmpty() ? null : expiry);

        // insert new record or update existing record
        if (ingredientId == -1)
            db.addIngredient(item);
        else
            db.updateIngredient(item);

        finish();
    }

    //method to safely extract and trim text from a TextInputEditText
    private String text(TextInputEditText field) {
        return field.getText() == null ? "" : field.getText().toString().trim();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        db.close();
    }
}