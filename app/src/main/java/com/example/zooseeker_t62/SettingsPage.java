package com.example.zooseeker_t62;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

public class SettingsPage extends AppCompatActivity {
    public static boolean routeType = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_page);

        Button home = (Button) findViewById(R.id.home_button);
        home.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsPage.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        ToggleButton toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.d("tag", "true");
                    setRouteType(true);
                } else {
                    setRouteType(false);
                    Log.d("tag", "false");
                }
            }
        });
    }

    public static void setRouteType(boolean state){
        routeType = state;
    }

    public static boolean getRouteType(){
        return routeType;
    }
}