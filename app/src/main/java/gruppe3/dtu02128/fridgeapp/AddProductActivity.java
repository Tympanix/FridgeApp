package gruppe3.dtu02128.fridgeapp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Calendar;
import java.util.GregorianCalendar;


public class AddProductActivity extends Activity implements DatePickerDialog.OnDateSetListener {

    private FridgeApp app;
    private boolean newScan;
    private Activity thisactivity = this;
    private TextView mDateDisplay;
    private Button mPickDate;
    private Button mAddButton;
    private Button mScanButton;
    private EditText mItemName;
    private EditText mItemExpiresAfter;
    private EditText mItemNumber;

    private String barcode;
    private int mYear;
    private int mMonth;
    private int mDay;

    private ItemDatabaseHelper dbhelp;
    private MyCursorAdapter adaptercr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        app = (FridgeApp) getApplication();
        dbhelp = app.getDBHelper();
        adaptercr = app.getDBCursor();

        // Capture UI elements
        mDateDisplay = (TextView) findViewById(R.id.dateDisplay);
        mPickDate = (Button) findViewById(R.id.pickDate);
        mAddButton = (Button) findViewById(R.id.add_item_add_button);
        mScanButton = (Button) findViewById(R.id.scan_button);
        mItemName = (EditText) findViewById(R.id.item_name);
        mItemExpiresAfter = (EditText) findViewById(R.id.expiration_after_opened);
        mItemNumber = (EditText) findViewById(R.id.item_number);

        // Set an OnClickListener fro the scan button
        mScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.scan_button) {
                    IntentIntegrator scanIntegrator = new IntentIntegrator(thisactivity);
                    scanIntegrator.initiateScan();
                }
            }
        });

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

                String itemName = mItemName.getText().toString();
                if (itemName.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "You must apply a name", Toast.LENGTH_SHORT).show();
                    return;
                }

                int itemNumber;
                try {
                    itemNumber = Integer.parseInt(mItemNumber.getText().toString());
                } catch (Exception exception) {
                    Toast.makeText(getApplicationContext(), "You must apply item number", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (itemNumber <= 0) {
                    Toast.makeText(getApplicationContext(), "You must apply item number", Toast.LENGTH_SHORT).show();
                    return;
                }

                int expiresAfter;
                try {
                    expiresAfter = Integer.parseInt(mItemExpiresAfter.getText().toString());
                } catch (Exception exception) {
                    Toast.makeText(getApplicationContext(), "You must apply expiration information", Toast.LENGTH_SHORT).show();
                    return;
                }

                Calendar cal = new GregorianCalendar(mYear, mMonth, mDay + 1);
                if (cal.before(Calendar.getInstance())) {
                    Toast.makeText(getApplicationContext(), "A valid date must be chosen", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent();
                intent.putExtra("name", itemName);
                intent.putExtra("number", itemNumber);
                intent.putExtra("expiresafter", expiresAfter);
                intent.putExtra("expiresyear", mYear);
                intent.putExtra("expiresmonth", mMonth);
                intent.putExtra("expiresday", mDay);
                intent.putExtra("barcode", barcode);

                if (newScan) {
                    Log.i("ADDITEM", "Created new entry i register - barcode: " + barcode + " name: " + itemName);
                    ContentValues cw = new ContentValues();
                    cw.put(ItemDatabaseHelper.REGISTER_COLUMN_ID, barcode);
                    cw.put(ItemDatabaseHelper.REGISTER_COLUMN_NAME, itemName);
                    cw.put(ItemDatabaseHelper.REGISTER_COLUMN_EXPIRES_OPEN, expiresAfter);

                    dbhelp.getWritableDatabase().insert(ItemDatabaseHelper.REGISTER_TABLE_NAME, null, cw);
                }

                setResult(RESULT_OK, intent);
                finish();

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

        if (resultCode != RESULT_OK){
            return;
        }

        if (scanningResult == null) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }

        String scanContent = scanningResult.getContents();
        String scanFormat = scanningResult.getFormatName();

        mScanButton.setText("Remove Scan");
        barcode = scanContent;
        Cursor cursor = app.getFromRegister(scanContent);
        Cursor cursor1 = app.getFromRegister();

        Log.i("ADDITEM", "Request from register - id: " + barcode + " count: " + cursor.getCount() + " of total: " + cursor1.getCount());

        if (cursor.getCount() <= 0){
            Toast toast = Toast.makeText(getApplicationContext(),
                    "New registry for: " + scanContent, Toast.LENGTH_SHORT);
            toast.show();
            newScan = true;
            return;
        }

        newScan = false;
        cursor.moveToFirst();
        String name = cursor.getString(cursor.getColumnIndexOrThrow(dbhelp.REGISTER_TABLE_NAME));
        int openexpires = cursor.getInt(cursor.getColumnIndexOrThrow(dbhelp.REGISTER_COLUMN_EXPIRES_OPEN));

        mItemName.setText(name);
        mItemName.setEnabled(false);

        mItemExpiresAfter.setText(openexpires);
        mItemExpiresAfter.setEnabled(false);

        Toast toast = Toast.makeText(getApplicationContext(),
                "Received: " + scanContent, Toast.LENGTH_SHORT);
        toast.show();


    }

    public void onDateSet(DatePicker view, int year, int monthOfYear,
                          int dayOfMonth) {
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
            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
            return datePickerDialog;

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
