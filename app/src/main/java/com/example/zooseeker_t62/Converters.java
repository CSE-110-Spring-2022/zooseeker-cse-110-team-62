package com.example.zooseeker_t62;

import androidx.room.TypeConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

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

    public static String idToString(List<ExhibitItem> items) {
        String ans = "";

        for (ExhibitItem e : items) {
            ans += e.getId() + ",";
        }
        ans = ans.substring(0, ans.length() - 1);

        return ans;
    }

    public static String idToString(Stack<ExhibitItem> items) {
        String ans = "";

        int length = items.size();
        for (int i = 0; i < length; i++) {
            ans += items.pop().getId() + ",";
        }
        ans = ans.substring(0, ans.length() - 1);

        return ans;
    }

    public static String idToString(Set<String> items) {
        String ans = "";

        for (String s : items) {
            ans += s + ",";
        }
        ans = ans.substring(0, ans.length() - 1);

        return ans;
    }

}
