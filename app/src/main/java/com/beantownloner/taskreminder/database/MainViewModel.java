package com.beantownloner.taskreminder.database;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import android.util.Log;

import com.beantownloner.taskreminder.objects.Notifications;

import java.util.List;

public class MainViewModel extends ViewModel {

    private LiveData<Notifications> notification;
    private LiveData<List<Notifications>> activeNotifications;
    private LiveData<List<Notifications>> inactiveNotifications;
    private long notifiID;
    private AppDatabase mDB;

    private static final String TAG = MainViewModel.class.getSimpleName();

    public MainViewModel(AppDatabase database, long notificationID) {

        notifiID = notificationID;
        mDB = database;
        Log.d(TAG," notificationID , rowID = "+notificationID);
    }

    public LiveData<Notifications> getNotification() {

        if(notification == null) {notification = mDB.notificationsDAO().getNotifaction(notifiID);}
        return notification;
    }

    public LiveData<List<Notifications>> getActiveNotifications() {
        if (activeNotifications == null) {activeNotifications = mDB.notificationsDAO().loadActiveNotifications();}
        return activeNotifications;
    }

    public LiveData<List<Notifications>> getInactiveNotifications() {
            if (inactiveNotifications == null) {inactiveNotifications = mDB.notificationsDAO().loadInactiveNotifications();}

        return inactiveNotifications;
    }

    public void deleteNotification() {

        if (notification == null) return;
        AppExecutors.getInstance().diskIO().execute(new Runnable() {


            @Override
            public void run() {

                mDB.notificationsDAO().deleteNotification(notifiID);
            }
        });

    }
}
