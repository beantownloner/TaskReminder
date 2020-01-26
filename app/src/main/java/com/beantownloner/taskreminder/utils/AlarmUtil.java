package com.beantownloner.taskreminder.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;


//import com.beantownloner.taskreminder.database.DatabaseHelper;
import com.beantownloner.taskreminder.database.AppDatabase;
import com.beantownloner.taskreminder.database.AppExecutors;
import com.beantownloner.taskreminder.models.ReminderConstants;
import com.beantownloner.taskreminder.objects.Daysofweek;
import com.beantownloner.taskreminder.objects.Notifications;
import com.beantownloner.taskreminder.receivers.AlarmReceiver;

import java.util.Calendar;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AlarmUtil {
    public static final String TAG = AlarmUtil.class.getSimpleName();
    public static void setAlarm(Context context, Intent intent, int notificationId, Calendar calendar) {
        intent.putExtra("NOTIFICATION_ID", notificationId);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

    public static void cancelAlarm(Context context, Intent intent, int notificationId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
    }

    public static void setNextAlarm(Context context, final Notifications reminder, final AppDatabase database) {
        Calendar calendar = DateAndTimeUtil.parseDateAndTime(reminder.getNotificationDT()+"");
        calendar.set(Calendar.SECOND, 0);

        Log.d(TAG, " reminder repeat type = "+reminder.getRepeatType());
        Log.d(TAG, " reminder repeat forever? = "+reminder.getRepeatForever());
        Log.d(TAG," reminder time is = "+reminder.getNotificationDT());
        Log.d(TAG," calendar value = "+ calendar.toString());
        switch (reminder.getRepeatType()) {
            case ReminderConstants.HOURLY:
                calendar.add(Calendar.HOUR, reminder.getNotificationInterval());
                break;
            case ReminderConstants.DAILY:
                calendar.add(Calendar.DATE, reminder.getNotificationInterval());
                break;
            case ReminderConstants.WEEKLY:
                calendar.add(Calendar.WEEK_OF_YEAR, reminder.getNotificationInterval());
                break;
            case ReminderConstants.MONTHLY:
                calendar.add(Calendar.MONTH, reminder.getNotificationInterval());
                break;
            case ReminderConstants.YEARLY:
                calendar.add(Calendar.YEAR, reminder.getNotificationInterval());
                break;
            case ReminderConstants.SPECIFIC_DAYS:
                Calendar weekCalendar = (Calendar) calendar.clone();
                weekCalendar.add(Calendar.DATE, 1);
                getDaysOfWeek(reminder,database);
                for (int i = 0; i < 7; i++) {
                    int position = (i + (weekCalendar.get(Calendar.DAY_OF_WEEK) - 1)) % 7;
                    if (reminder.getDaysOfWeek()[position]) {
                        calendar.add(Calendar.DATE, i + 1);
                        break;
                    }
                }
                break;
        }

        reminder.setNotificationDT(DateAndTimeUtil.toLongDateAndTime(calendar));
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                database.notificationsDAO().updateNotification(reminder);
            }
        });

        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        setAlarm(context, alarmIntent, reminder.getNotificationID(), calendar);
    }

    private static void getDaysOfWeek(final Notifications notification, final AppDatabase database) {
        ExecutorService service = Executors.newFixedThreadPool(3);
        //  final int[] iCount = {0};
        final Daysofweek[] nn = {new Daysofweek()};
        Future<Daysofweek> retInt =  service.submit(new Callable<Daysofweek>() {
            @Override
            public Daysofweek call() throws Exception {
                nn[0] =  database.daysofweekDAO().getDaysOfWeek(notification.getNotificationID());
                return nn[0];
            }

        });

        Daysofweek dow = new Daysofweek() ;
        try {
            dow = retInt.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        // Log.d(TAG," dow Received "+notificationID);

        service.shutdown();


        boolean[] daysOfWeek = new boolean[7];

        if (dow != null) {
            daysOfWeek[0] = Boolean.parseBoolean(dow.getColSunday());
            daysOfWeek[1] = Boolean.parseBoolean(dow.getColMonday());
            daysOfWeek[2] = Boolean.parseBoolean(dow.getColTuesday());
            daysOfWeek[3] = Boolean.parseBoolean(dow.getColWednesday());
            daysOfWeek[4] = Boolean.parseBoolean(dow.getColThursday());
            daysOfWeek[5] = Boolean.parseBoolean(dow.getColFriday());
            daysOfWeek[6] = Boolean.parseBoolean(dow.getColSaturday());

        }
        notification.setDaysOfWeek(daysOfWeek);

    }
}