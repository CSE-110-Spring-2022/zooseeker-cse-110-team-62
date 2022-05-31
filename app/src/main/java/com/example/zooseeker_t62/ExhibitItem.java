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

    public double lat, lng;

    public String group_id;

    /**
     * @description: The constructor
     */
    public ExhibitItem(String id, String group_id, String kind, String name, String[] tags, double lat, double lng){
        this.id = id;
        this.group_id = group_id;
        this.kind = kind;
        this.name = name;
        this.tags = tags;
        this.lat = lat;
        this.lng = lng;
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
                ", tags=" + Arrays.toString(tags) + '\'' +
                ", lat=" + lat + '\'' +
                ", lng=" + lng + '\'' +
                ", group_id=" + group_id +
                '}';
    }

    public String getName() {
        return this.name;
    }
    public String getId() { return this.id; }
    public String getKind() {return this.kind; }
    public String[] getTags() { return this.tags; }
    public double getLat() { return this.lat; }
    public double getLng() { return this.lng; }
    public String getGroup_id() { return this.group_id; }

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

            /*List<ExhibitItem> exhibits = new ArrayList<>();

            for (ExhibitItem animal : animals) {
                if (animal.kind.equals("exhibit")) {
                    exhibits.add(animal);
                }
            }

             */

            return animals;
        } catch (IOException e) {
            e.printStackTrace();

            return Collections.emptyList();
        }
    }
}
