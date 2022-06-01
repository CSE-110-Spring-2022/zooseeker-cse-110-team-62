package com.example.zooseeker_t62;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @description: This class represents the Activity where our ZooSeeker has a search bar to
 * find exhibits.
 */
public class SearchActivity extends AppCompatActivity {
    private SearchAdapter adapter;
    private List<ExhibitItem> searchList;
    public ExhibitViewModel viewModel;

    /**
     * @description: loadsSearchList Data --> creates viewModel --> creates recycler view
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_page);


        List<ExhibitItem> animals = ExhibitItem.loadJSON(this, "sample_ms2_exhibit_info.json");
        searchList = animals.stream().filter(animal -> animal.getKind().equals("exhibit") || animal.getKind().equals("exhibit_group")).collect(Collectors.toList());

        viewModel = new ViewModelProvider(this).get(ExhibitViewModel.class);
        initRecyclerView();

        Button settings = (Button) findViewById(R.id.settings_button);
        settings.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent = new Intent(SearchActivity.this, SettingsPage.class);
                startActivity(settingsIntent);
            }
        });
    }

    /**
     * @description: Binds adapter to recycler view and creates setOnExhibitClicked listener
     */
    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        adapter = new SearchAdapter(searchList, this);

        adapter.setOnExhibitClicked(viewModel::createExhibitFromList);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    /**
     * @description: Options menu at top of screen that has our search icon functionality
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            /**
             * @description: When user presses enter for a search
             */
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            /**
             * @description: When query text changes we re-filter recycler view
             */
            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }
    /**
     * @description: Handles the opening of the new plan list Activity
     */
    public void onLaunchExhibitListClick(View view) {
        Intent intent = new Intent(this, ExhibitActivity.class);
        startActivity(intent);
    }
    /**
     * @description: Proper activity cleanup when destroyed
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}