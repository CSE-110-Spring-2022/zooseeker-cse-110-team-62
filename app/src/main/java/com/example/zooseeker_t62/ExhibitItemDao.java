package com.example.zooseeker_t62;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ExhibitItemDao {
    @Insert
    long insert(ExhibitItem exhibitItem);

    @Insert
    List<Long> insertAll(List<ExhibitItem> exhibitItem);

    @Query("SELECT * FROM `exhibit_list_items` WHERE `id`=:id")
    ExhibitItem get(long id);

    @Query("SELECT * FROM `exhibit_list_items` ORDER BY `id`")
    List<ExhibitItem> getAll();

    @Query("SELECT * FROM `exhibit_list_items` ORDER BY `id`")
    LiveData<List<ExhibitItem>> getAllLive();

    @Query("SELECT `name` + 1 FROM `exhibit_list_items` ORDER BY `name` DESC LIMIT 1")
    int getOrderForAppend();

    @Delete
    int delete(ExhibitItem exhibitItem);
}
