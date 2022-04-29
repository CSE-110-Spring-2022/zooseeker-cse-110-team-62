package com.example.zooseeker_t62;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class ExhibitActivityTest {
    ExhibitDatabase testDb;
    ExhibitItemDao exhibitItemDao;

    private static void forceLayout(RecyclerView recyclerView) {
        recyclerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        recyclerView.layout(0,0,1080,2280);
    }

    @Before
    public void resetDatabase() {
        Context context = ApplicationProvider.getApplicationContext();
        testDb = Room.inMemoryDatabaseBuilder(context, ExhibitDatabase.class)
                .allowMainThreadQueries()
                .build();
        ExhibitDatabase.injectTestDatabase(testDb);

        List<ExhibitItem> todos = ExhibitItem.loadJSON(context, "demo_todos.json");
        exhibitItemDao = testDb.exhibitItemDao();
        exhibitItemDao.insertAll(todos);
    }

    @Test
    public void testEditTodoText() {
        String newText = "Ensure all tests pass";
        ActivityScenario<ExhibitActivity> scenario = ActivityScenario.launch(ExhibitActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            RecyclerView recyclerView = activity.recyclerView;
            RecyclerView.ViewHolder firstVH = recyclerView.findViewHolderForAdapterPosition(0);
            assertNotNull(firstVH);
            long id = firstVH.getItemId();

            EditText todoText = firstVH.itemView.findViewById(R.id.exhibit_item_text);
            todoText.requestFocus();
            todoText.setText("Ensure all tests pass");
            todoText.clearFocus();

            ExhibitItem editedItem = exhibitItemDao.get(id);
            assertEquals(newText, editedItem.text);
        });
    }

    @Test
    public void testAddNewTodo() {
        String newText = "Ensure all tests pass";

        ActivityScenario<ExhibitActivity> scenario = ActivityScenario.launch(ExhibitActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            List<ExhibitItem> beforeExhibit = exhibitItemDao.getAll();

            EditText newTodoText = activity.findViewById(R.id.new_todo_text);
            Button addTodoButton = activity.findViewById(R.id.add_todo_btn);

            newTodoText.setText(newText);
            addTodoButton.performClick();

            List<ExhibitItem> afterExhibit = exhibitItemDao.getAll();
            assertEquals(beforeExhibit.size() + 1, afterExhibit.size());
            assertEquals(newText, afterExhibit.get(afterExhibit.size()-1).text);
        });
    }

    @Test
    public void testDeleteTodo() {
        ActivityScenario<ExhibitActivity> scenario = ActivityScenario.launch(ExhibitActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            List<ExhibitItem> beforeExhibit = exhibitItemDao.getAll();

            RecyclerView recyclerView = activity.recyclerView;
            RecyclerView.ViewHolder firstVH = recyclerView.findViewHolderForAdapterPosition(0);
            assertNotNull(firstVH);
            long id = firstVH.getItemId();

            View deleteButton = firstVH.itemView.findViewById(R.id.delete_btn);
            deleteButton.performClick();

            List<ExhibitItem> afterExhibit = exhibitItemDao.getAll();
            assertEquals(beforeExhibit.size()-1, afterExhibit.size());

            ExhibitItem editedItem = exhibitItemDao.get(id);
            assertNull(editedItem);
        });
    }

    //@Test
    /*public void testCheckOffTodo() {
        ActivityScenario<ExhibitActivity> scenario = ActivityScenario.launch(ExhibitActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            RecyclerView recyclerView = activity.recyclerView;
            RecyclerView.ViewHolder firstVH = recyclerView.findViewHolderForAdapterPosition(0);
            assertNotNull(firstVH);
            long id = firstVH.getItemId();

            CheckBox todoCheck = firstVH.itemView.findViewById(R.id.completed);
            todoCheck.setChecked(false);

            ExhibitItem checkedBox = exhibitItemDao.get(id);
            assertEquals(true, checkedBox.completed);
        });
    }*/
}
