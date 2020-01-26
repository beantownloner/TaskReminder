package com.beantownloner.taskreminder.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

//import com.beantownloner.taskreminder.database.DatabaseHelper;
import com.beantownloner.taskreminder.database.AppDatabase;
import com.beantownloner.taskreminder.objects.Notifications;
import com.beantownloner.taskreminder.utils.NotificationUtil;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class SnoozeReceiver extends BroadcastReceiver {
    public static final String TAG = NagReceiver.class.getSimpleName();
    AppDatabase database;
    Notifications reminder;
    @Override
    public void onReceive(Context context, Intent intent) {


        database = AppDatabase.getInstance(context);
        final int reminderId = intent.getIntExtra("NOTIFICATION_ID", 0);

        ExecutorService service = Executors.newFixedThreadPool(3);
        //  final int[] iCount = {0};
        final Notifications[] nn = {new Notifications()};
        Future<Notifications> retInt =  service.submit(new Callable<Notifications>() {
            @Override
            public Notifications call() throws Exception {
                nn[0] =  database.notificationsDAO().getRawNotifaction(reminderId);
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
        Log.d(TAG," Nag Received "+reminderId);

        service.shutdown();
        if (reminder != null) {

            Log.d(TAG, " is reminder not null ? ");
            NotificationUtil.createNotification(context, reminder);
        } else {
            Log.d(TAG, " is reminder  null ? ");

        }



    }
}