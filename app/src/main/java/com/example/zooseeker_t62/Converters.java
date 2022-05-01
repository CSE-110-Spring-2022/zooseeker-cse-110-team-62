package com.example.zooseeker_t62;

import androidx.room.TypeConverter;

import java.util.ArrayList;

public class Converters {
    @TypeConverter
    public String fromArray(String[] strings) {
        String string = "";
        for(String s : strings) string += (s + ",");

        return string;
    }

    @TypeConverter
    public String[] toArray(String concatenatedStrings) {
        ArrayList<String> myStrings = new ArrayList<>();

        return concatenatedStrings.split(",");
    }
}
