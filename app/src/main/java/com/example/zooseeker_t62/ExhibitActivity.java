package com.example.zooseeker_t62;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

public class ExhibitActivity extends AppCompatActivity {

    public RecyclerView recyclerView;
    public ExhibitViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exhibit_list);

        viewModel = new ViewModelProvider(this)
                .get(ExhibitViewModel.class);

        ExhibitAdapter adapter = new ExhibitAdapter();
        adapter.setHasStableIds(true);
        adapter.setOnDeleteButtonClickedHandler(viewModel::deleteExhibit);
        viewModel.getExhibitItems().observe(this, adapter::setExhibitItems);

        recyclerView = findViewById(R.id.exhibit_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void onGoBackClicked(View view) {
        finish();
    }
}