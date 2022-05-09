package com.example.zooseeker_t62;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * @description: Class that manages the "Plan" list within our ZooSeeker app.
 */
public class ExhibitActivity extends AppCompatActivity {
    public RecyclerView recyclerView;
    public ExhibitViewModel viewModel;

    /**
     * @description: The onCreate lifecycle for ExhibitActivity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exhibit_list);

        TextView text = findViewById(R.id.exhibit_count);


        recyclerView = findViewById(R.id.exhibit_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        viewModel = new ViewModelProvider(this)
                .get(ExhibitViewModel.class);

        ExhibitAdapter adapter = new ExhibitAdapter();

        try {
            String graph_string = loadGraphString();
            JSONObject graph_json = new JSONObject(graph_string);
            JSONArray edges = graph_json.getJSONArray("edges");
            adapter.setEdges(edges);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        adapter.setHasStableIds(true);
        adapter.setOnDeleteButtonClickedHandler(viewModel::deleteExhibit);
        recyclerView.setAdapter(adapter);
        viewModel.getExhibitItems().observe(this, adapter::setExhibitItems);
        adapter.setExhibitCount(text);
    }

    public String loadGraphString() throws IOException {
        InputStream inputStream = this.getAssets().open("sample_zoo_graph.json");
        int size = inputStream.available();
        byte[] buffer = new byte[size];

        inputStream.read(buffer);
        inputStream.close();
        String graph_string = new String(buffer, "UTF-8");

        return graph_string;
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
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }
    /**
     * @description: Handles the opening of the new direction/route Activity
     */
    public void onDirectionsClick(View view) {
        if (recyclerView.getAdapter().getItemCount() <= 0) {
            Utilities.showAlert(this, "No exhibits have been added to the plan!");
            return;
        }
        Intent intent = new Intent(this, RouteDirectionsActivity.class);
        startActivity(intent);
    }
}
