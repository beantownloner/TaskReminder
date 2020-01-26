package com.beantownloner.taskreminder.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.beantownloner.taskreminder.database.AppDatabase;
import com.beantownloner.taskreminder.database.AppExecutors;
import com.beantownloner.taskreminder.objects.Notifications;
import com.beantownloner.taskreminder.utils.AlarmUtil;
import com.beantownloner.taskreminder.utils.NotificationUtil;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

//import com.beantownloner.taskreminder.database.DatabaseHelper;


public class AlarmReceiver extends BroadcastReceiver {
    public static final String TAG = AlarmReceiver.class.getSimpleName();
    private Notifications reminder;

    @Override
    public void onReceive(Context context, Intent intent) {

        final AppDatabase database;
        database = AppDatabase.getInstance(context);
        final int notificationID = intent.getIntExtra("NOTIFICATION_ID", 0);
        //LiveData<Notifications> reminderLive = database.notificationsDAO().getNotifaction(notificationID);


        ExecutorService service = Executors.newFixedThreadPool(3);
        //  final int[] iCount = {0};
        final Notifications[] nn = {new Notifications()};
        Future<Notifications> retInt = service.submit(new Callable<Notifications>() {
            @Override
            public Notifications call() throws Exception {
                nn[0] = database.notificationsDAO().getRawNotifaction(notificationID);
                return nn[0];
            }

        });


        try {
            reminder = retInt.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Log.d(TAG, " Alarm Received " + notificationID);

        service.shutdown();
        if (reminder == null) {

            Log.d(TAG, " is reminder null ? ");
        } else {
            Log.d(TAG, " is reminder not null ? ");

        }
        int newNumberShown = reminder.getNumberShown() + 1;
        reminder.setNumberShown(newNumberShown);
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                database.notificationsDAO().updateNotification(reminder);
            }
        });


        NotificationUtil.createNotification(context, reminder);

        // Check if new alarm needs to be set
        if (reminder.getNumberToShow() > reminder.getNumberShown() || Boolean.parseBoolean(reminder.getRepeatForever())) {
            AlarmUtil.setNextAlarm(context, reminder, database);
        }
        Intent updateIntent = new Intent("BROADCAST_REFRESH");
        LocalBroadcastManager.getInstance(context).sendBroadcast(updateIntent);

    }
}