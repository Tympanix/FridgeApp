package gruppe3.dtu02128.fridgeapp;


import android.app.DatePickerDialog.OnDateSetListener;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Button;

import android.widget.TextView;

import java.util.Calendar;

public class ItemViewActivity extends AppCompatActivity implements OnDateSetListener{

    private Intent data;
    private String name;
    private FridgeApp app;

    private TextView title;

    private ItemDatabaseHelper dbhelp;
    private SingleItemCursorAdapter adapter;

    private Button addExtraItem;
    private int mYear;
    private int mMonth;
    private int mDay;

    private int addCounter;
    private Boolean changeDate;
    private Bundle bund;
    private long changedDate;

    private SingleFoodListFragment list;
    private String ID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_view);

        data = getIntent();
        bund = new Bundle();
        name = data.getStringExtra("name");

        // Set the title of the activity to the current item
        setTitle(name);

        app = (FridgeApp) getApplication();
        dbhelp = app.getDBHelper();

        FragmentManager fm = getFragmentManager();
        list = new SingleFoodListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        list.setArguments(bundle);
        fm.beginTransaction().add(R.id.FragmentContainer, list).commit();

        adapter = list.getAdapter();

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

        addCounter++;
        if (addCounter % 2 == 0) {
            return;
        }

        Calendar cal = Calendar.getInstance();
        mYear = year;
        mMonth = monthOfYear;
        mDay = dayOfMonth;
        cal.set(mYear, mMonth, mDay);

        if (changeDate) {
            dbhelp.updateExpirationDate(cal.getTimeInMillis(), ID);
        } else {
            dbhelp.insertItemToDb(name, dbhelp.getExpirationOpenFromRegister(name), cal.getTimeInMillis());
        }

        list.update();


    }

    public void changeDate(String name, long expDate) {
        Bundle bund = new Bundle();
        changeDate = true;
        //Log.i("CHANGE", "Milli:" + expDate);
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

    public void setChangeDate(Boolean bool) {
        changeDate = bool;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void resetChangeDateCounter(){
        addCounter = 0;
    }
}
