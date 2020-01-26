package com.beantownloner.taskreminder.models;

public class ReminderConstants {

    // ReminderConstants types
    public static final int ACTIVE = 1;
    public static final int INACTIVE = 2;

    // Repetition types
    public static final int DOES_NOT_REPEAT = 0;
    public static final int HOURLY = 1;
    public static final int DAILY = 2;
    public static final int WEEKLY = 3;
    public static final int MONTHLY = 4;
    public static final int YEARLY = 5;
    public static final int SPECIFIC_DAYS = 6;
    public static final int ADVANCED = 7;

    public static final int INSERT = 8;
    public static final int UPDATE = 9;

    private int id;
    private String title;
    private String content;
    private String dateAndTime;
    private int repeatType;
    private String foreverState;
    private int numberToShow;
    private int numberShown;
    private String icon;
    private String colour;
    private boolean[] daysOfWeek;
    private int interval;

    public int getId() {
        return id;
    }

    public ReminderConstants setId(int id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public ReminderConstants setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getContent() {
        return content;
    }

    public ReminderConstants setContent(String content) {
        this.content = content;
        return this;
    }

    public String getDateAndTime() {
        return dateAndTime;
    }

    public ReminderConstants setDateAndTime(String dateAndTime) {
        this.dateAndTime = dateAndTime;
        return this;
    }

    public String getDate() {
        return dateAndTime.substring(0, 8);
    }

    public int getRepeatType() {
        return repeatType;
    }

    public ReminderConstants setRepeatType(int repeatType) {
        this.repeatType = repeatType;
        return this;
    }

    public String getForeverState() {
        return foreverState;
    }

    public ReminderConstants setForeverState(String foreverState) {
        this.foreverState = foreverState;
        return this;
    }

    public int getNumberToShow() {
        return numberToShow;
    }

    public ReminderConstants setNumberToShow(int numberToShow) {
        this.numberToShow = numberToShow;
        return this;
    }

    public int getNumberShown() {
        return numberShown;
    }

    public ReminderConstants setNumberShown(int numberShown) {
        this.numberShown = numberShown;
        return this;
    }

    public String getIcon() {
        return icon;
    }

    public ReminderConstants setIcon(String icon) {
        this.icon = icon;
        return this;
    }

    public String getColour() {
        return colour;
    }

    public ReminderConstants setColour(String colour) {
        this.colour = colour;
        return this;
    }

    public boolean[] getDaysOfWeek() {
        return daysOfWeek;
    }

    public ReminderConstants setDaysOfWeek(boolean[] daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
        return this;
    }

    public int getInterval() {
        return interval;
    }

    public ReminderConstants setInterval(int interval) {
        this.interval = interval;
        return this;
    }
}