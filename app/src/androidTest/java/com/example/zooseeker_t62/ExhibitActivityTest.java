package com.example.zooseeker_t62;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.DataInteraction;
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
                .fallbackToDestructiveMigration()
                .build();
        ExhibitDatabase.injectTestDatabase(testDb);

        List<ExhibitItem> exhibits = ExhibitItem.loadJSON(context, "sample_node_info.json");
        exhibitItemDao = testDb.exhibitItemDao();
        exhibitItemDao.insertAll(exhibits);
    }



    @Test
    public void testDeleteExhibit() {
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

            ExhibitItem deletedExhibit = exhibitItemDao.get(id);
            assertNull(deletedExhibit);
        });
    }
}
