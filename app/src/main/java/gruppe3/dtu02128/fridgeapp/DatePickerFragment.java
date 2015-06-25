package gruppe3.dtu02128.fridgeapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Created by Sebastian on 23-06-2015.
 */
public class DatePickerFragment extends DialogFragment implements
        DatePickerDialog.OnDateSetListener {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Set the current date in the DatePickerFragment
        DatePickerDialog datePickerDialog;
        // Create a new instance of DatePickerDialog and return it
        if (this.getArguments().getBoolean("CHANGEDATE")) {

            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(this.getArguments().getLong("EXPIRATION_DATE"));

            int year = cal.get(cal.YEAR);
            int month = cal.get(cal.MONTH);
            int day = cal.get(cal.DAY_OF_MONTH);

            datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
            datePickerDialog.setTitle("Change date for: " + this.getArguments().getString("ITEM_TITLE"));
        } else {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
        }

        return datePickerDialog;

    }

    // Callback to DatePickerActivity.onDateSet() to update the UI
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear,
                          int dayOfMonth) {
        ((DatePickerDialog.OnDateSetListener) getActivity()).onDateSet(view, year,
                monthOfYear, dayOfMonth);


    }

    public FragmentManager getThisFragmentManager() {
        return this.getFragmentManager();
    }

}