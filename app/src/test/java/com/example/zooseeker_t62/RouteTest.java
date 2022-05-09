package com.example.zooseeker_t62;


import android.content.Context;
import android.util.Log;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.jgrapht.Graph;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class RouteTest {

    /**
     * @Description: Mocks graphs and nodes to test findNearestNeighbor()
     *
     */
    @Test
    public void testFindNearestNeighbor() {
        Context context = InstrumentationRegistry.getInstrumentation().getContext();
        Graph<String, IdentifiedWeightedEdge> g = ZooData.loadZooGraphJSON("mock_graph.json", context);

        List<ExhibitItem> exhibits = ExhibitItem.loadJSON(context, "mock_nodes.json");

        for (ExhibitItem exhibit : exhibits) {
            System.out.println(exhibit.toString());
        }

        String nearestNeighbor = RouteDirectionsActivity.findNearestNeighbor(g, "one",
                exhibits);

        org.junit.Assert.assertEquals("two", nearestNeighbor);
    }

    /**
     * @Description: Mocks nodes to test getNameFromID()
     */
    @Test
    public void testGetNameFromID() {
        Context context = InstrumentationRegistry.getInstrumentation().getContext();
        List<ExhibitItem> exhibits = ExhibitItem.loadJSON(context, "mock_nodes.json");

        String name = RouteDirectionsActivity.getNameFromID("two", exhibits);
        org.junit.Assert.assertEquals("2", name);
    }
}