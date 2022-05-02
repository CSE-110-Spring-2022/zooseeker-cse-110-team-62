package com.example.zooseeker_t62;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

@RunWith(AndroidJUnit4.class)
public class ExhibitDatabaseTest {
    private ExhibitItemDao dao;
    private ExhibitDatabase db;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, ExhibitDatabase.class)
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
        dao = db.exhibitItemDao();
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }


    @Test
    public void testInsert() {
        ExhibitItem item1 = new ExhibitItem("Entrance", "not-exhibit", "entrance",
                new String[]{"gate", "entry"});
        ExhibitItem item2 = new ExhibitItem("Exit", "not-exhibit", "entrance",
                new String[]{"gate", "exit"});

        long id1 = dao.insert(item1);
        long id2 = dao.insert(item2);

        assertNotEquals(id1, id2);
    }



    @Test
    public void testDelete() {
        ExhibitItem item = new ExhibitItem("Entrance", "not-exhibit", "entrance",
                new String[]{"gate", "entry"});
        long id = dao.insert(item);

        int itemsDeleted = dao.delete(item);
        assertNull(dao.get(id));
    }
}
