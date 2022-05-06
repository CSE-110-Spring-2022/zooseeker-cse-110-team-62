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

import java.util.Locale;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_directions);

        List<ExhibitItem> exhibits = getPlannerExhibits();

        start = exhibits.get(0).id;
        end = exhibits.get(exhibits.size() - 1).id;

        g = ZooData.loadZooGraphJSON("sample_zoo_graph.json", this);
        path = DijkstraShortestPath.findPathBetween(g, start, end);

        vInfo = ZooData.loadVertexInfoJSON("sample_node_info.json", this);
        eInfo = ZooData.loadEdgeInfoJSON("sample_edge_info.json", this);

        printPathString(g, path, vInfo, eInfo);

        pathIdx = 0;

        String pathString = genPathString(pathIdx, g, path, vInfo, eInfo );

        TextView textView = (TextView) findViewById(R.id.path_exhibit);
        textView.setText(pathString);
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
                    "  %d. Walk %.0f meters along %s from '%s' to '%s'.\n",
                    pathIdx,
                    g.getEdgeWeight(edge),
                    eInfo.get(edge.getId()).street,
                    vInfo.get(g.getEdgeSource(edge).toString()).name,
                    vInfo.get(g.getEdgeTarget(edge).toString()).name);
    }

    public void onPrevClick(View view) {
        this.pathIdx = this.pathIdx - 1;

        TextView textView = (TextView) findViewById(R.id.path_exhibit);
        String pathString = genPathString(pathIdx, g, path, vInfo, eInfo );
        textView.setText(pathString);
    }

    public void onNextClick(View view) {
        this.pathIdx = this.pathIdx + 1;
        if (path.getLength() == this.pathIdx){
            TextView finishText = (TextView) findViewById(R.id.finish_text);
            TextView textView = (TextView) findViewById(R.id.path_exhibit);

            Button prev_button = (Button) findViewById(R.id.previous);
            Button next_button = (Button) findViewById(R.id.next);
            prev_button.setVisibility(View.INVISIBLE);
            next_button.setVisibility(View.INVISIBLE);

            textView.setVisibility(View.INVISIBLE);
            finishText.setVisibility(View.VISIBLE);
        } else {
            TextView textView = (TextView) findViewById(R.id.path_exhibit);
            String pathString = genPathString(pathIdx, g, path, vInfo, eInfo );
            textView.setText(pathString);
        }
    }

    public void onHomeClick(View view){
//        Intent intent = new Intent(this, MainActivity.class);
//        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
