package com.example.zooseeker_t62;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
//import androidx.test.core.app.ApplicationProvider;
//import androidx.test.platform.app.InstrumentationRegistry;


import java.util.ArrayList;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.List;
import java.util.Stack;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

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

    private boolean isAtEntrance;
    private String currNode;
    private String nextNode;
    private List<String> currPath;
    private List<String> currInvertedPath;
    private List<ExhibitItem> exhibits;
    private List<ExhibitItem> unvisited;
    private Stack<ExhibitItem> visited;
    private LocationModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_directions);

        model = new ViewModelProvider(this).get(LocationModel.class);
        //var locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        //var provider = LocationManager.GPS_PROVIDER;
        //model.addLocationProviderSource(locationManager, provider);
        exhibits = getPlannerExhibits();

        //Log.d("RouteDirectionsActivity.java onCreate", exhibits.toString());

        loadGraphData();

        List<ExhibitItem> allExhibits = ExhibitItem.loadJSON(this, "sample_ms2_exhibit_info.json");

        unvisited = new ArrayList<>();

        for (int i = 0; i < allExhibits.size(); i++) {
            if (allExhibits.get(i).kind.equals("gate")) {
                unvisited.add(allExhibits.get(i));
                exhibits.add(allExhibits.get(i));
            }
        }

        for (ExhibitItem item : exhibits) {
            unvisited.add(item);
        }

        currNode = findEntrance(allExhibits);
        isAtEntrance = true;

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

        for (int i = 0; i < exhibits.size(); i++) {
            if (currNode.equals(exhibits.get(i).id)) {
                visited.push(exhibits.get(i));
            }
        }


        nextNode = findNearestNeighbor(g, currNode, unvisited);

        Log.d("nextNode", nextNode);

        if (nextNode.equals("")) return false;

        if(SettingsPage.getRouteType()){
            currPath = findCurrPath(currNode, nextNode, exhibits);
        } else {
            currPath = findCurrPathBrief(currNode, nextNode, exhibits);
        }

        Log.d("calcNextStep()", "from " + currNode + " to " + nextNode + ": calcNextstep()");


        // Remove from array once visited, no need to visit again
        for (int i = 0; i < unvisited.size(); i++) {
            if (currNode.equals(unvisited.get(i).id)) {
                unvisited.remove(i);
            }
        }
        currNode = nextNode;
//        if (!isAtEntrance)
//        else {
//            isAtEntrance = false;
//        }
        Log.d("calcNextStepStack()", visited.toString());

        return true;
    }

    public List<String> findCurrPathBrief(String currNode, String nextNode, List<ExhibitItem> exhibits) {
        List<String> currPath = new ArrayList<>();
        path = DijkstraShortestPath.findPathBetween(g, currNode, nextNode);
        String from = getNameFromID(currNode, exhibits);
        if (from.equals("")) from = "Entrance and Exit Gate";

        /**
         *  Builds path BETWEEN two nodes, namely the start and end node where end is the closest
         *  unvisited node from the start
         */
        int sum = 0;
        String sourceName = null;
        String targetName = null;
        for (IdentifiedWeightedEdge edge : path.getEdgeList()) {
            sourceName = vInfo.get(g.getEdgeSource(edge).toString()).name;
            targetName = vInfo.get(g.getEdgeTarget(edge).toString()).name;
            sum += g.getEdgeWeight(edge);
        }
        String pathString = String.format("Walk %s meters from %s to %s.", sum, sourceName, targetName);
        Log.d("path", pathString);
        currPath.add(pathString);
        return currPath;
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
        if (prevNode.equals(currNode)) {
            if (visited.size() == 1) {
                return false;
            }
            visited.pop();
            prevNode = visited.peek().id;
        }
        if(SettingsPage.getRouteType()){
            currInvertedPath = findCurrPath(currNode, prevNode, exhibits);
        } else {
            currInvertedPath = findCurrPathBrief(currNode, prevNode, exhibits);
        }

        Log.d("calcPrevStep()", "from " + currNode + " to " + prevNode);
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
        Graph<String, IdentifiedWeightedEdge> g = ZooData.loadZooGraphJSON("sample_ms2_zoo_graph.json", context);
        Map<String, ZooData.VertexInfo> vInfo = ZooData.loadVertexInfoJSON("sample_ms2_exhibit_info.json", context);
        Map<String, ZooData.EdgeInfo> eInfo = ZooData.loadEdgeInfoJSON("sample_ms2_trail_info.json", context);

        Log.d("RouteDirectionsActivity", "entrance: " + entrance + " id: " + id);

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
            g = ZooData.loadZooGraphJSON("sample_ms2_zoo_graph.json", this);
            vInfo = ZooData.loadVertexInfoJSON("sample_ms2_exhibit_info.json", this);
            eInfo = ZooData.loadEdgeInfoJSON("sample_ms2_trail_info.json", this);
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
//            Log.d("RouteDirectionsActivity.java", start + ", " + exhibits.get(i).id);
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
        List<ExhibitItem> allExhibits = ExhibitItem.loadJSON(this, "sample_ms2_exhibit_info.json");
        if (!calcPrevStep()) {
//            Log.d("test", "returns false");
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
     * @description: Upon Next btn clicked, if pathIdx is at end of path string we go to end screen,
     * else we simply increment pathIdx and thus the next path string will display
     */
    public void onSkipClick(View view) {
        if (!calcSkipStep()) {
            Intent intent = new Intent(this, ExitActivity.class);
            startActivity(intent);
        }

        TextView textView = (TextView) findViewById(R.id.path_exhibit);
        String currPathString = "";
        for (int i = 0; i < currPath.size(); i++) {
            currPathString += currPath.get(i);
        }
        textView.setText(currPathString);
    }

    /**
     * @description: Skips next exhibit node
     */
    public boolean calcSkipStep() {
        if (unvisited.size() == 1) return false;

        for (int i = 0; i < unvisited.size(); i++) {
            if (unvisited.get(i).id.equals(nextNode)) {
                unvisited.remove(i);
            }
        }
        Log.d("nextNode", nextNode);

        currNode = visited.peek().id;
        nextNode = findNearestNeighbor(g, currNode, unvisited);
        currPath = findCurrPath(currNode, nextNode, exhibits);

        Log.d("calcSkipStep()", "from " + currNode + " to " + nextNode);
        Log.d("unvistedInSkipStep", unvisited.toString());

        return true;
    }

    public void onPlanClick(View view) {
        Intent intent = new Intent(this, ExhibitPlanner.class);
        startActivity(intent);
    }

    public void onCoordUpdateClick(View view) {
        //Log.d("onCoordUpdateClick()", view.toString());
        TextView coordsText = findViewById(R.id.coords_edit_txt);

        Log.d("onCoordUpdateClick()", coordsText.getText().toString());
        String[] coords = coordsText.getText().toString().split(",");
        double coordLat = Double.parseDouble(coords[0]);
        double coordLong = Double.parseDouble(coords[1]);

        Log.d("onCoordUpdateClick()", "latitude: " + coordLat + ", longitude: " + coordLong);

        Coord updatedCoords = new Coord(coordLat, coordLong);
        mockLocation(updatedCoords);

        model.getLastKnownCoords().observe(this, (coord) -> Log.i("onCoordUpdateClick", String.format("Observing location model update to %s", coord)));
    }

    /**
     * @description: Proper lifecycle cleanup
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @VisibleForTesting
    public void mockLocation(Coord coords) {
        model.mockLocation(coords);
    }

    @VisibleForTesting
    public Future<?> mockRoute(List<Coord> route, long delay, TimeUnit unit) {
        return model.mockRoute(route, delay, unit);
    }
}
