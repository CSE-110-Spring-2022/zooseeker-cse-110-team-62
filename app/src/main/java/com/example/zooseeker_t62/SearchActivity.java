package com.example.zooseeker_t62;

import androidx.appcompat.app.AppCompatActivity;

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

public class SearchActivity extends AppCompatActivity {
    private ArrayList<String> activeAnimalNames = new ArrayList<String>();
    /**
     * @description: creates adapter which holds activeAnimalNames based on our search bar query
     * Also holds onClick Listener when textView item is clicked
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_page);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, activeAnimalNames);
        AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.search_bar);

        textView.setAdapter(adapter);
        textView.setThreshold(1);

        // TODO, leaving for Andrew & Sumu, this listener gives you access to what is clicked
        textView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick (AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                Log.d("clickEvent", selection);
            }
        });

        List<AnimalItem> animals = AnimalItem.loadJSON(this, "sample_node_info.json");

        updateActiveAnimalNames(animals);
    }
    /**
     * @description: iterates through tags, updates activeAnimalNames based on if current tag
     * is already in our activeAnimalNames List
     */
    void updateActiveAnimalNames(List<AnimalItem> animals) {
        try {
            JSONArray animalsArr = new JSONArray(animals.toString());
            for (int i = 0; i < animalsArr.length(); i++) {
                JSONObject currNode = animalsArr.getJSONObject(i);
                JSONArray tags = currNode.getJSONArray("tags");
                for(int j = 0 ; j < tags.length() ; j++){
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
     * @description: Proper activity cleanup when destroyed
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}