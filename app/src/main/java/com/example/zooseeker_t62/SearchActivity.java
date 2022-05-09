package com.example.zooseeker_t62;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonParser;

import org.json.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: This class represents the Activity where our ZooSeeker has a search bar to
 * find exhibits.
 */
public class SearchActivity extends AppCompatActivity {
    private SearchAdapter adapter;
    private List<ExhibitItem> searchList;
    private SearchAdapter.RecyclerViewClickListener listener;
//    private ArrayList<String> activeAnimalNames = new ArrayList<String>();
    public ExhibitViewModel viewModel;


    /**
     * @description: Creates adapter which holds activeAnimalNames based on our search bar query
     * Also holds onClick Listener when textView item is clicked
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_page);

        searchList = ExhibitItem.loadJSON(this, "sample_node_info.json");
        viewModel = new ViewModelProvider(this).get(ExhibitViewModel.class);
        initRecyclerView();
//        setOnClickListener();






//        recyclerView.addOnItemTouchListener(
//                new RecyclerItemClickListener(this, recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
//                    @Override public void onItemClick(View view, int position) {
////                        RecyclerView.ViewHolder v = recyclerView.findViewHolderForAdapterPosition(position);
////                        String selection = (String) view.getItemAtPosition(position);
//
//                        String title = ((TextView) recyclerView.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.recycler_view)).getText().toString();
//                        Toast.makeText(getApplicationContext(), title, Toast.LENGTH_SHORT).show();
//                        Log.d("test", "clicked");
//                    }
//
//                    @Override public void onLongItemClick(View view, int position) {
//                        // do whatever
//                    }
//                })
//        );

//        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
//            @Override
//            public void onClick(View view, int position) {
//                String title = ((TextView) recyclerView.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.title)).getText().toString();
//                Toast.makeText(getApplicationContext(), title, Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onLongClick(View view, int position) { }
//        }));



//
//        viewModel = new ViewModelProvider(this)
//                .get(ExhibitViewModel.class);
//
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, activeAnimalNames);
//        AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.search_bar);
//        textView.setAdapter(adapter);
//        textView.setThreshold(1);
//
//        List<ExhibitItem> animals = ExhibitItem.loadJSON(this, "sample_node_info.json");

        // this listener gives you access to what is clicked
//        textView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick (AdapterView<?> parent, View view, int position, long id) {
//                String selection = (String) parent.getItemAtPosition(position);
//                Log.d("clickEvent", selection);
//                onAddExhibitClicked(animals, selection);
//            }
//        });
//
//        updateActiveAnimalNames(animals);
    }

//    private void setOnClickListener() {
//        listener = new SearchAdapter.RecyclerViewClickListener() {
//            @Override
//            public void onClick(View v, int position) {
//                String selection = searchList.get(position).id;
//            }
//        };
//    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        adapter = new SearchAdapter(searchList, listener, this);

        adapter.setOnExhibitClicked(viewModel::createExhibitFromList);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                Log.d("test", query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
//        searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick (AdapterView<?> parent, View view, int position, long id) {
//                String selection = (String) parent.getItemAtPosition(position);
//                Log.d("clickEvent", selection);
//                onAddExhibitClicked(animals, selection);
//            }
//        });
//        return true;
    }



//    /**
//     * @description: Iterates through tags, updates activeAnimalNames based on whether current tag
//     * is already in our activeAnimalNames List
//     */
//    void updateActiveAnimalNames(List<ExhibitItem> animals) {
//        try {
//            JSONArray animalsArr = new JSONArray(animals.toString());
//            for (int i = 0; i < animalsArr.length(); i++) {
//                JSONObject currNode = animalsArr.getJSONObject(i);
//                JSONArray tags = currNode.getJSONArray("tags");
//                for(int j = 0 ; j < tags.length(); j++) {
//                    String currTag = tags.getString(j);
//                    if(!activeAnimalNames.contains(currTag)) {
//                        activeAnimalNames.add(currTag);
//                    }
//                }
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * @description: Behavior for when an exhibit is clicked and meant to be added
     */
    public void onAddExhibitClicked(List<ExhibitItem> animals, String tag) {
        try {
            JSONArray animalsArr = new JSONArray(animals.toString());
            for (int i = 0; i < animalsArr.length(); i++) {
                JSONObject currNode = animalsArr.getJSONObject(i);
                JSONArray tags = currNode.getJSONArray("tags");
                for(int j = 0 ; j < tags.length(); j++) {
                    String currTag = tags.getString(j);
                    if (currTag.equals(tag)) {
                        String id = currNode.getString("id");
                        String kind = currNode.getString("kind");
                        String name = currNode.getString("name");
                        String[] stringTags = new String[tags.length()];

                        Log.d("onAddExhibitClicked", "stringTags.length: " + stringTags.length);

                        for (int k = 0; k < stringTags.length; k++) {
                            stringTags[k] = tags.getString(k);
                        }

                        Log.d("onAddExhibitClicked", "id: " + id);
                        Log.d("onAddExhibitClicked", "kind: " + kind);
                        Log.d("onAddExhibitClicked", "name: " + name);
                        Log.d("onAddExhibitClicked", "stringTags: " + stringTags.toString());

                        viewModel.createExhibit(id, kind, name, stringTags);
                        Utilities.showAlert(this, "Press OK to keep adding to the plan.");
                        Log.d("onAddExhibitClicked", "created exhibit " + id);

                        break;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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