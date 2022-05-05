package com.example.zooseeker_t62;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

public class RouteDirectionsActivity extends AppCompatActivity {

    public ExhibitViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_directions);

        viewModel = new ViewModelProvider(this)
                .get(ExhibitViewModel.class);

        ExhibitAdapter adapter = new ExhibitAdapter();
        adapter.setHasStableIds(true);


        List<ExhibitItem> routeList = viewModel.getList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
