package com.beantownloner.taskreminder.activities;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;

import androidx.core.view.ViewCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.transition.Explode;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;


import com.beantownloner.taskreminder.R;

import com.beantownloner.taskreminder.database.AppDatabase;
import com.beantownloner.taskreminder.database.MainViewModel;
import com.beantownloner.taskreminder.database.MainViewModelFactory;
import com.beantownloner.taskreminder.models.ReminderConstants;
import com.beantownloner.taskreminder.objects.Daysofweek;
import com.beantownloner.taskreminder.objects.Notifications;
import com.beantownloner.taskreminder.receivers.AlarmReceiver;
import com.beantownloner.taskreminder.receivers.DismissReceiver;
import com.beantownloner.taskreminder.receivers.SnoozeReceiver;
import com.beantownloner.taskreminder.utils.AlarmUtil;
import com.beantownloner.taskreminder.utils.DateAndTimeUtil;
import com.beantownloner.taskreminder.utils.NotificationUtil;
import com.beantownloner.taskreminder.utils.TextFormatUtil;
import com.google.android.material.snackbar.Snackbar;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewActivity extends AppCompatActivity {
    private static final String TAG = ViewActivity.class.getSimpleName();
    @BindView(R.id.notification_title)
    TextView notificationTitleText;
    @BindView(R.id.notification_time)
    TextView notificationTimeText;
    @BindView(R.id.notification_content)
    TextView contentText;
    @BindView(R.id.notification_icon)
    ImageView iconImage;
    @BindView(R.id.notification_circle)
    ImageView circleImage;
    @BindView(R.id.time)
    TextView timeText;
    @BindView(R.id.date)
    TextView dateText;
    @BindView(R.id.repeat)
    TextView repeatText;
    @BindView(R.id.shown)
    TextView shownText;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.detail_layout)
    LinearLayout linearLayout;
    @BindView(R.id.toolbar_shadow)
    View shadowView;
    @BindView(R.id.scroll)
    ScrollView scrollView;
    @BindView(R.id.header)
    View headerView;
    @BindView(R.id.view_coordinator)
    CoordinatorLayout coordinatorLayout;

    private AppDatabase database;
    private LiveData<Notifications> reminder;
    private Notifications notificationReminder;
    private boolean hideMarkAsDone;
    private boolean reminderChanged;
    private MainViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        ButterKnife.bind(this);


        setupTransitions();

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        if (getActionBar() != null) getActionBar().setDisplayHomeAsUpEnabled(true);
        if (getSupportActionBar() != null) getSupportActionBar().setTitle(null);
        getOverflowMenu();
        // Add drawable shadow and adjust layout if build version is before lollipop
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            linearLayout.setPadding(0, 0, 0, 0);
            shadowView.setVisibility(View.VISIBLE);
        } else {
            ViewCompat.setElevation(headerView, getResources().getDimension(R.dimen.toolbar_elevation));
        }
        database = AppDatabase.getInstance(this);


        Intent intent = getIntent();
        int mReminderId = intent.getIntExtra("NOTIFICATION_ID", 0);
Log.d(TAG, " NOTIFICATION_ID = "+mReminderId);
        MainViewModelFactory factory = new MainViewModelFactory(database, mReminderId);
        viewModel = ViewModelProviders.of(this,factory).get(MainViewModel.class);

notificationReminder = new Notifications();
        // Arrived to activity from notification on click
        // Cancel notification and nag alarm
        if (intent.getBooleanExtra("NOTIFICATION_DISMISS", false)) {
            Intent dismissIntent = new Intent().setClass(this, DismissReceiver.class);
            dismissIntent.putExtra("NOTIFICATION_ID", mReminderId);
            sendBroadcast(dismissIntent);
        }

        // Check if notification has been deleted
       // LiveData<Notifications> notificationsLiveData= viewModel.getNotification();
       // if (notificationsLiveData != null) {


            viewModel.getNotification().observe(this, new Observer<Notifications>() {
                @Override
                public void onChanged(@Nullable Notifications notifications) {
                    notificationReminder = notifications;
                    getDaysOfWeek(notifications.getNotificationID());
                    assignReminderValues();




                }
            });

      //  } else {

      //      returnHome();
     //   }
    }
    private void getDaysOfWeek(final int notificationID) {
        ExecutorService service = Executors.newFixedThreadPool(3);
        //  final int[] iCount = {0};
        final Daysofweek[] nn = {new Daysofweek()};
        Future<Daysofweek> retInt =  service.submit(new Callable<Daysofweek>() {
            @Override
            public Daysofweek call() throws Exception {
                nn[0] =  database.daysofweekDAO().getDaysOfWeek(notificationID);
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
        notificationReminder.setDaysOfWeek(daysOfWeek);

    }

    public void assignReminderValues() {
        Calendar calendar = DateAndTimeUtil.parseDateAndTime(notificationReminder.getNotificationDT()+"");
        notificationTitleText.setText(notificationReminder.getNotificationTitle());
        contentText.setText(notificationReminder.getNotificationContent());
        dateText.setText(DateAndTimeUtil.toStringReadableDate(calendar));
        iconImage.setImageResource(getResources().getIdentifier(notificationReminder.getNotificationIcon(), "drawable", getPackageName()));
        circleImage.setColorFilter(Color.parseColor(notificationReminder.getNotificationColor()));
        String readableTime = DateAndTimeUtil.toStringReadableTime(calendar, this);
        timeText.setText(readableTime);
        notificationTimeText.setText(readableTime);

        if (notificationReminder.getRepeatType() == ReminderConstants.SPECIFIC_DAYS) {
            repeatText.setText(TextFormatUtil.formatDaysOfWeekText(this, notificationReminder.getDaysOfWeek()));
        } else {
            if (notificationReminder.getNotificationInterval() > 1) {
                repeatText.setText(TextFormatUtil.formatAdvancedRepeatText(this, notificationReminder.getRepeatType(), notificationReminder.getNotificationInterval()));
            } else {
                repeatText.setText(getResources().getStringArray(R.array.repeat_array)[notificationReminder.getRepeatType()]);
            }
        }

        if (Boolean.parseBoolean(notificationReminder.getRepeatForever())) {
            shownText.setText(R.string.forever);
        } else {
            shownText.setText(getString(R.string.times_shown, notificationReminder.getNumberShown(), notificationReminder.getNumberToShow()));
        }

        // Hide "Mark as done" action if reminder is inactive
        hideMarkAsDone = notificationReminder.getNumberToShow() <= notificationReminder.getNumberShown() && !Boolean.parseBoolean(notificationReminder.getRepeatForever());
        invalidateOptionsMenu();
    }

    public void setupTransitions() {
        // Add shared element transition animation if on Lollipop or later
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Enter transitions
            TransitionSet setEnter = new TransitionSet();

            Transition slideDown = new Explode();
            slideDown.addTarget(headerView);
            slideDown.excludeTarget(scrollView, true);
            slideDown.setDuration(500);
            setEnter.addTransition(slideDown);

            Transition fadeOut = new Slide(Gravity.BOTTOM);
            fadeOut.addTarget(scrollView);
            fadeOut.setDuration(500);
            setEnter.addTransition(fadeOut);

            // Exit transitions
            TransitionSet setExit = new TransitionSet();

            Transition slideDown2 = new Explode();
            slideDown2.addTarget(headerView);
            slideDown2.setDuration(570);
            setExit.addTransition(slideDown2);

            Transition fadeOut2 = new Slide(Gravity.BOTTOM);
            fadeOut2.addTarget(scrollView);
            fadeOut2.setDuration(280);
            setExit.addTransition(fadeOut2);

            getWindow().setEnterTransition(setEnter);
            getWindow().setReturnTransition(setExit);
        }
    }

    public void confirmDelete() {
        new AlertDialog.Builder(this, R.style.Dialog)
                .setMessage(R.string.delete_confirmation)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        actionDelete();
                    }
                })
                .setNegativeButton(R.string.no, null).show();
    }

    public void actionShowNow() {
        NotificationUtil.createNotification(this, notificationReminder);
    }

    public void actionDelete() {

if (viewModel.getNotification().hasActiveObservers()) {

    viewModel.getNotification().removeObservers( this);
}
Log.d(TAG," NOTIFICATION_ID when delete = "+notificationReminder.getNotificationID());
        viewModel.deleteNotification();
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        AlarmUtil.cancelAlarm(this, alarmIntent, notificationReminder.getNotificationID());
        Intent snoozeIntent = new Intent(this, SnoozeReceiver.class);
        AlarmUtil.cancelAlarm(this, snoozeIntent, notificationReminder.getNotificationID());
        finish();
    }

    public void actionEdit() {
        Intent intent = new Intent(this, CreateEditActivity.class);
        intent.putExtra("NOTIFICATION_ID", notificationReminder.getNotificationID());
        startActivity(intent);
        finish();
    }

    public void actionMarkAsDone() {
        reminderChanged = true;

        // Check whether next alarm needs to be set
        if (notificationReminder.getNumberShown() + 1 != notificationReminder.getNumberToShow() || Boolean.parseBoolean(notificationReminder.getRepeatForever())) {
            AlarmUtil.setNextAlarm(this, notificationReminder,database);
        } else {
            Intent alarmIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
            AlarmUtil.cancelAlarm(this, alarmIntent, notificationReminder.getNotificationID());
            notificationReminder.setNotificationDT(DateAndTimeUtil.toLongDateAndTime(Calendar.getInstance()));
        }
        notificationReminder.setNumberShown(notificationReminder.getNumberShown() + 1);
        database.notificationsDAO().updateNotification(notificationReminder);
        assignReminderValues();

        Snackbar.make(coordinatorLayout, R.string.toast_mark_as_done, Snackbar.LENGTH_SHORT).show();
    }


    public void returnHome() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void updateReminder() {

        reminder = database.notificationsDAO().getNotifaction(notificationReminder.getNotificationID());

        assignReminderValues();
    }

    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, new IntentFilter("BROADCAST_REFRESH"));
       // updateReminder();
        super.onResume();
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
        super.onPause();
    }

    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            reminderChanged = true;
            updateReminder();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_viewer, menu);
        if (hideMarkAsDone) {
            menu.findItem(R.id.action_mark_as_done).setVisible(false);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (reminderChanged) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_delete:
                confirmDelete();
                return true;
            case R.id.action_edit:
                actionEdit();
                return true;

            case R.id.action_mark_as_done:
                actionMarkAsDone();
                return true;
            case R.id.action_show_now:
                actionShowNow();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getOverflowMenu() {

        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if(menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}