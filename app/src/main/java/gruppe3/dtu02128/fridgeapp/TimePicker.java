package gruppe3.dtu02128.fridgeapp;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;

import java.util.Calendar;

/**
 * Created by Marcus on 18-06-2015.
 */
public class TimePicker extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    final static String hourKey = "hour";
    final static String minuteKey= "minute";

    static TimePicker newInstance(int hour, int minute){
        TimePicker t = new TimePicker();

        Bundle args = new Bundle();

        args.putInt("hour", hour);
        args.putInt("minute", minute);
        t.setArguments(args);
        return t;

    }

    public Dialog onCreateDialog(Bundle savedInstenceState){
        super.onCreateDialog(savedInstenceState);
        int hour = getArguments().getInt(hourKey);
        int minute = getArguments().getInt(minuteKey);

        final Calendar calendar = Calendar.getInstance();
        //int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        //int minute = calendar.get(Calendar.MINUTE);
        //return new TimePickerDialog(getActivity(), this, hourOfDay,minute,true);
        return new TimePickerDialog(getActivity(), this, hour,minute,true);

    }


    @Override
    public void onTimeSet(android.widget.TimePicker view, int hourOfDay, int minute) {
        ((TimePickerDialog.OnTimeSetListener) getActivity()).onTimeSet(view, hourOfDay, minute);
    }



}
