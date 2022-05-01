package com.example.zooseeker_t62;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class ExhibitAdapter extends RecyclerView.Adapter<ExhibitAdapter.ViewHolder> {
    private List<ExhibitItem> exhibitItems = Collections.emptyList();
    private Consumer<ExhibitItem> onTextClicked;
    private Consumer<ExhibitItem> onDeleteButtonClicked;

    public void setTodoListItems(List<ExhibitItem> newTodoItems) {
        this.exhibitItems.clear();
        this.exhibitItems = newTodoItems;
    }


    public void setOnTextEditedHandler(Consumer<ExhibitItem> onTextClicked) {
        this.onTextClicked = onTextClicked;
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
        holder.setExhibitItem(exhibitItems.get(position));
    }

    @Override
    public int getItemCount() {
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

            this.textView.setOnClickListener(view -> {
                if (onTextClicked == null) return;
                onTextClicked.accept(exhibitItem);
            });


            this.deleteButton.setOnClickListener(view -> {
                if (onDeleteButtonClicked == null) return;
                onDeleteButtonClicked.accept(exhibitItem);
            });
        }

        public ExhibitItem getExhibitItem() {return exhibitItem;}
        public void setExhibitItem(ExhibitItem exhibitItem) {
            this.exhibitItem = exhibitItem;
            this.textView.setText(exhibitItem.name);
        }
    }
}
