package com.example.zooseeker_t62;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class ExhibitViewModel extends AndroidViewModel {
    private LiveData<List<ExhibitItem>> exhibitItems;
    private final ExhibitItemDao exhibitItemDao;

    public ExhibitViewModel(@NonNull Application application) {
        super(application);
        Context context = getApplication().getApplicationContext();
        ExhibitDatabase db = ExhibitDatabase.getSingleton(context);
        exhibitItemDao = db.exhibitItemDao();
    }

    public LiveData<List<ExhibitItem>> getExhibitItems() {
        if (exhibitItems == null) {
            loadUsers();
        }
        return exhibitItems;
    }

    private void loadUsers() {
        exhibitItems = exhibitItemDao.getAllLive();
    }


    public void deleteTodo(ExhibitItem exhibitItem) {
        exhibitItemDao.delete(exhibitItem);
    }

    public void updateText(ExhibitItem exhibitItem) {
        exhibitItemDao.update(exhibitItem);
    }

    public void createExhibit(String id, String name, String[] tags) {
        //int endOfListOrder = exhibitItemDao.getOrderForAppend();
        ExhibitItem newItem = new ExhibitItem(id, "exhibit", name, tags);
        exhibitItemDao.insert(newItem);
    }
}
