package com.example.zooseeker_t62;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

/**
 * @description: The Main Activity of ZooSeeker. Essentially unused
 */
public class MainActivity extends AppCompatActivity {
    /**
     * @description: Entry point of our app, links to SearchActivity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        SharedPreferences mPrefs = getSharedPreferences("IDvalue", MODE_PRIVATE);
        String str = mPrefs.getString("activity", "");


        Intent intent;
        if (str.equals("route")) {
            intent = new Intent(this, RouteDirectionsActivity.class);
        }
        else if (str.equals("exhibit"))
            intent = new Intent(this, ExhibitActivity.class);
        else if (str.equals("search"))
            intent = new Intent(this, SearchActivity.class);
        else {
            intent = new Intent(this, SearchActivity.class);
        }

        Log.d("activitystart", "act:" + str);


        startActivity(intent);
    }
}