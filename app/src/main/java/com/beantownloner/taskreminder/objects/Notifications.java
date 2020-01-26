package com.beantownloner.taskreminder.objects;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "Notifications")
public class Notifications implements Parcelable {



    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID")
    public Integer  notificationID;



    @ColumnInfo(name= "TITLE")
    public String notificationTitle;

    @ColumnInfo(name= "CONTENT")
    public String notificationContent;

    @ColumnInfo(name= "DATE_AND_TIME")
    public Long notificationDT;

    @ColumnInfo(name= "DREPEAT_TYPE")
    public Integer repeatType;

    @ColumnInfo(name= "FOREVER")
    public String repeatForever;

    @ColumnInfo(name = "NUMBER_TO_SHOW")
    public Integer  numberToShow;

    @ColumnInfo(name = "NUMBER_SHOWN")
    public Integer  numberShown;

    @ColumnInfo(name = "ICON")
    public String  notificationIcon;

    @ColumnInfo(name= "COLOR")
    public String notificationColor;

    @ColumnInfo(name= "INTERVAL")
    public Integer notificationInterval;


    @Ignore
   public boolean[] daysOfWeek;

    public Notifications() {
    }

    @Ignore
    public Notifications(Integer notificationID, String notificationTitle, String notificationContent, Long notificationDT, Integer repeatType, String repeatForever, Integer numberToShow, Integer numberShown, String notificationIcon, String notificationColor, Integer notificationInterval) {
        this.notificationID = notificationID;
        this.notificationTitle = notificationTitle;
        this.notificationContent = notificationContent;
        this.notificationDT = notificationDT;
        this.repeatType = repeatType;
        this.repeatForever = repeatForever;
        this.numberToShow = numberToShow;
        this.numberShown = numberShown;
        this.notificationIcon = notificationIcon;
        this.notificationColor = notificationColor;
        this.notificationInterval = notificationInterval;
    }

    protected Notifications(Parcel in) {
        if (in.readByte() == 0) {
            notificationID = null;
        } else {
            notificationID = in.readInt();
        }
        notificationTitle = in.readString();
        notificationContent = in.readString();
        if (in.readByte() == 0) {
            notificationDT = null;
        } else {
            notificationDT = in.readLong();
        }
        if (in.readByte() == 0) {
            repeatType = null;
        } else {
            repeatType = in.readInt();
        }
        repeatForever = in.readString();
        if (in.readByte() == 0) {
            numberToShow = null;
        } else {
            numberToShow = in.readInt();
        }
        if (in.readByte() == 0) {
            numberShown = null;
        } else {
            numberShown = in.readInt();
        }
        notificationIcon = in.readString();
        notificationColor = in.readString();
        if (in.readByte() == 0) {
            notificationInterval = null;
        } else {
            notificationInterval = in.readInt();
        }
        daysOfWeek = in.createBooleanArray();
    }

    public static final Creator<Notifications> CREATOR = new Creator<Notifications>() {
        @Override
        public Notifications createFromParcel(Parcel in) {
            return new Notifications(in);
        }

        @Override
        public Notifications[] newArray(int size) {
            return new Notifications[size];
        }
    };

    public Integer getNotificationID() {
        return notificationID;
    }

    public void setNotificationID(Integer notificationID) {
        this.notificationID = notificationID;
    }

    public String getNotificationContent() {
        return notificationContent;
    }

    public void setNotificationContent(String notificationContent) {
        this.notificationContent = notificationContent;
    }

    public long getNotificationDT() {
        return notificationDT;
    }

    public void setNotificationDT(long notificationDT) {
        this.notificationDT = notificationDT;
    }

    public Integer getRepeatType() {
        return repeatType;
    }

    public void setRepeatType(Integer repeatType) {
        this.repeatType = repeatType;
    }

    public String getRepeatForever() {
        return repeatForever;
    }

    public void setRepeatForever(String repeatForever) {
        this.repeatForever = repeatForever;
    }

    public Integer getNumberToShow() {
        return numberToShow;
    }

    public void setNumberToShow(Integer numberToShow) {
        this.numberToShow = numberToShow;
    }

    public Integer getNumberShown() {
        return numberShown;
    }

    public void setNumberShown(Integer numberShown) {
        this.numberShown = numberShown;
    }

    public String getNotificationColor() {
        return notificationColor;
    }

    public void setNotificationColor(String notificationColor) {
        this.notificationColor = notificationColor;
    }

    public Integer getNotificationInterval() {
        return notificationInterval;
    }

    public void setNotificationInterval(Integer notificationInterval) {
        this.notificationInterval = notificationInterval;
    }
    public String getNotificationTitle() {
        return notificationTitle;
    }

    public void setNotificationTitle(String notificationTitle) {
        this.notificationTitle = notificationTitle;
    }

    public String getNotificationIcon() {
        return notificationIcon;
    }

    public void setNotificationIcon(String notificationIcon) {
        this.notificationIcon = notificationIcon;
    }

    public String getDate() {

        return notificationDT.toString().substring(0, 8);
    }

    public boolean[] getDaysOfWeek() {
        return daysOfWeek;
    }

   public void setDaysOfWeek(boolean[] daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (notificationID == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(notificationID);
        }
        dest.writeString(notificationTitle);
        dest.writeString(notificationContent);
        if (notificationDT == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(notificationDT);
        }
        if (repeatType == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(repeatType);
        }
        dest.writeString(repeatForever);
        if (numberToShow == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(numberToShow);
        }
        if (numberShown == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(numberShown);
        }
        dest.writeString(notificationIcon);
        dest.writeString(notificationColor);
        if (notificationInterval == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(notificationInterval);
        }
        dest.writeBooleanArray(daysOfWeek);
    }
}

