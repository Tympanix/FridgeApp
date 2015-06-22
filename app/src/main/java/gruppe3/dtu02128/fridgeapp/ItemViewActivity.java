package gruppe3.dtu02128.fridgeapp;

import android.app.Activity;
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
import android.widget.EditText;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_view);

        data = getIntent();
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
                // Create a new DatePickerFragment
                DialogFragment newFragment = new DatePickerFragment();

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

    private void updateList() {

        adaptercr = app.getAdapterDetail(this, name);
        setListAdapter(adaptercr);



    }


    public static class DatePickerFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Log.i("DATE_PICKER", "DatePickerFrag");
            // Set the current date in the DatePickerFragment
            final Calendar c = Calendar.getInstance();

            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
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
