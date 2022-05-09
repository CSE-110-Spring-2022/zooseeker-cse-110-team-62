package com.example.zooseeker_t62;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import android.content.Context;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ExampleViewHolder> implements Filterable {
    private List<ExhibitItem> searchList;
    private List<ExhibitItem> searchListFull;
    private RecyclerViewClickListener listener;
    private Consumer<ExhibitItem> onExhibitClicked;
    private Context context;

    public SearchAdapter(List<ExhibitItem> searchList, RecyclerViewClickListener listener, Context context) {
        this.searchList = searchList;
        this.listener = listener;
        this.context = context;
        searchListFull = new ArrayList<>(searchList);
    }

    class ExampleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textView1;

        ExampleViewHolder(@NonNull View itemView) {
            super(itemView);
            textView1 = itemView.findViewById(R.id.text_view1);
        }

        @Override
        public void onClick(View view) {
            Log.d("test2", "in onClick");

//            Log.d("testing", "" + getAdapterPosition());
//            listener.onClick(view, getAdapterPosition());
        }
    }

    public void setOnExhibitClicked(Consumer<ExhibitItem> onExhibitClicked) {
        this.onExhibitClicked = onExhibitClicked;
    }

    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item,
                parent, false);
        return new ExampleViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ExampleViewHolder holder, int position) {
        ExhibitItem currentItem = searchList.get(position);
        holder.textView1.setText(currentItem.getName());
        String name = holder.textView1.getText().toString();

        holder.textView1.setOnClickListener(v -> {
            if (onExhibitClicked == null) return;

            ExhibitItem exhibitItem = null;
            for (int i = 0; i < searchList.size(); i++) {
                String currName = searchList.get(i).name;
                ExhibitItem currItem = searchList.get(i);
                if (currName.equals(name)) {
                    exhibitItem = currItem;
                }
            }
            onExhibitClicked.accept(exhibitItem);
            Utilities.showAlert((Activity) context, "Press OK to keep adding to the plan.");
        });
    }

    @Override
    public int getItemCount() {
        return searchList.size();
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ExhibitItem> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(searchListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (ExhibitItem item : searchListFull) {
                    if (item.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            searchList.clear();
            searchList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public interface RecyclerViewClickListener {
        void onClick(View v, int position);
    }

}
