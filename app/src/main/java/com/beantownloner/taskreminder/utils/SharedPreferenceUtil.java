package com.beantownloner.taskreminder.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;

import com.beantownloner.taskreminder.objects.Notifications;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SharedPreferenceUtil {

    private static final String PREFS_TAG = "SharedPrefs";
    private static final String Notification_TAG = "MyNotification";

    public static List<Notifications> getNotificationListsFromSharedPreferences(Context context){
        Gson gson = new Gson();
        List<Notifications> productFromShared = new ArrayList<>();
        SharedPreferences sharedPref =  PreferenceManager.getDefaultSharedPreferences(context);
        String jsonPreferences = sharedPref.getString(Notification_TAG, "");

        Type type = new TypeToken<List<Notifications>>() {}.getType();
        productFromShared = gson.fromJson(jsonPreferences, type);

        return productFromShared;
    }

    public static void saveNotificationLists(@NonNull Context context, List<Notifications> reminderList){

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(reminderList);
        prefsEditor.putString(Notification_TAG, json);
        prefsEditor.commit();
    }
}
