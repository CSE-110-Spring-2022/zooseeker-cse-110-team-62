package com.example.zooseeker_t62;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @description: Class that holds structure for node data in JSON
 */
@Entity(tableName = "exhibit_list_items")
public class ExhibitItem {
    @PrimaryKey(autoGenerate = true)
    public long long_id;

    @NonNull
    public String id, kind, name;

    @NonNull
    public String[] tags;

    /**
     * @description: The constructor
     */
    public ExhibitItem(String id, String kind, String name, String[] tags){
        this.id = id;
        this.kind = kind;
        this.name = name;
        this.tags = tags;
    }

    /**
     * @description: The String representation of the ExhibitItem
     */
    @Override
    public String toString() {
        return "{" +
                "id='" + id + '\'' +
                ", kind='" + kind + '\'' +
                ", name='" + name + '\'' +
                ", tags=" + Arrays.toString(tags) +
                '}';
    }

    public String getName() {
        return this.name;
    }

    /**
     * @description: loads JSON from path and returns object
     */
    public static List<ExhibitItem> loadJSON(Context context, String path){
        try {
            InputStream input = context.getAssets().open(path);
            Reader reader = new InputStreamReader(input);

            Gson gson = new Gson();
            Type type = new TypeToken<List<ExhibitItem>>(){}.getType();

            List<ExhibitItem> animals = gson.fromJson(reader, type);

            return animals;
        } catch (IOException e) {
            e.printStackTrace();

            return Collections.emptyList();
        }
    }
}
