package com.example.zooseeker_t62;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.List;
import java.util.concurrent.Executors;

/**
 * @description: This class manages a database our other classes may interact with
 */
@Database(entities = {ExhibitItem.class}, version = 4)
@TypeConverters({Converters.class})
public abstract class ExhibitDatabase extends RoomDatabase {
    private static ExhibitDatabase singleton = null;
    public abstract ExhibitItemDao exhibitItemDao();

    /**
     * @description: Retrieve the data singleton for use
     */
    public synchronized static ExhibitDatabase getSingleton(Context context) {
        if (singleton == null) {
            singleton = ExhibitDatabase.makeDatabase(context);
        }

        return singleton;
    }

    /**
     * @description: Reset the singleton to a mock database for testing purposes
     */
    @VisibleForTesting
    public static void injectTestDatabase(ExhibitDatabase testDatabase) {
        if (singleton != null) {
            singleton.close();
        }
        singleton = testDatabase;
    }

    /**
     * @description: Create the database for use with the Room API
     */
    private static ExhibitDatabase makeDatabase(Context context) {
        return Room.databaseBuilder(context, ExhibitDatabase.class, "todo_app.db")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        Executors.newSingleThreadExecutor().execute(() -> {
                            List<ExhibitItem> todos = ExhibitItem
                                    .loadJSON(context, "demo_todos.json");
                            getSingleton(context).exhibitItemDao().insertAll(todos);
                        });
                    }
                })
                .build();
    }
}
