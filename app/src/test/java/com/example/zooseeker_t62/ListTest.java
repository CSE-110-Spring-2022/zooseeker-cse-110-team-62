package com.example.zooseeker_t62;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.widget.TextView;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class ListTest {

    @Test
    public void addItem() {
        ArrayList<ExhibitItem> testArray = new ArrayList<ExhibitItem>();
        Context context = InstrumentationRegistry.getInstrumentation().getContext();

        List<ExhibitItem> exhibits = ExhibitItem.loadJSON(context, "mock_nodes.json");
        testArray.add(exhibits.get(0));
        testArray.add(exhibits.get(1));

        assertEquals(testArray.size(), 2);

        ExhibitPlannerAdapter tester = new ExhibitPlannerAdapter();
        tester.setExhibitCount(new TextView(context));
        tester.setExhibitItems(testArray);

        assertEquals(tester.getItemCount(), 2);
    }

}
