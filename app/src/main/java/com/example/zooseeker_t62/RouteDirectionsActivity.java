package com.example.zooseeker_t62;

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
    private String currNode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_directions);

        List<ExhibitItem> exhibits = getPlannerExhibits();

        loadGraphData();
        buildOptimalPath(exhibits);
    }
    /**
     * @description: Loads in graph data from ZooData helper functions
     */
    public boolean loadGraphData() {
        try {
            g = ZooData.loadZooGraphJSON("zoo_graph.json", this);
            vInfo = ZooData.loadVertexInfoJSON("zoo_node_info.json", this);
            eInfo = ZooData.loadEdgeInfoJSON("zoo_edge_info.json", this);
        }
        catch (Exception e) {
            return false;
        }

        return true;
    }
    /**
     * @description: Main loop that calculates optimal path using algo referenced in class header
     */
    public boolean buildOptimalPath(List<ExhibitItem> exhibits) {
        if (exhibits == null || exhibits.size() <= 0) {
            return false;
        }
        pathIdx = 0;
        currNode = "entrance_exit_gate";

        pathStrings = new ArrayList<>();

        while (!exhibits.isEmpty()) {
            String nearestNeighbor = findNearestNeighbor(g, currNode, exhibits);
            if (nearestNeighbor.equals("")) break;

            path = DijkstraShortestPath.findPathBetween(g, currNode, nearestNeighbor);

            String from = getNameFromID(currNode, exhibits);
            // case where "from" ID is not an exhibit, namely entrance_plaza
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
                pathStrings.add(pathString);
            }
            // Remove from array once visited, no need to visit again
            for (int i = 0; i < exhibits.size(); i++) {
                if (currNode.equals(exhibits.get(i).id)) {
                    exhibits.remove(i);
                }
            }
            currNode = nearestNeighbor;
        }


        /* Not SRP, move this elsewhere */
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
            Log.d("RouteDirectionsActivity.java", start + ", " + exhibits.get(i).id);
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

        ExhibitAdapter adapter = new ExhibitAdapter();
        adapter.setHasStableIds(true);

        List<ExhibitItem> exhibits = viewModel.getList();
        return exhibits;
    }

    /**
     * @description: Upon Previous btn clicked, if pathIdx is 0 we go back to Planner, else
     * we simply decrement pathIdx and thus the previous path string will display
     */
    public void onPrevClick(View view) {
        if (pathIdx == 0) {
            Intent intent = new Intent(this, ExhibitActivity.class);
            startActivity(intent);
        } else {
            this.pathIdx = this.pathIdx - 1;
            TextView textView = (TextView) findViewById(R.id.path_exhibit);
            String pathString = pathStrings.get(pathIdx);
            textView.setText(pathString);
        }
    }

    /**
     * @description: Upon Next btn clicked, if pathIdx is at end of path string we go to end screen,
     * else we simply increment pathIdx and thus the next path string will display
     */
    public void onNextClick(View view) {
        this.pathIdx = this.pathIdx + 1;
        if (pathStrings.size() == this.pathIdx){
            Intent intent = new Intent(this, ExitActivity.class);
            startActivity(intent);
        } else {
            TextView textView = (TextView) findViewById(R.id.path_exhibit);
            String pathString = pathStrings.get(pathIdx);
            textView.setText(pathString);
        }
    }

    public void onPlanClick(View view) {
        Intent intent = new Intent(this, ExhibitPlanner.class);
        startActivity(intent);
    }

    /**
     * @description: Proper lifecycle cleanup
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
