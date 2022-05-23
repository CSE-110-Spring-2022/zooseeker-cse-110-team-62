package com.example.zooseeker_t62;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class ExhibitPlanner extends AppCompatActivity {
    public RecyclerView recyclerView;
    public ExhibitViewModel viewModel;

    /**
     * @description: The onCreate lifecycle for ExhibitActivity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exhibit_planner);

        TextView text = findViewById(R.id.exhibit_count2);

        recyclerView = findViewById(R.id.exhibit_items2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        viewModel = new ViewModelProvider(this)
                .get(ExhibitViewModel.class);

        ExhibitPlannerAdapter adapter = new ExhibitPlannerAdapter();

        adapter.setHasStableIds(true);
        recyclerView.setAdapter(adapter);
        viewModel.getExhibitItems().observe(this, adapter::setExhibitItems);
        adapter.setExhibitCount(text);
    }

    /**
     * @description: The onDestroy lifecycle of ExhibitActivity
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * @description: finish()ing the Activity when returning to the caller Activity
     */
    public void onGoBackClicked(View view) {
        finish();
    }
}
