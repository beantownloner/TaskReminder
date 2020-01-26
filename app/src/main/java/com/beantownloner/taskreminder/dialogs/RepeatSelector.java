package com.beantownloner.taskreminder.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.beantownloner.taskreminder.R;
import com.beantownloner.taskreminder.models.ReminderConstants;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;


public class RepeatSelector extends DialogFragment {

    public interface RepeatSelectionListener {
        void onRepeatSelection(DialogFragment dialog, int interval, String repeatText);
    }

    RepeatSelectionListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (RepeatSelectionListener) context;
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String[] repeatArray = getResources().getStringArray(R.array.repeat_array);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.Dialog);
        builder.setItems(repeatArray, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (which == ReminderConstants.SPECIFIC_DAYS) {
                    DialogFragment daysOfWeekDialog = new DaysOfWeekSelector();
                    daysOfWeekDialog.show(getActivity().getSupportFragmentManager(), "DaysOfWeekSelector");
                }  else if (which == ReminderConstants.ADVANCED) {
                    DialogFragment advancedDialog = new AdvancedRepeatSelector();
                    advancedDialog.show(getActivity().getSupportFragmentManager(), "AdvancedSelector");
                } else {
                    listener.onRepeatSelection(RepeatSelector.this, which, repeatArray[which]);
                }
            }
        });
        return builder.create();
    }
}