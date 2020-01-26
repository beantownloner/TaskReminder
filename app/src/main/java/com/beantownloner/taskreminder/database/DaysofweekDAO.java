package com.beantownloner.taskreminder.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.beantownloner.taskreminder.objects.Daysofweek;


@Dao
public interface DaysofweekDAO {

    @Query("SELECT * from DAYS_OF_WEEK where ID = :notificationID LIMIT 1")
    Daysofweek getDaysOfWeek(long notificationID);
    @Insert
    void addDaysofweek(Daysofweek daysofweek);
    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateDaysofweek(Daysofweek daysofweek);
}
