package gruppe3.dtu02128.fridgeapp;


import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Button;

import android.widget.TextView;
import android.app.DatePickerDialog;

import java.util.Calendar;

public class ItemViewActivity extends ListActivity implements OnDateSetListener{

    private Intent data;
    private String name;
    private FridgeApp app;

    private TextView title;

    private ItemDatabaseHelper dbhelp;
    private SingleItemCursorAdapter adaptercr;

    private Button addExtraItem;
    private int mYear;
    private int mMonth;
    private int mDay;

    private int addCounter;
    private Boolean changeDate;
    private Bundle bund;
    private long changedDate;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_view);

        data = getIntent();
        bund = new Bundle();
        name = data.getStringExtra("name");

        app = (FridgeApp) getApplication();
        dbhelp = app.getDBHelper();
        adaptercr = app.getAdapterDetail(this, name);


        // Set the title of the product
        title = (TextView) findViewById(R.id.item_title);
        title.setText(name);
        setListAdapter(adaptercr);

        addExtraItem = (Button) findViewById(R.id.add_item_add_button);
        addExtraItem.setText("Add extra " + name);

        addExtraItem.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                addCounter = 0;
                changeDate = false;
                bund.putBoolean("CHANGEDATE", changeDate);
                // Create a new DatePickerFragment
                DialogFragment newFragment = new DatePickerFragment();

                newFragment.setArguments(bund);
                // Display DatePickerFragment
                newFragment.show(getFragmentManager(), "DatePicker");

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_item_view, menu);
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

    public String getItemName(){
        return name;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

        if(changeDate) {
            Calendar cal = Calendar.getInstance();
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            cal.set(mYear, mMonth, mDay);
            setChangedDate(cal);
            adaptercr.updateDate();
            adaptercr.update();
        } else {
            addCounter++;
            if (addCounter % 2 == 0) {
                Calendar cal = Calendar.getInstance();
                mYear = year;
                mMonth = monthOfYear;
                mDay = dayOfMonth;
                cal.set(mYear, mMonth, mDay);
                Log.i("DATE_PICKER", "OnDateSet2");
                dbhelp.insertItemToDb(name, dbhelp.getExpirationOpenFromRegister(name), cal.getTimeInMillis());
                adaptercr.update();
            }
        }

    }

    public void changeDate(String name, long expDate) {
        Bundle bund = new Bundle();
        changeDate = true;
        Log.i("CHANGE", "Mili:" + expDate);
        bund.putString("ITEM_TITLE", name);
        bund.putBoolean("CHANGEDATE", changeDate);
        bund.putLong("EXPIRATION_DATE", expDate);

        DialogFragment newFragment = new DatePickerFragment();
        newFragment.setArguments(bund);
        // Display DatePickerFragment
        newFragment.show(getFragmentManager(), "DatePicker");

    }

    public void setChangedDate(Calendar cal) {
        changedDate = cal.getTimeInMillis();
    }

    public long getChangedDate() {
        return changedDate;
    }

    public static class DatePickerFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Log.i("DATE_PICKER", "DatePickerFrag");
            // Set the current date in the DatePickerFragment
            DatePickerDialog datePickerDialog;
            // Create a new instance of DatePickerDialog and return it
            if (this.getArguments().getBoolean("CHANGEDATE")) {

                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(this.getArguments().getLong("EXPIRATION_DATE"));
                Log.i("CHANGE", "Mili2:" + this.getArguments().getLong("EXPIRATION_DATE"));
                int year = cal.get(cal.YEAR);
                int month = cal.get(cal.MONTH);
                int day = cal.get(cal.DAY_OF_MONTH);
                Log.i("CHANGE", "Year:" + year + " month: " + month + " day: " + day );
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
            Log.i("DATE_PICKER", "OnDateSet1");
            ((DatePickerDialog.OnDateSetListener) getActivity()).onDateSet(view, year,
                    monthOfYear, dayOfMonth);


        }

    }
}
