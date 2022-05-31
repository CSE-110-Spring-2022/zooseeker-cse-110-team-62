package com.example.zooseeker_t62;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

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

    private static String YOU_ARE_HERE = "You are here!";

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
    private String prevNextNode;
    private String nextNode;
    private List<String> currPath;
    private List<String> currInvertedPath;
    private List<ExhibitItem> exhibits;
    private List<ExhibitItem> unvisited;
    private Stack<ExhibitItem> visited;
    private LocationModel model;
    private ExhibitItem closestExhibit;
    private List<ExhibitItem> allExhibits;
    private ExhibitItem nearestNodeByLocation;


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

        allExhibits = ExhibitItem.loadJSON(this, "sample_ms2_exhibit_info.json");

        ExhibitItem entrance = null;

        unvisited = new ArrayList<>();
        visited = new Stack<>();

        for (int i = 0; i < allExhibits.size(); i++) {
            if (allExhibits.get(i).kind.equals("gate")) {
                entrance = allExhibits.get(i);
                exhibits.add(allExhibits.get(i));
            }
        }

        for (ExhibitItem item : exhibits) {
            unvisited.add(item);
        }

        for (int i = 0; i < unvisited.size(); i++) {
            if (unvisited.get(i).kind.equals("gate")) {
                visited.push(unvisited.get(i));
                unvisited.remove(i);
                break;
            }
        }

        Log.d("unvisited", unvisited.toString());

        currNode = findEntrance(allExhibits);
        prevNextNode = currNode;
        mockLocation(new Coord(entrance.getLat(), entrance.getLng()));
        isAtEntrance = true;



        calcNextStep();

        TextView textView = (TextView) findViewById(R.id.path_exhibit);
        textView.setText(currPath.toString());

        updateUserLocation();
    }

    public ExhibitItem findExhibitById(String id) {
        for (ExhibitItem item : exhibits) {
            if (item.id.equals(id)) {
                return item;
            }
        }
        return null;
    }

    public void updateCurrent(Coord coord) {
        String oldCurrent = currNode;
        double minDistance = Double.MAX_VALUE;
        for (ExhibitItem exhibit : exhibits) {
            double distance = calcDistance(coord, exhibit);
            if (distance < minDistance) {
                minDistance = distance;
                currNode = exhibit.id;
            }
        }

        if (!oldCurrent.equals(currNode)) {
            currPath = findCurrPath(currNode, nextNode, exhibits);

            TextView textView = (TextView) findViewById(R.id.path_exhibit);
            String currPathString = "";
            for (int i = 0; i < currPath.size(); i++) {
                currPathString += currPath.get(i);
            }

            if (currPathString.equals("")) {
                currPathString = YOU_ARE_HERE;
                ExhibitItem currExhibit = findExhibitById(currNode);
                boolean inUnvisited = false;
                for (int i = 0; i < unvisited.size(); i++) {
                    if (currExhibit.id.equals(unvisited.get(i).id)) {
                        inUnvisited = true;
                        break;
                    }
                }

                if (inUnvisited) {
                    this.visited.push(currExhibit);
                    for (int i = 0; i < unvisited.size(); i++) {
                        if (currExhibit.id.equals(unvisited.get(i).id)) {
                            unvisited.remove(i);
                            break;
                        }
                    }
                }
                else {
                    unvisited.add(currExhibit);
                }

                Log.d("updateCurrent()", "visited: " + visited.toString());
                Log.d("updateCurrent()", "unvisited: " + unvisited.toString());

            }
            textView.setText(currPathString);
        }

        Log.d("updateCurrent()", "currNode: " + currNode);
    }

    public void updateUserLocation() {
        model.getLastKnownCoords().observe(this, (coord) -> {
            Log.i("onCoordUpdateClick", String.format("Observing location model update to %s", coord));

            updateCurrent(coord);

            Log.d("updateUserLocation", "unvisited: " + unvisited.toString());
            Log.d("updateUserLocation", "visited: " + visited.toString());
            this.closestExhibit = findNearestUnvisitedPlannedExhibit(coord);

            Log.d("updateUserLocation", "this.closestExhibit: " + this.closestExhibit.toString());

            offerReplan(coord);
        });
    }

    public ExhibitItem findNearestUnvisitedPlannedExhibit(Coord coord) {
        ExhibitItem minNode = null;
        double minDistance = Double.MAX_VALUE;
        for (ExhibitItem unvisitedExhibit : unvisited) {
            //Log.d("findClosestNodeByLocation()", "unvisitedExhibit: " + unvisitedExhibit.toString());
            if (minNode == null) {
                minNode = unvisitedExhibit;
                minDistance = calcDistance(coord, unvisitedExhibit);
                continue;
            }

            double distance = calcDistance(coord, unvisitedExhibit);

            //Log.d("distance", "" + distance);
            if (distance < minDistance) {
                minDistance = distance;
                minNode = unvisitedExhibit;
            }
        }

        if (minNode == null) {
            Intent intent = new Intent(this, ExitActivity.class);
            startActivity(intent);
            return findExhibitById(findEntrance(allExhibits));
        }

        //Log.d("minNode", minNode.toString());
        //Log.d("currNode", currNode);

        return minNode;
    }

    public ExhibitItem findNearestPlannedExhibit(Coord coord) {
        ExhibitItem minNode = null;
        double minDistance = Double.MAX_VALUE;
        for (ExhibitItem exhibit : exhibits) {
            //Log.d("findClosestNodeByLocation()", "unvisitedExhibit: " + unvisitedExhibit.toString());
            if (minNode == null) {
                minNode = exhibit;
                minDistance = calcDistance(coord, exhibit);
                continue;
            }

            double distance = calcDistance(coord, exhibit);

            //Log.d("distance", "" + distance);
            if (distance < minDistance) {
                minDistance = distance;
                minNode = exhibit;
            }
        }

        Log.d("minNode", minNode.toString());
        Log.d("currNode", currNode);

        return minNode;
    }

    public void offerReplan(Coord coord) {
        if (this.closestExhibit == null || this.visited.size() == 0) return;

        Log.d("offerReplan()", "currNode: " + currNode);
        Log.d("offerReplan()", "prevNextNode: " + prevNextNode);

        ExhibitItem nearestExhibit = findNearestPlannedExhibit(coord);
        ExhibitItem nearestUnvisitedExhibit = findNearestUnvisitedPlannedExhibit(coord);

        Log.d("offerReplan()", "nearestExhibit: " + nearestExhibit.toString());
        Log.d("offerReplan()", "this.closestExhibit: " + this.closestExhibit.toString());
        if (this.closestExhibit.id.equals(nearestExhibit.id) && !this.closestExhibit.id.equals(nextNode)) {
            nearestNodeByLocation = findNearestNodeByLocation(coord);
            inflatePopup(coord);
            //currNode = this.closestExhibit.id;
        }
    }

    public ExhibitItem findNearestNodeByLocation(Coord coord) {
        ExhibitItem minNode = null;
        double minDistance = Double.MAX_VALUE;
        for (ExhibitItem node : allExhibits) {
//            Log.d("findNearestNodeByLocation()", "node: " + node.toString());
            if (minNode == null) {
                minNode = node;
                minDistance = calcDistance(coord, node);
                continue;
            }

            double distance = calcDistance(coord, node);

            //Log.d("distance", "" + distance);
            if (distance < minDistance) {
                minDistance = distance;
                minNode = node;
            }
        }

        Log.d("minNode", minNode.toString());

        return minNode;
    }

    public void inflatePopup(Coord coord) {
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.replan_popup, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(findViewById(R.id.route_dir_layout), Gravity.CENTER, 0, 0);

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                String nearestNode = findNearestNodeByLocation(coord).id;
                currPath = findCurrPath(nearestNode, nextNode, exhibits);

                TextView textView = (TextView) findViewById(R.id.path_exhibit);
                String currPathString = "";
                for (int i = 0; i < currPath.size(); i++) {
                    currPathString += currPath.get(i);
                }
                if (currPathString.equals("")) {
                    ExhibitItem exhibit = findExhibitById(nearestNode);
                    for (int i = 0; i < unvisited.size(); i++) {
                        if (exhibit.id.equals(unvisited.get(i).id)) {
                            unvisited.remove(i);
                            break;
                        }
                    }
                    visited.push(exhibit);

                    currPathString = YOU_ARE_HERE;
                }
                else {
                    currNode = nearestNode;
                }
                textView.setText(currPathString);
                popupWindow.dismiss();
                return true;
            }
        });

        Button accept = popupView.findViewById(R.id.accept_replan_btn);
        Button reject = popupView.findViewById(R.id.reject_replan_btn);

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextNode = findNearestUnvisitedPlannedExhibit(coord).id;

                String nearestNode = findNearestNodeByLocation(coord).id;
                currPath = findCurrPath(nearestNode, nextNode, exhibits);

                TextView textView = (TextView) findViewById(R.id.path_exhibit);
                String currPathString = "";
                for (int i = 0; i < currPath.size(); i++) {
                    currPathString += currPath.get(i);
                }
                if (currPathString.equals("")) {
                    ExhibitItem exhibit = findExhibitById(nearestNode);
                    for (int i = 0; i < unvisited.size(); i++) {
                        if (exhibit.id.equals(unvisited.get(i).id)) {
                            unvisited.remove(i);
                            break;
                        }
                    }
                    visited.push(exhibit);
                    currPathString = YOU_ARE_HERE;
                }
                else {
                    currNode = nearestNode;
                }
                textView.setText(currPathString);
                popupWindow.dismiss();
            }
        });

        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nearestNode = findNearestNodeByLocation(coord).id;
                currPath = findCurrPath(nearestNode, nextNode, exhibits);

                TextView textView = (TextView) findViewById(R.id.path_exhibit);
                String currPathString = "";
                for (int i = 0; i < currPath.size(); i++) {
                    currPathString += currPath.get(i);
                }
                if (currPathString.equals("")) {
                    ExhibitItem exhibit = findExhibitById(nearestNode);
                    for (int i = 0; i < unvisited.size(); i++) {
                        if (exhibit.id.equals(unvisited.get(i).id)) {
                            unvisited.remove(i);
                            break;
                        }
                    }
                    visited.push(exhibit);
                    currPathString = YOU_ARE_HERE;
                }
                else {
                    currNode = nearestNode;
                }
                textView.setText(currPathString);
                popupWindow.dismiss();
            }
        });
    }

    public double calcDistance(Coord coord, ExhibitItem exhibit) {
        return Math.sqrt(Math.pow(coord.lat - exhibit.getLat(), 2) + Math.pow(coord.lng - exhibit.getLng(), 2));
    }

    /**
     * @description: When user clicks next these things happen:
     */
    public boolean calcNextStep() {
        if (unvisited == null || unvisited.size() <= 0) {
            return false;
        }

        if (nextNode != null) {
            prevNextNode = nextNode;
        }
        String oldNextNode = nextNode;
        nextNode = findNearestNeighbor(g, currNode, unvisited);
        Log.d("nextNode", nextNode);
        if (nextNode.equals("")) return false;

        /*if (oldNextNode != null && oldNextNode.equals(nextNode)) {
            ExhibitItem oldNextExhibit = findExhibitById(oldNextNode);
            for (int i = 0; i < unvisited.size(); i++) {
                if (oldNextExhibit.id.equals(unvisited.get(i).id)) {
                    unvisited.remove(i);
                    break;
                }
            }
            nextNode = findNearestNeighbor(g, currNode, unvisited);
            unvisited.add(oldNextExhibit);
        }*/

        currPath = findCurrPath(currNode, nextNode, exhibits);

//        Log.d("calcNextStep()", "from " + currNode + " to " + nextNode + ": calcNextstep()");

        Log.d("calcNextStep()", "nextNode: " + nextNode);

        return true;
    }

    public boolean calcPrevStep() {
        if (visited == null || visited.size() <= 0) {
            return false;
        }

        /*if (visited.size() == 1) {
            if (visited.peek().kind.equals("gate")) {
                return false;
            }
        }*/

        ExhibitItem poppedExhibit = visited.pop();
        nextNode = poppedExhibit.id;
        //unvisited.add(poppedExhibit);
        currInvertedPath = findCurrPath(currNode, nextNode, exhibits);

        Log.d("calcPrevStep()", "from " + currNode + " to " + nextNode);
        Log.d("calcPrevStep()", "visited: " + visited.toString());
        Log.d("calcPrevStep()", "unvisited: " + unvisited.toString());
        return true;
    }

    public List<String> findCurrPath(String currNode, String nextNode, List<ExhibitItem> exhibits) {

        List<String> currPath = new ArrayList<>();
        path = DijkstraShortestPath.findPathBetween(g, currNode, nextNode);
        String from = getNameFromID(currNode, allExhibits);
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
            if (currInvertedPathString.equals("")) {
                currInvertedPathString = YOU_ARE_HERE;
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
            if (currPathString.equals("")) {
                currPathString = YOU_ARE_HERE;
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
        if (currPathString.equals("")) {
            currPathString = YOU_ARE_HERE;
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
