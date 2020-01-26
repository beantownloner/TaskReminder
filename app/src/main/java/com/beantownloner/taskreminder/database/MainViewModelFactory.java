package com.beantownloner.taskreminder.database;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class MainViewModelFactory implements ViewModelProvider.Factory {

    private final AppDatabase mDb;
    private final long notifiID;

    public MainViewModelFactory(AppDatabase database, long notificationID) {
        mDb = database;
        notifiID = notificationID;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new MainViewModel(mDb, notifiID);
    }


}
