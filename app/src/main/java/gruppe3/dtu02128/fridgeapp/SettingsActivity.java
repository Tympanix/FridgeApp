package gruppe3.dtu02128.fridgeapp;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.app.DialogFragment;


public class SettingsActivity extends Activity implements TimePickerDialog.OnTimeSetListener {

    private TextView timeDisplay;
    private Button pickTime;
    private int hour;
    private int minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        timeDisplay = (TextView) findViewById(R.id.time_display);
        pickTime = (Button) findViewById(R.id.pick_time);
            pickTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogFragment timePicker = new TimePicker();

                    timePicker.show(getFragmentManager(), "TimePicker");
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
        updateDisplay();
    }
    private void updateDisplay(){
        timeDisplay.setText(new StringBuilder().append(pad(this.hour)).append(":").append(pad(minute)));
    }
    private String pad(int a){
        if(a >= 10) {
            return String.valueOf(a);
        }else{
            return "0" + String.valueOf(a);
        }
    }
}
