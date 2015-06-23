package gruppe3.dtu02128.fridgeapp;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class SettingsActivity extends Activity implements TimePickerDialog.OnTimeSetListener {

    private TextView timeDisplay;
    private Button pickTime;
    private Button Containers;
    private int hour;
    private int minute;
    private int daysBefore;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        preferences = this.getSharedPreferences(getString(R.string.shared_preference),Context.MODE_PRIVATE);

        this.hour = preferences.getInt(getString(R.string.settings_timeHour), FridgeApp.DEFAULT_ALARM_HOUR);

        this.minute = preferences.getInt(getString(R.string.settings_timeMinute), FridgeApp.DEFAULT_ALARM_MINUTE);

        this.daysBefore = preferences.getInt(getString(R.string.settings_dayBefore), 0);

        timeDisplay = (TextView) findViewById(R.id.time_display);

        updateDisplay();

        pickTime = (Button) findViewById(R.id.pick_time);
            pickTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogFragment timePicker = TimePicker.newInstance(hour,minute);

                    timePicker.show(getFragmentManager(), "TimePicker");

                }
            });
        Containers = (Button) findViewById(R.id.add_container);
            Containers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent containersIntent = new Intent(getApplicationContext(),ContainersActivity.class);
                    startActivity(containersIntent);
                }
            });
        EditText daysBeforeEditor = (EditText) findViewById(R.id.editText);
        daysBeforeEditor.setText("" + daysBefore);
            daysBeforeEditor.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        daysBefore = Integer.parseInt(s.toString());
                        if(daysBefore >= 0) {
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putInt(getString(R.string.settings_dayBefore), daysBefore);
                            editor.commit();
                        }
                    }catch (Exception e){

                    }

                }
            });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTimeSet(android.widget.TimePicker view, int hourOfDay, int minute) {
        this.hour = hourOfDay;
        this.minute = minute;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(getString(R.string.settings_timeHour), this.hour);
        editor.putInt(getString(R.string.settings_timeMinute), this.minute);
        editor.commit();
        updateDisplay();
    }
    private void updateDisplay(){
        StringBuilder s = new StringBuilder().append(pad(this.hour)).append(":").append(pad(minute));
        timeDisplay.setText(s);
    }

    private String pad(int a){
        if(a >= 10) {
            return String.valueOf(a);
        }else{
            return "0" + String.valueOf(a);
        }
    }
}
