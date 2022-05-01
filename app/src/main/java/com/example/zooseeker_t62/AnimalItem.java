package com.example.zooseeker_t62;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @description: Class that holds structure for node data in JSON
 */
public class AnimalItem {
    public String id, kind, name;
    public String[] tags;
    /**
     * @description: Animal Item Constructor
     */
    public AnimalItem(String id, String kind, String name, String[] tags){
        this.id = id;
        this.kind = kind;
        this.name = name;
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "{" +
                "id='" + id + '\'' +
                ", kind='" + kind + '\'' +
                ", name='" + name + '\'' +
                ", tags=" + Arrays.toString(tags) +
                '}';
    }

    /**
     * @description: loads JSON from path and returns object
     */
    public static List<AnimalItem> loadJSON(Context context, String path){
        try {
            InputStream input = context.getAssets().open(path);
            Reader reader = new InputStreamReader(input);

            Gson gson = new Gson();
            Type type = new TypeToken<List<AnimalItem>>(){}.getType();

            List<AnimalItem> animals = gson.fromJson(reader, type);
            return animals;
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
