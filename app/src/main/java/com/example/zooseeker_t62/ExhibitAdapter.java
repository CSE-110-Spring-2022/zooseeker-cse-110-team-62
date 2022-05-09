package com.example.zooseeker_t62;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ExhibitAdapter extends RecyclerView.Adapter<ExhibitAdapter.ViewHolder> {
    private List<ExhibitItem> exhibitItems = Collections.emptyList();
    private Consumer<ExhibitItem> onDeleteButtonClicked;
    private JSONArray edges;
    private TextView exhibitCount;

    public void setExhibitItems(List<ExhibitItem> newExhibitItems) {
        this.exhibitItems.clear();
        this.exhibitItems = newExhibitItems;
        Log.d("ExhibitAdapter.java, exhibitItems: ", exhibitItems.toString());

        exhibitCount.setText("Exhibits: " + exhibitItems.size());

        Log.d("ExhibitAdapter.java", "" + exhibitItems.size());
        notifyDataSetChanged();
    }

    public void setExhibitCount(TextView textView) {
        this.exhibitCount = textView;
    }



    public void setEdges(JSONArray edges) {
        this.edges = edges;
    }

    public void setOnDeleteButtonClickedHandler(Consumer<ExhibitItem> onDeleteButtonClicked) {
        this.onDeleteButtonClicked = onDeleteButtonClicked;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.exhibit_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            holder.setExhibitItem(exhibitItems.get(position));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        Log.d("getItemCount()", "" + exhibitItems.size());
        return exhibitItems.size();
    }

    public long getItemId(int position) {
        return exhibitItems.get(position).long_id;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private ExhibitItem exhibitItem;
        private final TextView deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textView = itemView.findViewById(R.id.exhibit_item_text);
            this.deleteButton = itemView.findViewById(R.id.delete_btn);


            this.deleteButton.setOnClickListener(view -> {
                if (onDeleteButtonClicked == null) return;
                onDeleteButtonClicked.accept(exhibitItem);
            });

            Log.d("ExhibitAdapter.java", "ViewHolder()");
        }

        public ExhibitItem getExhibitItem() {return exhibitItem;}

        public void setExhibitItem(ExhibitItem exhibitItem) throws JSONException {
            this.exhibitItem = exhibitItem;

            String distance = findExhibitDist(exhibitItem.id);
            if (distance == null) {
                this.textView.setText(String.format("%s" , exhibitItem.name));
            } else {
                this.textView.setText(String.format("%s, %sm" , exhibitItem.name, distance));
            }
        }
        public String findExhibitDist(String id) throws JSONException {
            for (int i = 0; i < edges.length(); i++) {
                String currSource =  edges.getJSONObject(i).getString("source");
                String currTarget = edges.getJSONObject(i).getString("target");
                String currWeight = edges.getJSONObject(i).getString("weight");

                if (currSource.equals("entrance_plaza") || currTarget.equals("entrance_plaza")) {
                    if (id.equals(currTarget) || id.equals(currSource)) {
                        return currWeight;
                    }
                }
            }
          return null;
        }
    }
}
