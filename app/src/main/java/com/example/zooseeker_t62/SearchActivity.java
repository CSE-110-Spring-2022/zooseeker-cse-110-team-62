package com.example.zooseeker_t62;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.google.gson.JsonParser;

import org.json.*;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private ArrayList<String> animalNames = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_page);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, animalNames);
        AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.search_bar);

        textView.setAdapter(adapter);
        textView.setThreshold(1);

        List<AnimalItem> animals = AnimalItem.loadJSON(this, "sample_node_info.json");
        Log.d("Animal List ", animals.toString() );

        try {
            JSONArray jsonArray = new JSONArray(animals.toString());

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String kind = jsonObject.getString("kind");
                //if (kind.equals("exhibit")){
                    JSONArray tags = jsonObject.getJSONArray("tags");
                    for(int j = 0 ; j < tags.length() ; j++){
                        String tagName = tags.getString(j);
                        if(!contains(animalNames, tagName)){
                            animalNames.add(tagName);
                        }
                    }
                    //String animalName = jsonObject.getString("name");
                    //animalNameafs.add(animalName);
                //}
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    boolean contains(ArrayList<String> arr, String string){
        boolean hasTag = false;
        for(int i = 0 ; i < arr.size() ; i++){
            if(arr.get(i).equals(string)){
                hasTag = true;
            }
        }
        return hasTag;
    }
}