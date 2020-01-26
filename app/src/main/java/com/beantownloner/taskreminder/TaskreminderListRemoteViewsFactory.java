package com.beantownloner.taskreminder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.beantownloner.taskreminder.activities.ViewActivity;
import com.beantownloner.taskreminder.database.AppDatabase;
import com.beantownloner.taskreminder.objects.Notifications;
import com.beantownloner.taskreminder.utils.DateAndTimeUtil;
import com.beantownloner.taskreminder.utils.SharedPreferenceUtil;

import java.util.Calendar;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class TaskreminderListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    Context mContext;
    private AppDatabase database;
    private List<Notifications> reminderList;

    private static String TAG = TaskreminderListRemoteViewsFactory.class.getSimpleName();
    public TaskreminderListRemoteViewsFactory(Context applicationContext) {
        Log.d(TAG, " WIDGETTEST ");
        mContext = applicationContext;
        database = AppDatabase.getInstance(mContext);

        reminderList = SharedPreferenceUtil.getNotificationListsFromSharedPreferences(mContext);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

        reminderList = SharedPreferenceUtil.getNotificationListsFromSharedPreferences(mContext);
        Log.d(TAG, " reminderList size =  "+reminderList.size());
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return reminderList != null  ?
                reminderList.size() : 0;
    }

    @Override
    public RemoteViews getViewAt(int position) {

        Notifications reminder = reminderList.get(position);

        if (reminder == null) return null;

        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_item);

        Calendar calendar = DateAndTimeUtil.parseDateAndTime(reminderList.get(position).getNotificationDT()+"");
        if (position > 0 && reminderList.get(position).getDate().equals(reminderList.get(position - 1).getDate()) ) {
            views.setViewVisibility(R.id.header_separator,GONE);
        } else {
            String appropriateDate = DateAndTimeUtil.getAppropriateDateFormat(mContext, calendar);
            views.setTextViewText(R.id.header_separator,appropriateDate);
            views.setViewVisibility(R.id.header_separator,VISIBLE);
        }

        views.setTextViewText(R.id.textview_notification_title, reminder.getNotificationTitle());
        views.setTextViewText(R.id.textview_notification_time,DateAndTimeUtil.toStringReadableTime(calendar, mContext));
        Log.d(TAG, " reminderList title =  "+reminder.getNotificationTitle());
        Bundle extras = new Bundle();
        extras.putInt("NOTIFICATION_ID", reminder.getNotificationID());
        Intent fillInIntent = new Intent(mContext, ViewActivity.class);
        fillInIntent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP);
        fillInIntent.putExtras(extras);
        views.setOnClickFillInIntent(R.id.textview_notification_title, fillInIntent);
        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
