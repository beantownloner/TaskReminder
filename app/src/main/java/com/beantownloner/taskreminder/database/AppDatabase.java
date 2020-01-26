package com.beantownloner.taskreminder.database;

import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import android.content.Context;
import androidx.annotation.NonNull;
import android.util.Log;

import com.beantownloner.taskreminder.objects.Daysofweek;
import com.beantownloner.taskreminder.objects.Notifications;


import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.Room;
import androidx.room.RoomDatabase;



@Database(entities = {Notifications.class,Daysofweek.class}, version = 1, exportSchema = false)
public abstract  class AppDatabase extends RoomDatabase {


    private static final String TAG = AppDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME =  "TASKREMINDER_DB";
    private static AppDatabase sInstance;

    public abstract NotificationsDAO notificationsDAO();

    public abstract DaysofweekDAO daysofweekDAO();


    public static AppDatabase getInstance(final Context context) {

        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(TAG, "Creating new database instance");


                sInstance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, AppDatabase.DATABASE_NAME)
                          // .allowMainThreadQueries()
                        .addCallback(new RoomDatabase.Callback(){
                            @Override
                            public void onCreate(@androidx.annotation.NonNull SupportSQLiteDatabase db) {
                                super.onCreate(db);

                            }
                        })
                        .build();


            }
        }


        Log.d(TAG, "Getting the database instance");
        return sInstance;

    }



    @NonNull
    @Override
    protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config) {
        return null;
    }

    @NonNull
    @Override
    protected InvalidationTracker createInvalidationTracker() {
        return null;
    }


}
