package com.beantownloner.taskreminder.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beantownloner.taskreminder.R;
import com.beantownloner.taskreminder.TaskreminderWidgetProvider;
import com.beantownloner.taskreminder.adapters.ReminderAdapter;
import com.beantownloner.taskreminder.database.AppDatabase;
import com.beantownloner.taskreminder.database.MainViewModel;
import com.beantownloner.taskreminder.database.MainViewModelFactory;
import com.beantownloner.taskreminder.models.ReminderConstants;
import com.beantownloner.taskreminder.objects.Notifications;
import com.beantownloner.taskreminder.utils.SharedPreferenceUtil;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

//import com.beantownloner.taskreminder.database.DatabaseHelper;

public class TabFragment extends Fragment {
    public static final String TAG = TabFragment.class.getSimpleName();
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.empty_text)
    TextView emptyText;
    @BindView(R.id.empty_view)
    LinearLayout linearLayout;
    @BindView(R.id.empty_icon)
    ImageView imageView;

    private ReminderAdapter reminderAdapter;
    private List<Notifications> renderList;
    private int remindersType;
    private AppDatabase database;
    private MainViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tabs, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        ButterKnife.bind(this, view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        database = AppDatabase.getInstance(getContext());
        recyclerView.setLayoutManager(layoutManager);
        MainViewModelFactory factory = new MainViewModelFactory(database, 0);
        viewModel = new ViewModelProvider(getActivity(), factory).get(MainViewModel.class);

        remindersType = this.getArguments().getInt("TYPE");
        if (remindersType == ReminderConstants.INACTIVE) {
            emptyText.setText(R.string.no_inactive);
            imageView.setImageResource(R.drawable.ic_notifications_off_black_empty);
        }
        reminderAdapter = new ReminderAdapter(getContext());
        retrieveNotifications();


    }

    public void retrieveNotifications() {
        if (remindersType == ReminderConstants.INACTIVE) {
            viewModel.getInactiveNotifications().observe(getActivity(), new Observer<List<Notifications>>() {
                @Override
                public void onChanged(@Nullable List<Notifications> notifications) {
                    renderList = notifications;
                    Log.d(TAG, " length of inactive reminders are  " + notifications.size());
                    reminderAdapter.setReminderList(notifications);
                    recyclerView.setAdapter(reminderAdapter);

                    if (reminderAdapter.getItemCount() == 0) {
                        recyclerView.setVisibility(View.GONE);
                        linearLayout.setVisibility(View.VISIBLE);
                    } else {
                        recyclerView.setVisibility(View.VISIBLE);
                        linearLayout.setVisibility(View.GONE);
                    }

                }
            });

        } else {

            viewModel.getActiveNotifications().observe(getActivity(), new Observer<List<Notifications>>() {
                @Override
                public void onChanged(@Nullable List<Notifications> notifications) {
                    renderList = notifications;
                    Log.d(TAG, " length of nactive reminders are  " + notifications.size());
                    reminderAdapter.setReminderList(notifications);
                    recyclerView.setAdapter(reminderAdapter);

                    if (reminderAdapter.getItemCount() == 0) {
                        recyclerView.setVisibility(View.GONE);
                        linearLayout.setVisibility(View.VISIBLE);
                    } else {
                        recyclerView.setVisibility(View.VISIBLE);
                        linearLayout.setVisibility(View.GONE);
                    }


                    SharedPreferenceUtil.saveNotificationLists(getContext(), notifications);
                    updateWidget();

                }
            });

        }

    }

    public void updateWidget() {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {


                // this will send the broadcast to update the appwidget
                TaskreminderWidgetProvider.sendRefreshBroadcast(getContext());
            }
        });

    }

    public LiveData<List<Notifications>> getListData(int remindersType) {

        LiveData<List<Notifications>> liveRenderList;

        if (remindersType == ReminderConstants.INACTIVE) {
            //reminderList=  database.notificationsDAO().loadInactiveNotifications();
            liveRenderList = viewModel.getInactiveNotifications();
        } else {

            liveRenderList = viewModel.getActiveNotifications();
        }
        return liveRenderList;
    }

    public void updateList() {

        retrieveNotifications();

    }

    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateList();
        }
    };

    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(messageReceiver, new IntentFilter("BROADCAST_REFRESH"));
        updateList();
        super.onResume();
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(messageReceiver);
        super.onPause();
    }
}