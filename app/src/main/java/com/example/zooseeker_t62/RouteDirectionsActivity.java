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

public class RouteDirectionsActivity extends AppCompatActivity {
    public ExhibitViewModel viewModel;

    private String start;
    private String end;
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

        g = ZooData.loadZooGraphJSON("sample_zoo_graph.json", this);
        currNode = "entrance_plaza";

        vInfo = ZooData.loadVertexInfoJSON("sample_node_info.json", this);
        eInfo = ZooData.loadEdgeInfoJSON("sample_edge_info.json", this);

        pathIdx = 0;
        pathStrings = new ArrayList<>();

        while (!exhibits.isEmpty()) {
            String nearestNeighbor = findNearestNeighbor(g, currNode, exhibits);

            if (nearestNeighbor.equals("")) {
                break;
            }
            path = DijkstraShortestPath.findPathBetween(g, currNode, nearestNeighbor);

            String from = getNameFromID(currNode, exhibits);
            // case where "from" ID is not an exhibit, namely entrance_plaza
            if (from.equals("")) {
                from = "Entrance Plaza";
            }
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
    }

    public String getNameFromID(String id, List<ExhibitItem> exhibits) {
        for (ExhibitItem item : exhibits) {
            if(item.id.equals(id)) {
                return item.name;
            }
        }
        return "";
    }

    public String findNearestNeighbor(Graph<String, IdentifiedWeightedEdge> g, String start,
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

    List<ExhibitItem> getPlannerExhibits() {
        viewModel = new ViewModelProvider(this)
                .get(ExhibitViewModel.class);

        ExhibitAdapter adapter = new ExhibitAdapter();
        adapter.setHasStableIds(true);

        List<ExhibitItem> exhibits = viewModel.getList();
        return exhibits;
    }

    void printPathString( Graph<String, IdentifiedWeightedEdge> g,
                          GraphPath<String, IdentifiedWeightedEdge> path,
                          Map<String, ZooData.VertexInfo> vInfo,
                          Map<String, ZooData.EdgeInfo> eInfo) {
        System.out.printf("The shortest path from '%s' to '%s' is:\n", start, end);

        int i = 1;
        for (IdentifiedWeightedEdge e : path.getEdgeList()) {
            System.out.printf("  %d. Walk %.0f meters along %s from '%s' to '%s'.\n",
                    i,
                    g.getEdgeWeight(e),
                    eInfo.get(e.getId()).street,
                    vInfo.get(g.getEdgeSource(e).toString()).name,
                    vInfo.get(g.getEdgeTarget(e).toString()).name);
            i++;
        }
    }

    String genPathString(int pathIdx, Graph<String, IdentifiedWeightedEdge> g,
                           GraphPath<String, IdentifiedWeightedEdge> path,
                           Map<String, ZooData.VertexInfo> vInfo,
                           Map<String, ZooData.EdgeInfo> eInfo) {
        Log.d("RouteActivity.java", "The shortest path from " + start + " to " + end + " is:\n");

            IdentifiedWeightedEdge edge = path.getEdgeList().get(pathIdx);
            return String.format(Locale,
                    "Walk %.0f meters along %s from '%s' to '%s'.\n",
                    g.getEdgeWeight(edge),
                    eInfo.get(edge.getId()).street,
                    vInfo.get(g.getEdgeSource(edge).toString()).name,
                    vInfo.get(g.getEdgeTarget(edge).toString()).name);
    }

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
