package com.beantownloner.taskreminder.activities;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.beantownloner.taskreminder.R;
import com.beantownloner.taskreminder.database.AppDatabase;
import com.beantownloner.taskreminder.database.AppExecutors;
import com.beantownloner.taskreminder.dialogs.AdvancedRepeatSelector;
import com.beantownloner.taskreminder.dialogs.DaysOfWeekSelector;
import com.beantownloner.taskreminder.dialogs.RepeatSelector;
import com.beantownloner.taskreminder.models.ReminderConstants;
import com.beantownloner.taskreminder.objects.Daysofweek;
import com.beantownloner.taskreminder.objects.Notifications;
import com.beantownloner.taskreminder.receivers.AlarmReceiver;
import com.beantownloner.taskreminder.utils.AlarmUtil;
import com.beantownloner.taskreminder.utils.AnimationUtil;
import com.beantownloner.taskreminder.utils.DateAndTimeUtil;
import com.beantownloner.taskreminder.utils.TextFormatUtil;
import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreateEditActivity extends AppCompatActivity implements AdvancedRepeatSelector.AdvancedRepeatSelectionListener,
        DaysOfWeekSelector.DaysOfWeekSelectionListener, RepeatSelector.RepeatSelectionListener {
    public static final String TAG = CreateEditActivity.class.getSimpleName();
    @BindView(R.id.create_coordinator)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.notification_title)
    EditText titleEditText;
    @BindView(R.id.notification_content)
    EditText contentEditText;
    @BindView(R.id.time)
    TextView timeText;
    @BindView(R.id.date)
    TextView dateText;
    @BindView(R.id.repeat_day)
    TextView repeatText;
    @BindView(R.id.switch_toggle)
    SwitchCompat foreverSwitch;
    @BindView(R.id.show_times_number)
    EditText timesEditText;
    @BindView(R.id.forever_row)
    LinearLayout foreverRow;
    @BindView(R.id.bottom_row)
    LinearLayout bottomRow;
    @BindView(R.id.bottom_view)
    View bottomView;
    @BindView(R.id.show)
    TextView showText;
    @BindView(R.id.times)
    TextView timesText;
    @BindView(R.id.error_time)
    ImageView imageWarningTime;
    @BindView(R.id.error_date)
    ImageView imageWarningDate;
    @BindView(R.id.error_show)
    ImageView imageWarningShow;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private String icon;
    private String colour;
    private Calendar calendar;
    private boolean[] daysOfWeek = new boolean[7];
    private int timesShown = 0;
    private int timesToShow = 1;
    private int repeatType;
    private int saveType = ReminderConstants.INSERT;
    private int id;
    private int interval = 1;
    private AppDatabase database;
    private Notifications notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        if (getActionBar() != null) getActionBar().setDisplayHomeAsUpEnabled(true);
        if (getSupportActionBar() != null) getSupportActionBar().setTitle(null);

        calendar = Calendar.getInstance();
        icon = getString(R.string.default_icon_value);
        colour = getString(R.string.default_colour_value);
        repeatType = ReminderConstants.DOES_NOT_REPEAT;
        id = getIntent().getIntExtra("NOTIFICATION_ID", 0);
        database = AppDatabase.getInstance(this);
        // Check whether to edit or create a new notification
        if (id == 0) {
            saveType = ReminderConstants.INSERT;


            ExecutorService service = Executors.newFixedThreadPool(3);
            //  final int[] iCount = {0};
            final Notifications[] nn = {new Notifications()};
            Future<Notifications> retInt = service.submit(new Callable<Notifications>() {
                @Override
                public Notifications call() throws Exception {
                    nn[0] = database.notificationsDAO().getLastNotification();
                    return nn[0];
                }

            });


            try {
                notification = retInt.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }


            service.shutdown();
            if (notification == null) {

                Log.d(TAG, " is notification null ? ");
            } else {
                Log.d(TAG, " is notification not null ? ");

            }


            if (notification != null) {
                id = notification.getNotificationID() + 1;
            } else {

                id++;
            }
            Log.d(TAG, "id is " + id);
        } else {
            saveType = ReminderConstants.UPDATE;
            LiveData<Notifications> reminderLive = database.notificationsDAO().getNotifaction(id);

            reminderLive.observe(this, new Observer<Notifications>() {
                @Override
                public void onChanged(@Nullable Notifications notifications) {
                    getDaysOfWeek(notifications);
                    assignReminderValues(notifications);
                }
            });

        }
    }

    private void getDaysOfWeek(final Notifications notification) {
        ExecutorService service = Executors.newFixedThreadPool(3);
        //  final int[] iCount = {0};
        final Daysofweek[] nn = {new Daysofweek()};
        Future<Daysofweek> retInt = service.submit(new Callable<Daysofweek>() {
            @Override
            public Daysofweek call() throws Exception {
                nn[0] = database.daysofweekDAO().getDaysOfWeek(notification.getNotificationID());
                return nn[0];
            }

        });

        Daysofweek dow = new Daysofweek();
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

    public void assignReminderValues(Notifications notification) {
        // Prevent keyboard from opening automatically
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Log.d(TAG, "id is = " + id);

        //    Notifications reminder = reminderLive.getValue();

        timesShown = notification.getNumberShown();
        repeatType = notification.getRepeatType();
        interval = notification.getNotificationInterval();
        icon = notification.getNotificationIcon();
        colour = notification.getNotificationColor();

        calendar = DateAndTimeUtil.parseDateAndTime(notification.getNotificationDT() + "");

        showText.setText(getString(R.string.times_shown_edit, notification.getNumberShown()));
        titleEditText.setText(notification.getNotificationTitle());
        contentEditText.setText(notification.getNotificationContent());
        dateText.setText(DateAndTimeUtil.toStringReadableDate(calendar));
        timeText.setText(DateAndTimeUtil.toStringReadableTime(calendar, this));
        timesEditText.setText(String.valueOf(notification.getNumberToShow()));

        timesText.setVisibility(View.VISIBLE);

        if (notification.getRepeatType() != ReminderConstants.DOES_NOT_REPEAT) {
            if (notification.getNotificationInterval() > 1) {
                repeatText.setText(TextFormatUtil.formatAdvancedRepeatText(this, repeatType, interval));
            } else {
                repeatText.setText(getResources().getStringArray(R.array.repeat_array)[notification.getRepeatType()]);
            }
            showFrequency(true);
        }

        if (notification.getRepeatType() == ReminderConstants.SPECIFIC_DAYS) {
            daysOfWeek = notification.getDaysOfWeek();

            repeatText.setText(TextFormatUtil.formatDaysOfWeekText(this, daysOfWeek));
        }

        if (Boolean.parseBoolean(notification.getRepeatForever())) {
            foreverSwitch.setChecked(true);
            bottomRow.setVisibility(View.GONE);
        }
    }

    public void showFrequency(boolean show) {
        if (show) {
            foreverRow.setVisibility(View.VISIBLE);
            bottomRow.setVisibility(View.VISIBLE);
            bottomView.setVisibility(View.VISIBLE);
        } else {
            foreverSwitch.setChecked(false);
            foreverRow.setVisibility(View.GONE);
            bottomRow.setVisibility(View.GONE);
            bottomView.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.time_row)
    public void timePicker() {
        TimePickerDialog TimePicker = new TimePickerDialog(CreateEditActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                timeText.setText(DateAndTimeUtil.toStringReadableTime(calendar, getApplicationContext()));
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), DateFormat.is24HourFormat(this));
        TimePicker.show();
    }

    @OnClick(R.id.date_row)
    public void datePicker(View view) {
        DatePickerDialog DatePicker = new DatePickerDialog(CreateEditActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker DatePicker, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                dateText.setText(DateAndTimeUtil.toStringReadableDate(calendar));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        DatePicker.show();
    }


    @OnClick(R.id.repeat_row)
    public void repeatSelector() {
        DialogFragment dialog = new RepeatSelector();
        dialog.show(getSupportFragmentManager(), "RepeatSelector");
    }


    @Override
    public void onDaysOfWeekSelected(boolean[] days) {
        repeatText.setText(TextFormatUtil.formatDaysOfWeekText(this, days));
        daysOfWeek = days;
        repeatType = ReminderConstants.SPECIFIC_DAYS;
        showFrequency(true);
    }

    @Override
    public void onAdvancedRepeatSelection(int type, int interval, String repeatText) {
        repeatType = type;
        this.interval = interval;
        this.repeatText.setText(repeatText);
        showFrequency(true);
    }

    @Override
    public void onRepeatSelection(DialogFragment dialog, int which, String repeatText) {
        interval = 1;
        repeatType = which;
        this.repeatText.setText(repeatText);
        if (which == ReminderConstants.DOES_NOT_REPEAT) {
            showFrequency(false);
        } else {
            showFrequency(true);
        }
    }

    public void saveNotification() {

        final Notifications reminder = new Notifications();
        reminder.setNotificationID(id);
        reminder.setNotificationTitle(titleEditText.getText().toString());
        reminder.setNotificationContent(contentEditText.getText().toString());

        /*if (repeatType == ReminderConstants.SPECIFIC_DAYS) {
            Calendar weekCalendar = (Calendar) calendar.clone();
            weekCalendar.add(Calendar.DATE, 1);

            for (int i = 0; i < 7; i++) {
                int position = (i + (weekCalendar.get(Calendar.DAY_OF_WEEK) - 1)) % 7;
                if (daysOfWeek[position]) {
                    calendar.add(Calendar.DATE, i + 1);
                    break;
                }
            }
        }
        */
        long id = DateAndTimeUtil.toLongDateAndTime(calendar);
        reminder.setNotificationDT(id);

        reminder.setRepeatType(repeatType);
        reminder.setRepeatForever(Boolean.toString(foreverSwitch.isChecked()));
        reminder.setNumberToShow(timesToShow);
        reminder.setNumberShown(timesShown);
        reminder.setNotificationIcon(icon);
        reminder.setNotificationColor(colour);
        reminder.setNotificationInterval(interval);

        AppExecutors.getInstance().diskIO().execute(new Runnable() {


            @Override
            public void run() {

                if (saveType == ReminderConstants.INSERT) {
                    database.notificationsDAO().insertNotification(reminder);
                } else {
                    database.notificationsDAO().updateNotification(reminder);
                }
            }
        });


        if (repeatType == ReminderConstants.SPECIFIC_DAYS) {
            reminder.setDaysOfWeek(daysOfWeek);

            final Daysofweek dow = new Daysofweek();
            dow.setColId(reminder.getNotificationID());

            for (int i = 0; i <= daysOfWeek.length; i++) {
                switch (i) {
                    case 0:
                        dow.setColSunday(daysOfWeek[i] + "");
                        break;
                    case 1:
                        dow.setColMonday(daysOfWeek[i] + "");
                        break;
                    case 2:
                        dow.setColTuesday(daysOfWeek[i] + "");
                        break;
                    case 3:
                        dow.setColWednesday(daysOfWeek[i] + "");
                        break;
                    case 4:
                        dow.setColThursday(daysOfWeek[i] + "");
                        break;
                    case 5:
                        dow.setColFriday(daysOfWeek[i] + "");
                        break;
                    case 6:
                        dow.setColSaturday(daysOfWeek[i] + "");
                        break;


                }

            }

            AppExecutors.getInstance().diskIO().execute(new Runnable() {


                @Override
                public void run() {


                    if (saveType == ReminderConstants.INSERT) {
                        database.daysofweekDAO().addDaysofweek(dow);
                    } else {
                        database.daysofweekDAO().updateDaysofweek(dow);
                    }

                }
            });


        }
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        calendar.set(Calendar.SECOND, 0);
        AlarmUtil.setAlarm(this, alarmIntent, reminder.getNotificationID(), calendar);
        finish();
    }

    @OnClick(R.id.forever_row)
    public void toggleSwitch() {
        foreverSwitch.toggle();
        if (foreverSwitch.isChecked()) {
            bottomRow.setVisibility(View.GONE);
        } else {
            bottomRow.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.switch_toggle)
    public void switchClicked() {
        if (foreverSwitch.isChecked()) {
            bottomRow.setVisibility(View.GONE);
        } else {
            bottomRow.setVisibility(View.VISIBLE);
        }
    }

    public void validateInput() {
        imageWarningShow.setVisibility(View.GONE);
        imageWarningTime.setVisibility(View.GONE);
        imageWarningDate.setVisibility(View.GONE);
        Calendar nowCalendar = Calendar.getInstance();

        if (timeText.getText().equals(getString(R.string.time_now))) {
            calendar.set(Calendar.HOUR_OF_DAY, nowCalendar.get(Calendar.HOUR_OF_DAY));
            calendar.set(Calendar.MINUTE, nowCalendar.get(Calendar.MINUTE));
        }

        if (dateText.getText().equals(getString(R.string.date_today))) {
            calendar.set(Calendar.YEAR, nowCalendar.get(Calendar.YEAR));
            calendar.set(Calendar.MONTH, nowCalendar.get(Calendar.MONTH));
            calendar.set(Calendar.DAY_OF_MONTH, nowCalendar.get(Calendar.DAY_OF_MONTH));
        }

        // Check if the number of times to show notification is empty
        if (timesEditText.getText().toString().isEmpty()) {
            timesEditText.setText("1");
        }

        timesToShow = Integer.parseInt(timesEditText.getText().toString());
        if (repeatType == ReminderConstants.DOES_NOT_REPEAT) {
            timesToShow = timesShown + 1;
        }

        // Check if selected date is before today's date
        if (DateAndTimeUtil.toLongDateAndTime(calendar) < DateAndTimeUtil.toLongDateAndTime(nowCalendar)) {
            Snackbar.make(coordinatorLayout, R.string.toast_past_date, Snackbar.LENGTH_SHORT).show();
            imageWarningTime.setVisibility(View.VISIBLE);
            imageWarningDate.setVisibility(View.VISIBLE);

            // Check if title is empty
        } else if (titleEditText.getText().toString().trim().isEmpty()) {
            Snackbar.make(coordinatorLayout, R.string.toast_title_empty, Snackbar.LENGTH_SHORT).show();
            AnimationUtil.shakeView(titleEditText, this);

            // Check if times to show notification is too low
        } else if (timesToShow <= timesShown && !foreverSwitch.isChecked()) {
            Snackbar.make(coordinatorLayout, R.string.toast_higher_number, Snackbar.LENGTH_SHORT).show();
            imageWarningShow.setVisibility(View.VISIBLE);
        } else {
            saveNotification();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_save:
                validateInput();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
