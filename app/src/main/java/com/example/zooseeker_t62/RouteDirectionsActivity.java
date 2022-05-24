package com.example.zooseeker_t62;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
//import androidx.test.core.app.ApplicationProvider;
//import androidx.test.platform.app.InstrumentationRegistry;


import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.Stack;

/**
 * @description: Uses our algorithm to calculate optimal route of exhibit paths
 * 1. Finds exhibit shortest distance away from start and runs dijkstras(entrance, foundExhibit)
 * 2. Once at that exhibit, re-evaluates and finds closestNearestNeighbor to this exhibit
 * 3. Once found, runs dijkstras(currExhibit, closestNearestNeighbor)
 * 4. Repeat until all exhibits are visited.
 */
public class RouteDirectionsActivity extends AppCompatActivity {
    public ExhibitViewModel viewModel;

    private int pathIdx;
    private java.util.Locale Locale;

    private Graph<String, IdentifiedWeightedEdge> g;
    private GraphPath<String, IdentifiedWeightedEdge> path;
    private Map<String, ZooData.VertexInfo> vInfo;
    private Map<String, ZooData.EdgeInfo> eInfo;
    private List<String> pathStrings;

    private List<String> inversePathStrings;


    private String currNode;
    private String nextNode;
    private List<String> currPath;
    private List<String> currInvertedPath;
    private List<ExhibitItem> exhibits;
    private List<ExhibitItem> unvisited;
    private Stack<ExhibitItem> visited;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_directions);

        exhibits = getPlannerExhibits();

        //Log.d("RouteDirectionsActivity.java onCreate", exhibits.toString());

        loadGraphData();

        List<ExhibitItem> allExhibits = ExhibitItem.loadJSON(this, "sample_ms1_demo_node_info.json");

        unvisited = new ArrayList<>();
        for (ExhibitItem item : exhibits) {
            unvisited.add(item);
        }

        currNode = findEntrance(allExhibits);
        visited = new Stack<>();

        calcNextStep();

        TextView textView = (TextView) findViewById(R.id.path_exhibit);
        textView.setText(currPath.toString());
    }

    /**
     * @description: When user clicks next these things happen:
     */
    public boolean calcNextStep() {
        if (unvisited == null || unvisited.size() <= 0) {
            return false;
        }

        if (nextNode != null) {
            for (int i = 0; i < exhibits.size(); i++) {
                if (currNode.equals(exhibits.get(i).id)) {
                    visited.push(exhibits.get(i));
                }
            }
        }

        nextNode = findNearestNeighbor(g, currNode, unvisited);

        if (nextNode.equals("")) return false;


        currPath = findCurrPath(currNode, nextNode, exhibits);

        //Log.d("calcNextStep()", "from " + currNode + " to " + nextNode);
        currNode = nextNode;
        // Remove from array once visited, no need to visit again
        for (int i = 0; i < unvisited.size(); i++) {
            if (currNode.equals(unvisited.get(i).id)) {
                unvisited.remove(i);
            }
        }

        //Log.d("calcNextStep()", visited.toString());

        return true;
    }

    public boolean calcPrevStep() {
        if (visited == null || visited.size() <= 0) {
            return false;
        }


        for (int i = 0; i < exhibits.size(); i++) {
            if (currNode.equals(exhibits.get(i).id)) {
                unvisited.add(exhibits.get(i));
            }
        }


        String prevNode = visited.peek().id;
        currInvertedPath = findCurrPath(currNode, prevNode, exhibits);

        //Log.d("calcPrevStep()", "from " + currNode + " to " + prevNode);
        visited.pop();

        nextNode = currNode;
        currNode = prevNode;
        // Remove from array once visited, no need to visit again
        return true;
    }

    public List<String> findCurrPath(String currNode, String nextNode, List<ExhibitItem> exhibits) {

        List<String> currPath = new ArrayList<>();
        path = DijkstraShortestPath.findPathBetween(g, currNode, nextNode);
        String from = getNameFromID(currNode, exhibits);
        if (from.equals("")) from = "Entrance and Exit Gate";

        /**
         *  Builds path BETWEEN two nodes, namely the start and end node where end is the closest
         *  unvisited node from the start
         */
        for (IdentifiedWeightedEdge edge : path.getEdgeList()) {
            String sourceName = vInfo.get(g.getEdgeSource(edge).toString()).name;
            String targetName = vInfo.get(g.getEdgeTarget(edge).toString()).name;

            String to = (!sourceName.equals(from)) ? sourceName : targetName;
            String pathString = String.format(Locale,
                    "Walk %.0f meters along %s from '%s' to '%s'.\n",
                    g.getEdgeWeight(edge),
                    eInfo.get(edge.getId()).street,
                    from,
                    to);
            from = to;
            currPath.add(pathString);
        }
        return currPath;
    }

    /**
     * @description: find exhibit distance from entrance
     */
    public static String findExhibitDist(Context context, String entrance, String id) {
        Graph<String, IdentifiedWeightedEdge> g = ZooData.loadZooGraphJSON("sample_ms1_demo_zoo_graph.json", context);
        Map<String, ZooData.VertexInfo> vInfo = ZooData.loadVertexInfoJSON("sample_ms1_demo_node_info.json", context);
        Map<String, ZooData.EdgeInfo> eInfo = ZooData.loadEdgeInfoJSON("sample_ms1_demo_edge_info.json", context);

        GraphPath<String, IdentifiedWeightedEdge> path = DijkstraShortestPath.findPathBetween(g, entrance, id);

        double pathDist = 0;
        for (IdentifiedWeightedEdge edge : path.getEdgeList()) {
            pathDist += g.getEdgeWeight(edge);
        }
        return "" + pathDist;
    }

    /**
     * @description: Loads in graph data from ZooData helper functions
     */
    public boolean loadGraphData() {
        try {
            g = ZooData.loadZooGraphJSON("sample_ms1_demo_zoo_graph.json", this);
            vInfo = ZooData.loadVertexInfoJSON("sample_ms1_demo_node_info.json", this);
            eInfo = ZooData.loadEdgeInfoJSON("sample_ms1_demo_edge_info.json", this);
        }
        catch (Exception e) {
            return false;
        }

        return true;
    }
    /**
     * @description: Finds entrance of our data JSON
     */
    public static String findEntrance( List<ExhibitItem> exhibits) {
        for(int i = 0 ; i <exhibits.size() ; i++) {
            ExhibitItem currExhibit = exhibits.get(i);
            if (currExhibit.getKind().equals("gate")) {
                return currExhibit.getId();
            }
        }
        return null;
    }

    /**
     * @description: Main loop that calculates optimal path using algo referenced in class header
     */
    public boolean buildOptimalPath(List<ExhibitItem> exhibits) {
        if (exhibits == null || exhibits.size() <= 0) {
            return false;
        }
        pathIdx = 0;

        //Set currNode to be ID of exhibit that is kind "gate"
        List<ExhibitItem> allExhibits = ExhibitItem.loadJSON(this, "sample_ms1_demo_node_info.json");
        currNode = findEntrance(allExhibits);


        pathStrings = new ArrayList<>();
        inversePathStrings = new ArrayList<>();

        while (!exhibits.isEmpty()) {
            String nearestNeighbor = findNearestNeighbor(g, currNode, exhibits);
            if (nearestNeighbor.equals("")) break;

            path = DijkstraShortestPath.findPathBetween(g, currNode, nearestNeighbor);

            String from = getNameFromID(currNode, exhibits);
            // case where "from" ID is not an exhibit, namely entrance_exit_gate
            if (from.equals("")) from = "Entrance and Exit Gate";
            /**
             *  Builds path BETWEEN two nodes, namely the start and end node where end is the closest
             *  unvisited node from the start
             */
            for (IdentifiedWeightedEdge edge : path.getEdgeList()) {
                String sourceName = vInfo.get(g.getEdgeSource(edge).toString()).name;
                String targetName = vInfo.get(g.getEdgeTarget(edge).toString()).name;

                String to = (!sourceName.equals(from)) ? sourceName : targetName;
                String pathString = String.format(Locale,
                        "Walk %.0f meters along %s from '%s' to '%s'.\n You are at %s",
                        g.getEdgeWeight(edge),
                        eInfo.get(edge.getId()).street,
                        from,
                        to, from);

                String inverseString = String.format(Locale,
                        "Walk %.0f meters along %s from '%s' to '%s'.\n You are at %s",
                        g.getEdgeWeight(edge),
                        eInfo.get(edge.getId()).street,
                        to,
                        from, to);

                from = to;
                pathStrings.add(pathString);
                inversePathStrings.add(inverseString);
            }
            // Remove from array once visited, no need to visit again
            for (int i = 0; i < exhibits.size(); i++) {
                if (currNode.equals(exhibits.get(i).id)) {
                    exhibits.remove(i);
                }
            }
            currNode = nearestNeighbor;
        }
        String pathString = pathStrings.get(0);
        TextView textView = (TextView) findViewById(R.id.path_exhibit);
        textView.setText(pathString);

        return true;
    }

    /**
     * @description: Since we have ID's in exhibits but we need names, helper to convert
     */
    public static String getNameFromID(String id, List<ExhibitItem> exhibits) {
        for (ExhibitItem item : exhibits) {
            if(item.id.equals(id)) {
                return item.name;
            }
        }
        return "";
    }
    /**
     * @description: Algo to find nearest neighbor given a node in our graph
     */
    public static String findNearestNeighbor(Graph<String, IdentifiedWeightedEdge> g, String start,
                                           List<ExhibitItem> exhibits ) {
        String nearestNeighbor = "";
        double shortestTotalPathWeight = Double.MAX_VALUE;

        for (int i = 0; i < exhibits.size(); i++) {
            GraphPath<String, IdentifiedWeightedEdge> currPath = DijkstraShortestPath.findPathBetween(g, start, exhibits.get(i).id);
            if (currPath.getLength() > 0) {
                double totalCurrPathWeight = 0;
                for (IdentifiedWeightedEdge e : currPath.getEdgeList()) {
                    totalCurrPathWeight += g.getEdgeWeight(e);
                }
                if (totalCurrPathWeight < shortestTotalPathWeight) {
                    shortestTotalPathWeight = totalCurrPathWeight;
                    nearestNeighbor = exhibits.get(i).id;
                }
            }
        }
        return nearestNeighbor;
    }
    /**
     * @description: Returns our exhibits currently planned
     */
    List<ExhibitItem> getPlannerExhibits() {
        viewModel = new ViewModelProvider(this)
                .get(ExhibitViewModel.class);

        ExhibitAdapter adapter = new ExhibitAdapter(this);
        adapter.setHasStableIds(true);

        List<ExhibitItem> exhibits = viewModel.getList();
        return exhibits;
    }

    /**
     * @description: Upon Previous btn clicked, if pathIdx is 0 we go back to Planner, else
     * we simply decrement pathIdx and thus the previous path string will display
     */
    public void onPrevClick(View view) {
        List<ExhibitItem> allExhibits = ExhibitItem.loadJSON(this, "sample_ms1_demo_node_info.json");
        if (!calcPrevStep()) {
            Log.d("test", "returns false");
            Intent intent = new Intent(this, ExhibitActivity.class);
            startActivity(intent);
        } else {
            TextView textView = (TextView) findViewById(R.id.path_exhibit);
            String currInvertedPathString = "";
            for (int i = 0; i < currInvertedPath.size(); i++) {
                currInvertedPathString += currInvertedPath.get(i);
            }
            textView.setText(currInvertedPathString);
        }
    }

    /**
     * @description: Upon Next btn clicked, if pathIdx is at end of path string we go to end screen,
     * else we simply increment pathIdx and thus the next path string will display
     */
    public void onNextClick(View view) {

        if (!calcNextStep()){
            Intent intent = new Intent(this, ExitActivity.class);
            startActivity(intent);
        } else {
            TextView textView = (TextView) findViewById(R.id.path_exhibit);
            String currPathString = "";
            for (int i = 0; i < currPath.size(); i++) {
                currPathString += currPath.get(i);
            }
            textView.setText(currPathString);
        }
    }
    /**
     * @description: Proper lifecycle cleanup
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
