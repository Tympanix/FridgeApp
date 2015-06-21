package gruppe3.dtu02128.fridgeapp;

import android.app.AlarmManager;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import org.joda.time.DateTime;
import org.joda.time.TimeOfDay;

import java.util.Calendar;


public class MainActivity extends ListActivity {

    private final static int ADD_PRODUCT = 1;
    private static final long ALARM_DELAY = 5 * 1000L;
    private AlarmManager mAlarmManager;

    Button button1;
    Button button2;
    Button button3;
    ListViewAdapter adapter;
    ItemDatabaseHelper dbhelp;
    MyCursorAdapter adaptercr;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the AlarmManager Service
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        final PendingIntent alarmPendingIntent;

        FridgeApp app = (FridgeApp) getApplication();
        dbhelp = app.getDBHelper();
        //adaptercr = app.getDBCursor();
        adaptercr = new MyCursorAdapter(MainActivity.this, update(), dbhelp);
        context = getApplicationContext();

        //adapter = new ListViewAdapter(getApplicationContext());
        button1 = (Button) findViewById(R.id.click_button);
        button2 = (Button) findViewById(R.id.click_button2);
        button3 = (Button) findViewById(R.id.click_button3);
//        setListAdapter(adapter);
        setListAdapter(adaptercr);

        // Create an Intent to broadcast to the AlarmNotificationReceiver
        Intent alarmIntent = new Intent(MainActivity.this,
                FoodExpireBroadcastReceiver.class);

        // Create an PendingIntent that holds the NotificationReceiverIntent
        alarmPendingIntent = PendingIntent.getBroadcast(
                MainActivity.this, 0, alarmIntent, 0);

        button1.setOnClickListener(new View.OnClickListener() {
            int counter = 0;

            @Override
            public void onClick(View v) {
                button1.setText("Clicked");

                ContentValues cw = new ContentValues();
                cw.put(ItemDatabaseHelper.FOOD_NAME, "Apple" + counter);
                counter++;
                cw.put(ItemDatabaseHelper.EXPIRES_OPEN, 5);
                cw.put(ItemDatabaseHelper.EXPIRE_DATE,1435524000000.);
                cw.put(ItemDatabaseHelper.OPEN, 0);
                cw.put(ItemDatabaseHelper.DATE_ADDED,DateTime.now().getMillis());
                dbhelp.getWritableDatabase().insert(ItemDatabaseHelper.TABLE_NAME, null, cw);
                cw.clear();
                adaptercr.changeCursor(update());
                setListAdapter(adaptercr);
                //adapter.add();
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                dbhelp.getWritableDatabase().delete(ItemDatabaseHelper.TABLE_NAME,null,null);
//                adaptercr.changeCursor(update());
//                setListAdapter(adaptercr);
                Intent inte = new Intent(MainActivity.this,AddProductActivity.class);
                startActivityForResult(inte, ADD_PRODUCT);
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("FRIDGE", "Setting alarm");

                mAlarmManager.set(AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis() + ALARM_DELAY,
                        alarmPendingIntent);


            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode != ADD_PRODUCT) {
            return;
        }
        if(resultCode != RESULT_OK) {
            return;
        }
        String name = data.getStringExtra("name");
        int number = data.getIntExtra("number", 1);
        int expiresafter = data.getIntExtra("expiresafter", 0);
        Calendar cal = Calendar.getInstance();
        int year = data.getIntExtra("expiresyear", cal.get(Calendar.YEAR));
        int month = data.getIntExtra("expiresmonth", cal.get(Calendar.MONTH));
        int day = data.getIntExtra("expiresday", cal.get(Calendar.DAY_OF_MONTH));
        cal.set(year, month, day);


        ContentValues cw = new ContentValues();
        cw.put(ItemDatabaseHelper.FOOD_NAME,name);
        cw.put(ItemDatabaseHelper.EXPIRES_OPEN, expiresafter);
        cw.put(ItemDatabaseHelper.DATE_ADDED,DateTime.now().getMillis());
        cw.put(ItemDatabaseHelper.EXPIRE_DATE,cal.getTimeInMillis());
        //The item has not been opened yet
        cw.put(ItemDatabaseHelper.OPEN,0);

        dbhelp.getWritableDatabase().insert(ItemDatabaseHelper.TABLE_NAME,null,cw);
        adaptercr.changeCursor(update());
        setListAdapter(adaptercr);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public Cursor update() {
        return dbhelp.getWritableDatabase().rawQuery("SELECT  * FROM " + ItemDatabaseHelper.TABLE_NAME, null);
    }
}
