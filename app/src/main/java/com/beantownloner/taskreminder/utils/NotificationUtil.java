package com.beantownloner.taskreminder.utils;



import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import androidx.core.app.NotificationCompat;
//import android.support.v4.app.NotificationCompat;


import com.beantownloner.taskreminder.R;
import com.beantownloner.taskreminder.activities.ViewActivity;
import com.beantownloner.taskreminder.objects.Notifications;
import com.beantownloner.taskreminder.receivers.DismissReceiver;
import com.beantownloner.taskreminder.receivers.NagReceiver;
import com.beantownloner.taskreminder.receivers.SnoozeActionReceiver;

import java.util.Calendar;

public class NotificationUtil {
    private static NotificationManager notifManager;
    public static void createNotification(Context context, Notifications reminder) {
        String id ="default_channel_id";
        // Create intent for notification onClick behaviour
        Intent viewIntent = new Intent(context, ViewActivity.class);
        viewIntent.putExtra("NOTIFICATION_ID", reminder.getNotificationID());
        viewIntent.putExtra("NOTIFICATION_DISMISS", true);
        PendingIntent pending = PendingIntent.getActivity(context, reminder.getNotificationID(), viewIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder;
        if (notifManager == null) {
            notifManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        int imageResId = context.getResources().getIdentifier(reminder.getNotificationIcon(), "drawable", context.getPackageName());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = notifManager.getNotificationChannel(id);
            if (mChannel == null) {
                mChannel = new NotificationChannel(id, reminder.getNotificationTitle(), importance);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                notifManager.createNotificationChannel(mChannel);
            }
            builder = new NotificationCompat.Builder(context, id);


        }
        else {
            builder = new NotificationCompat.Builder(context, id);


        }

        // Create intent for notification snooze click behaviour
        Intent snoozeIntent = new Intent(context, SnoozeActionReceiver.class);
        snoozeIntent.putExtra("NOTIFICATION_ID", reminder.getNotificationID());
        PendingIntent pendingSnooze = PendingIntent.getBroadcast(context, reminder.getNotificationID(), snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        builder.setSmallIcon(imageResId)
                .setColor(Color.parseColor(reminder.getNotificationColor()))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(reminder.getNotificationContent()))
                .setContentTitle(reminder.getNotificationTitle())
                .setContentText(reminder.getNotificationContent())
                .setTicker(reminder.getNotificationTitle())
                .setContentIntent(pending);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        if (sharedPreferences.getBoolean("checkBoxNagging", false)) {
            Intent swipeIntent = new Intent(context, DismissReceiver.class);
            swipeIntent.putExtra("NOTIFICATION_ID", reminder.getNotificationID());
            PendingIntent pendingDismiss = PendingIntent.getBroadcast(context, reminder.getNotificationID(), swipeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setDeleteIntent(pendingDismiss);

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, sharedPreferences.getInt("nagMinutes", context.getResources().getInteger(R.integer.default_nag_minutes)));
            calendar.add(Calendar.SECOND, sharedPreferences.getInt("nagSeconds", context.getResources().getInteger(R.integer.default_nag_seconds)));
            Intent alarmIntent = new Intent(context, NagReceiver.class);
            AlarmUtil.setAlarm(context, alarmIntent, reminder.getNotificationID(), calendar);
        }

        String soundUri = sharedPreferences.getString("NotificationSound", "content://settings/system/notification_sound");
        if (soundUri.length() != 0) {
            builder.setSound(Uri.parse(soundUri));
        }
        if (sharedPreferences.getBoolean("checkBoxLED", true)) {
            builder.setLights(Color.BLUE, 700, 1500);
        }
        if (sharedPreferences.getBoolean("checkBoxOngoing", false)) {
            builder.setOngoing(true);
        }
        if (sharedPreferences.getBoolean("checkBoxVibrate", true)) {
            long[] pattern = {0, 300, 0};
            builder.setVibrate(pattern);
        }
        if (sharedPreferences.getBoolean("checkBoxMarkAsDone", true)) {
            Intent intent = new Intent(context, DismissReceiver.class);
            intent.putExtra("NOTIFICATION_ID", reminder.getNotificationID());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, reminder.getNotificationID(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.addAction(R.drawable.ic_done_white_24dp, context.getString(R.string.mark_as_done), pendingIntent);
        }
        if (sharedPreferences.getBoolean("checkBoxSnooze", true)) {
            builder.addAction(R.drawable.ic_snooze_white_24dp, context.getString(R.string.snooze), pendingSnooze);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            builder.setPriority(Notification.PRIORITY_HIGH);
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(reminder.getNotificationID(), builder.build());
    }

    public static void cancelNotification(Context context, int notificationId) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationId);
    }
}