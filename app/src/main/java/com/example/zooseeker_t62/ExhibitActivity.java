package com.example.zooseeker_t62;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

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

        recyclerView = findViewById(R.id.exhibit_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        viewModel = new ViewModelProvider(this)
                .get(ExhibitViewModel.class);

        ExhibitAdapter adapter = new ExhibitAdapter();
        adapter.setHasStableIds(true);
        adapter.setOnDeleteButtonClickedHandler(viewModel::deleteExhibit);
        recyclerView.setAdapter(adapter);
        viewModel.getExhibitItems().observe(this, adapter::setExhibitItems);
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