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

/**
 * @description: SearchAdapter that bridges our searchList --> recyclerView in SearchActivity
 */
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ExampleViewHolder> implements Filterable {
    private List<ExhibitItem> searchList;
    private List<ExhibitItem> searchListFull;
    private Consumer<ExhibitItem> onExhibitClicked;
    private Context context;

    /**
     * @description: Constructor passing in our exhibits in list and context for the alert modal
     */
    public SearchAdapter(List<ExhibitItem> searchList, Context context) {
        this.searchList = searchList;
        this.context = context;
        searchListFull = new ArrayList<>(searchList);
    }

    /**
     * @description: finds our exhibit_name in itemView and sets member var to that TextView
     */
    class ExampleViewHolder extends RecyclerView.ViewHolder  {
        TextView exhibit_text_view;

        ExampleViewHolder(@NonNull View itemView) {
            super(itemView);
            exhibit_text_view = itemView.findViewById(R.id.exhibit_text_view);
        }
    }
    /**
     * @description: Serves as onClick listener, when this is called in SearchActivity we set
     * member variable which has consumer items
     */
    public void setOnExhibitClicked(Consumer<ExhibitItem> onExhibitClicked) {
        this.onExhibitClicked = onExhibitClicked;
    }

    /**
     * @description: Method to actually build and create our viewHolder
     */
    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item,
                parent, false);
        return new ExampleViewHolder(v);
    }

    /**
     * @description: This is how recyclerView text gets set to the exhibit names in the JSON
     * Also onClick listener is what adds to DAO and shows alert on screen
     */
    @Override
    public void onBindViewHolder(@NonNull ExampleViewHolder holder, int position) {
        ExhibitItem currentItem = searchList.get(position);
        holder.exhibit_text_view.setText(currentItem.getName());
        String name = holder.exhibit_text_view.getText().toString();

        holder.exhibit_text_view.setOnClickListener(v -> {
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

    /**
     * @description: Get size of searchList array
     */
    @Override
    public int getItemCount() {
        return searchList.size();
    }

    /**
     * @description: Returns current filter object
     */
    @Override
    public Filter getFilter() {
        return exampleFilter;
    }
    /**
     * @description: Filtering method used in our searchView, if our search query is a substring
     * of one of the items in searchList we add to filtered list and return results.
     */
    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ExhibitItem> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(searchListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (ExhibitItem item : searchListFull) {
                    //for names
                    if (item.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    } else {
                        //for tags
                        for(int i = 0 ; i < item.getTags().length ; i++){
                            if(item.getTags()[i].toLowerCase().contains(filterPattern)){
                                filteredList.add(item);
                                break;
                            }
                        }
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }
        /**
         * @description: Now we add the filtered results to searchList and notify that dataSet
         * has changed for component rerender.
         */
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            searchList.clear();
            searchList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
}
