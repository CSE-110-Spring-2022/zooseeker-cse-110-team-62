package com.example.zooseeker_t62;

import android.app.Application;
import android.content.Context;
import android.util.Log;

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

    public List<ExhibitItem> getList() {
        return exhibitItemDao.getAll();
    }

    private void loadUsers() {
        exhibitItems = exhibitItemDao.getAllLive();
        List<ExhibitItem> items = exhibitItemDao.getAll();
        if (items != null) {
            for (ExhibitItem item : items) {
                Log.d("ExhibitViewModel.java", item.toString());
            }

        }
    }

    public void deleteExhibit(ExhibitItem exhibitItem) {
        exhibitItemDao.delete(exhibitItem);
    }


    public void createExhibit(String id, String kind, String name, String[] tags) {
        //int endOfListOrder = exhibitItemDao.getOrderForAppend();
        ExhibitItem newItem = new ExhibitItem(id, kind, name, tags);
        exhibitItemDao.insert(newItem);
    }
    public void createExhibitFromList(ExhibitItem item) {
        exhibitItemDao.insert(item);
    }
}
