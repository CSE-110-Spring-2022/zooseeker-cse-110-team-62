package com.example.zooseeker_t62;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ExhibitActivity extends AppCompatActivity {

    public RecyclerView recyclerView;
    public ExhibitViewModel viewModel;

    private EditText newTodoText;
    private Button addTodoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exhibit_list);

        viewModel = new ViewModelProvider(this)
                .get(ExhibitViewModel.class);

        ExhibitAdapter adapter = new ExhibitAdapter();
        adapter.setHasStableIds(true);
        adapter.setOnTextEditedHandler(viewModel::updateText);
        adapter.setOnDeleteButtonClickedHandler(viewModel::deleteTodo);
        viewModel.getExhibitItems().observe(this, adapter::setTodoListItems);

        recyclerView = findViewById(R.id.todo_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);



        this.newTodoText = this.findViewById(R.id.new_todo_text);
        this.addTodoButton = this.findViewById(R.id.add_todo_btn);

        addTodoButton.setOnClickListener(this::onAddTodoClicked);

    }

    void onAddTodoClicked(View view) {
        String text = newTodoText.getText().toString();
        newTodoText.setText("");
        viewModel.createExhibit(text);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void onGoBackClicked(View view) {
        finish();
    }
}