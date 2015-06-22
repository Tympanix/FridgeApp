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

import java.util.Calendar;
import java.util.Random;


public class MainActivity extends ListActivity {

    private final static int ADD_PRODUCT = 1;
    private AlarmManager mAlarmManager;
    private Random r = new Random();

    Button button1;
    Button button2;
    Button button3;
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
        adaptercr = app.getDBCursor();
        context = getApplicationContext();

        //adapter = new ListViewAdapter(getApplicationContext());
        button1 = (Button) findViewById(R.id.click_button);
        button2 = (Button) findViewById(R.id.click_button2);
        button3 = (Button) findViewById(R.id.click_button3);
//        setListAdapter(adapter);
        setListAdapter(adaptercr);

        button1.setOnClickListener(new View.OnClickListener() {
            int counter = 0;

            @Override
            public void onClick(View v) {
                button1.setText("Clicked");

                counter++;
                dbhelp.insertTestToDB("Apple", 5, false);
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

                // Create an Intent to broadcast to the AlarmNotificationReceiver
                Intent alarmIntent = new Intent(MainActivity.this,
                        FoodExpireBroadcastReceiver.class);

                alarmIntent.setAction(FridgeApp.ACTION_NOTIFICATIONS);

                sendBroadcast(alarmIntent);

            }
        });

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        adaptercr.update();
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


        dbhelp.insertItemToDb(name, expiresafter, cal.getTimeInMillis());
        adaptercr.changeCursor(update());
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
        return dbhelp.getCompactListFromDb();
        //return dbhelp.getWritableDatabase().rawQuery("SELECT  * FROM " + ItemDatabaseHelper.TABLE_NAME, null);
    }
}
