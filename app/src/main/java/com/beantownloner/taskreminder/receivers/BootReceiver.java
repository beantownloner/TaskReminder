package com.beantownloner.taskreminder.receivers;

import androidx.lifecycle.LiveData;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


//import com.beantownloner.taskreminder.database.DatabaseHelper;
import com.beantownloner.taskreminder.database.AppDatabase;
import com.beantownloner.taskreminder.objects.Notifications;
import com.beantownloner.taskreminder.utils.AlarmUtil;
import com.beantownloner.taskreminder.utils.DateAndTimeUtil;

import java.util.Calendar;
import java.util.List;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        AppDatabase database;
        database = AppDatabase.getInstance(context);
       final LiveData<List<Notifications>> reminderList = database.notificationsDAO().loadActiveNotifications();

       List<Notifications> reminderList2 = reminderList.getValue();
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);

        if (reminderList2 != null) {
            for (Notifications reminder : reminderList2) {
                Calendar calendar = DateAndTimeUtil.parseDateAndTime(reminder.getNotificationDT() + "");
                calendar.set(Calendar.SECOND, 0);
                AlarmUtil.setAlarm(context, alarmIntent, reminder.getNotificationID(), calendar);
            }
        }
    }
}