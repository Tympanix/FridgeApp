package gruppe3.dtu02128.fridgeapp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;


public class AddProductActivity extends Activity implements DatePickerDialog.OnDateSetListener {

    private TextView mDateDisplay;
    private Button mPickDate;
    private Button mAddButton;
    private EditText mItemName;
    private EditText mItemCategory;
    private EditText mItemExpiresAfter;
    private EditText mItemNumber;

    private Date expiresDate;
    private int mYear;
    private int mMonth;
    private int mDay;

    static final int DATE_DIALOG_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        // Capture UI elements
        mDateDisplay = (TextView) findViewById(R.id.dateDisplay);
        mPickDate = (Button) findViewById(R.id.pickDate);
        mAddButton = (Button) findViewById(R.id.add_item_add_button);
        mItemName = (EditText) findViewById(R.id.item_name);
        mItemCategory = (EditText) findViewById(R.id.item_category);
        mItemExpiresAfter = (EditText) findViewById(R.id.expiration_after_opened);
        mItemNumber = (EditText) findViewById(R.id.item_number);


        // Set an OnClickListener for the Change the Date Button
        mPickDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // Create a new DatePickerFragment
                DialogFragment newFragment = new DatePickerFragment();

                // Display DatePickerFragment
                newFragment.show(getFragmentManager(), "DatePicker");
            }
        });

        // Add OnClickListener for add item button
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mItemName.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "You must apply a name", Toast.LENGTH_SHORT);
                }

            }
        });
    }

    public void onDateSet(DatePicker view, int year, int monthOfYear,
                          int dayOfMonth) {
        expiresDate = new Date(year, monthOfYear, dayOfMonth);
        mYear = year;
        mMonth = monthOfYear;
        mDay = dayOfMonth;
        updateDisplay();
    }

    // Update the date String in the TextView
    private void updateDisplay() {
        mDateDisplay.setText(new StringBuilder()
                // Month is 0 based so add 1
                .append(mMonth + 1).append("-").append(mDay).append("-")
                .append(mYear).append(" "));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_product, menu);
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



    public static class DatePickerFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            // Set the current date in the DatePickerFragment
            final Calendar c = Calendar.getInstance();

            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);

        }

        // Callback to DatePickerActivity.onDateSet() to update the UI
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {

            ((DatePickerDialog.OnDateSetListener) getActivity()).onDateSet(view, year,
                    monthOfYear, dayOfMonth);


        }
    }
}
