package com.example.zooseeker_t62;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.google.gson.JsonParser;

import org.json.*;

import java.util.List;

public class SearchPage extends AppCompatActivity {
    private String animals[] = new String[]{"Black bear","Grizzly Bear",  "Beetle", "Giraffe", "Goat", "Tiger"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_page);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, animals);
        AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.search_bar);

        textView.setAdapter(adapter);
        textView.setThreshold(1);

        List<AnimalItem> animals = AnimalItem.loadJSON(this, "sample_node_info.json");
        Log.d("Animal List ", animals.toString() );

        

    }


}