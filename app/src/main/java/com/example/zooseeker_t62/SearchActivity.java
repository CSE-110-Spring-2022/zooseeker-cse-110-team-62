package com.example.zooseeker_t62;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.google.gson.JsonParser;

import org.json.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: This class represents the Activity where our ZooSeeker has a search bar to
 * find exhibits.
 */
public class SearchActivity extends AppCompatActivity {
    private ArrayList<String> activeAnimalNames = new ArrayList<String>();
    public ExhibitViewModel viewModel;

    /**
     * @description: Creates adapter which holds activeAnimalNames based on our search bar query
     * Also holds onClick Listener when textView item is clicked
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_page);

        viewModel = new ViewModelProvider(this)
                .get(ExhibitViewModel.class);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, activeAnimalNames);
        AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.search_bar);
        textView.setAdapter(adapter);
        textView.setThreshold(1);

        List<ExhibitItem> animals = ExhibitItem.loadJSON(this, "sample_node_info.json");

        // this listener gives you access to what is clicked
        textView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick (AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                Log.d("clickEvent", selection);
                onAddExhibitClicked(animals, selection);
            }
        });

        updateActiveAnimalNames(animals);
    }

    /**
     * @description: Iterates through tags, updates activeAnimalNames based on whether current tag
     * is already in our activeAnimalNames List
     */
    void updateActiveAnimalNames(List<ExhibitItem> animals) {
        try {
            JSONArray animalsArr = new JSONArray(animals.toString());
            for (int i = 0; i < animalsArr.length(); i++) {
                JSONObject currNode = animalsArr.getJSONObject(i);
                JSONArray tags = currNode.getJSONArray("tags");
                for(int j = 0 ; j < tags.length(); j++) {
                    String currTag = tags.getString(j);
                    if(!activeAnimalNames.contains(currTag)) {
                        activeAnimalNames.add(currTag);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

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