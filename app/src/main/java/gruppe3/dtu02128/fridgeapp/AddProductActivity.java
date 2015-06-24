package gruppe3.dtu02128.fridgeapp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class AddProductActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private FridgeApp app;
    private boolean newScan;
    private Activity thisactivity = this;
    private TextView dateEditText;
    private Button mPickDate;
    private Button mAddButton;
    private Button mScanButton;
    private AutoCompleteTextView mItemName;
    private EditText mItemExpiresAfter;
    private EditText mItemNumber;
    private ArrayAdapter<String> names;

    private int openexpires;
    private String productName = "";

    private String barcode;
    private int mYear;
    private int mMonth;
    private int mDay;

    private ItemDatabaseHelper dbhelp;
    private MyCursorAdapter adaptercr;
    private String dateString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        app = (FridgeApp) getApplication();
        dbhelp = app.getDBHelper();
        adaptercr = app.getDBCursor();

        // Capture UI elements
        dateEditText = (TextView) findViewById(R.id.dateDisplay);
        mPickDate = (Button) findViewById(R.id.pickDate);
        mAddButton = (Button) findViewById(R.id.add_item_add_button);
        mScanButton = (Button) findViewById(R.id.scan_button);
        mItemName = (AutoCompleteTextView) findViewById(R.id.item_name);
        mItemExpiresAfter = (EditText) findViewById(R.id.expiration_after_opened);
        mItemNumber = (EditText) findViewById(R.id.item_number);

        // Set an OnClickListener fro the scan button
        mScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.scan_button) {
                    if (barcode != null) {
                        barcode = null;
                        mScanButton.setText(R.string.scan_button);
                        mItemName.setText(null);
                        mItemName.setEnabled(true);
                        mItemExpiresAfter.setText(null);
                        mItemExpiresAfter.setEnabled(true);
                        return;
                    }
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

        //Set AutoEditText
        String[] productNames = dbhelp.getProductNames();
        names = new ArrayAdapter<String>(this,R.layout.text_complete,productNames);
        Log.i("test", String.valueOf(dbhelp.getProductNames().length));
        mItemName.setThreshold(1);
        mItemName.setAdapter(names);

        mItemName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemName = names.getItem(position);

                Log.i("test", "Selected " + itemName);
            }
        });


        //If the TextField does not contain a valid object, unlock the field
        mItemName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i("test", String.valueOf(!isName(s.toString())));
                if(isName(s.toString())) {
                    setDisplay(s.toString());
                } else {
                    resetDisplay();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Add OnClickListener for add item button
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String tempName = mItemName.getText().toString();

                if (tempName.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "You must apply a name", Toast.LENGTH_SHORT).show();
                    return;
                }
                String itemName = tempName.substring(0,1).toUpperCase() + tempName.substring(1);

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

                    try {
                        dbhelp.getWritableDatabase().insertOrThrow(ItemDatabaseHelper.REGISTER_TABLE_NAME, null, cw);
                    } catch (SQLiteConstraintException e) {
                        dbhelp.getWritableDatabase().update(dbhelp.REGISTER_TABLE_NAME,cw,dbhelp.REGISTER_COLUMN_NAME +"=?",new String[]{itemName});
                    }
                }

                setResult(RESULT_OK, intent);
                finish();

            }
        });
    }

    protected boolean isName(String s) {
        for(int i = 0; i < names.getCount(); i++) {
            if(s.toLowerCase().equals(names.getItem(i).toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    protected void setDisplay(String itemName) {
        Cursor curs = dbhelp.getRegisterByName(itemName);
        curs.moveToFirst();
        openexpires = curs.getInt(curs.getColumnIndexOrThrow(dbhelp.REGISTER_COLUMN_EXPIRES_OPEN));
        productName = curs.getString(curs.getColumnIndexOrThrow(dbhelp.REGISTER_COLUMN_NAME));
        mItemExpiresAfter.setText(String.valueOf(openexpires));
        mItemExpiresAfter.setEnabled(false);
        barcode = curs.getString(curs.getColumnIndexOrThrow(dbhelp.REGISTER_COLUMN_ID));
        String message;
        if(barcode == null) {
            message = "Add barcode";
        } else {
            message = "Remove barcode";
        }
        mScanButton.setText(message);
    }

    protected void resetDisplay() {
        mItemExpiresAfter.setText("");
        mItemExpiresAfter.setEnabled(true);
        if (barcode == null) {
            mScanButton.setText("Scan");
        } else {
            mScanButton.setText("Remove scan");
        }
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

        Log.i("ADDITEM", "Item found - id: " + barcode);

        newScan = false;
        cursor.moveToFirst();
        String name = cursor.getString(cursor.getColumnIndexOrThrow(dbhelp.REGISTER_COLUMN_NAME));
        Log.i("ADDITEM", "Found name: " + name);
        int openexpires = cursor.getInt(cursor.getColumnIndexOrThrow(dbhelp.REGISTER_COLUMN_EXPIRES_OPEN));
        Log.i("ADDITEM", "Found expire: " + openexpires);
        String code = cursor.getString(cursor.getColumnIndexOrThrow(dbhelp.REGISTER_COLUMN_ID));
        Log.i("ADDITEM", "Found id: " + code);

        mItemName.setText(name);
        mItemName.setEnabled(false);
        //Remove autoComplete if scan receives a name
        mItemName.setAdapter(null);
        mItemExpiresAfter.setText(String.valueOf(openexpires));
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

        Calendar cal = new GregorianCalendar(mYear, mMonth, mDay);
        SimpleDateFormat format = new SimpleDateFormat("dd MMMM yyyy");
        Date date = new Date(cal.getTimeInMillis());
        dateString = format.format(date);
        dateEditText.setText(dateString);

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

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            ((DatePickerDialog.OnDateSetListener) getActivity()).onDateSet(view, year,
                    monthOfYear, dayOfMonth);


        }
    }

}
