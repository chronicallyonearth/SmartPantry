package com.example.smartpantry;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.materialswitch.MaterialSwitch;

// allow users to manage app preferences, such as toggling pantry expiry notifications.
public class SettingsActivity extends AppCompatActivity{

    //constant keys used for sharedpreferences storage
    public static final String PREFS_NAME = "smart_pantry_prefs";
    public static final String KEY_EXPIRY_ALERT = "expiry_alert";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //initialize the top app bar toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        //open the app's private sharedpreferences file to read/write settings data
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        MaterialSwitch toggle = findViewById(R.id.switchExpiryAlert);

        //load the saved state of the switch from preferences
        toggle.setChecked(prefs.getBoolean(KEY_EXPIRY_ALERT, true));

        //listen for user changes to the switch and immediately save the new true/false state
        toggle.setOnCheckedChangeListener((button, isChecked) ->
                prefs.edit().putBoolean(KEY_EXPIRY_ALERT, isChecked).apply());
    }
}