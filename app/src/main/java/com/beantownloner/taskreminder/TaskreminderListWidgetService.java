package com.beantownloner.taskreminder;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class TaskreminderListWidgetService  extends RemoteViewsService {

private static String TAG = TaskreminderListWidgetService.class.getSimpleName();
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new TaskreminderListRemoteViewsFactory(this.getApplicationContext());
    }
}
