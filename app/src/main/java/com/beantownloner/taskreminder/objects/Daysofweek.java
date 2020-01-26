package com.beantownloner.taskreminder.objects;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "DAYS_OF_WEEK")
public class Daysofweek {


    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID")
    public Integer  colId;


    @ColumnInfo(name = "SUNDAY")
    public String  colSunday;


    @ColumnInfo(name = "MONDAY")
    public String  colMonday;

    @ColumnInfo(name = "TUESDAY")
    public String  colTuesday;

    @ColumnInfo(name = "WEDNESDAY")
    public String  colWednesday;

    @ColumnInfo(name = "THURSDAY")
    public String  colThursday;

    @ColumnInfo(name = "FRIDAY")
    public String  colFriday;

    @ColumnInfo(name = "SATURDAY")
    public String  colSaturday;

    public Integer getColId() {
        return colId;
    }

    public void setColId(Integer colId) {
        this.colId = colId;
    }

    public String getColSunday() {
        return colSunday;
    }

    public void setColSunday(String colSunday) {
        this.colSunday = colSunday;
    }

    public String getColMonday() {
        return colMonday;
    }

    public void setColMonday(String colMonday) {
        this.colMonday = colMonday;
    }

    public String getColTuesday() {
        return colTuesday;
    }

    public void setColTuesday(String colTuesday) {
        this.colTuesday = colTuesday;
    }

    public String getColWednesday() {
        return colWednesday;
    }

    public void setColWednesday(String colWednesday) {
        this.colWednesday = colWednesday;
    }

    public String getColThursday() {
        return colThursday;
    }

    public void setColThursday(String colThursday) {
        this.colThursday = colThursday;
    }

    public String getColFriday() {
        return colFriday;
    }

    public void setColFriday(String colFriday) {
        this.colFriday = colFriday;
    }

    public String getColSaturday() {
        return colSaturday;
    }

    public void setColSaturday(String colSaturday) {
        this.colSaturday = colSaturday;
    }
}
