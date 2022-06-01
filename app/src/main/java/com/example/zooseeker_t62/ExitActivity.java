package com.example.zooseeker_t62;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

public class ExitActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exit);

        SharedPreferences mPrefs = this.getSharedPreferences("IDvalue", MODE_PRIVATE);
        SharedPreferences.Editor editor2 = mPrefs.edit();
        editor2.putString("activity", "search");
        editor2.apply();
    }
    /**
     * @description: onClick going back to home
     */
    public void onHomeClicked(View view) {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
        finish();
    }
}