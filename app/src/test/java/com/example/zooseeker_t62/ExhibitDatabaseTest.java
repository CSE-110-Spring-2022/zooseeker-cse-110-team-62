package com.example.zooseeker_t62;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import android.content.Context;

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
                .build();
        dao = db.exhibitItemDao();
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void testInsert() {
        ExhibitItem item1 = new ExhibitItem("Pizza time", false, 0);
        ExhibitItem item2 = new ExhibitItem("Photos of Spider-Man", false, 1);

        long id1 = dao.insert(item1);
        long id2 = dao.insert(item2);

        assertNotEquals(id1, id2);
    }

    @Test
    public void testGet() {
        ExhibitItem insertedItem = new ExhibitItem("Pizza time", false, 0);
        long id = dao.insert(insertedItem);

        ExhibitItem item = dao.get(id);
        assertEquals(id, item.id);
        assertEquals(insertedItem.text, item.text);
        assertEquals(insertedItem.completed, item.completed);
        assertEquals(insertedItem.order, item.order);
    }

    @Test
    public void testUpdate() {
        ExhibitItem item = new ExhibitItem("Pizza time", false, 0);
        long id = dao.insert(item);

        item = dao.get(id);
        item.text = "Photos of Spider-Man";
        int itemsUpdated = dao.update(item);
        assertEquals(1, itemsUpdated);

        item = dao.get(id);
        assertNotNull(item);
        assertEquals("Photos of Spider-Man", item.text);
    }

    @Test
    public void testDelete() {
        ExhibitItem item = new ExhibitItem("Pizza time", false, 0);
        long id = dao.insert(item);

        item = dao.get(id);
        int itemsDeleted = dao.delete(item);
        assertEquals(1, itemsDeleted);
        assertNull(dao.get(id));
    }
}
