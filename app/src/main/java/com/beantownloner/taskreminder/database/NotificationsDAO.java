package com.beantownloner.taskreminder.database;

import com.beantownloner.taskreminder.objects.Notifications;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface NotificationsDAO {

    @Query("SELECT * FROM Notifications ")
    List<Notifications> loadAllNotifications();

    @Query("SELECT * FROM Notifications WHERE NUMBER_SHOWN < NUMBER_TO_SHOW OR FOREVER = 'true'  ORDER BY DATE_AND_TIME ")
    LiveData<List<Notifications>> loadActiveNotifications();

    @Query("SELECT * FROM Notifications WHERE NUMBER_SHOWN = NUMBER_TO_SHOW AND FOREVER = 'false'  ORDER BY DATE_AND_TIME DESC ")
    LiveData<List<Notifications>> loadInactiveNotifications();


    @Query("SELECT * from Notifications where ID = :notificationID")
    LiveData<Notifications> getNotifaction(long notificationID);

    @Query("SELECT * from Notifications where ID = :notificationID")
    Notifications getRawNotifaction(long notificationID);


    @Query("SELECT * FROM Notifications ORDER BY ID DESC LIMIT 1")
    Notifications getLastNotification();


    @Query("delete from Notifications where ID = :notificationID")
    void deleteNotification(long notificationID);

    @Delete
    void deleteNotification(Notifications notification);

    @Insert
    void insertNotification(Notifications notification);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateNotification(Notifications notification);

}
