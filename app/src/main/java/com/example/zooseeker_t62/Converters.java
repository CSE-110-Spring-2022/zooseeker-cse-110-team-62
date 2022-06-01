package com.example.zooseeker_t62;

import androidx.room.TypeConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: Class with methods to convert a String[] array to
 * a single String and vice-versa.
 */
public class Converters {

    /**
     * @description: Given an array of strings, gives a single
     * comma-separated string containing all elements from the array
     */
    @TypeConverter
    public static String fromArray(String[] strings) {
        String string = "";

        for(String s : strings) string += (s + ",");

        return string;
    }

    /**
     * @description: Given a comma-separated string of strings, generates
     * a String[] array storing the strings separately.
     */
    @TypeConverter
    public static String[] toArray(String concatenatedStrings) {
        ArrayList<String> myStrings = new ArrayList<>();

        return concatenatedStrings.split(",");
    }

    public static String fromList(String[] strings) {
        String string = "";

        for(String s : strings) string += (s + ",");

        return string;
    }

}
