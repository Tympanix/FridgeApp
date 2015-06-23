package gruppe3.dtu02128.fridgeapp;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;

import java.util.Calendar;
import java.util.Random;


public class MainActivity extends AppCompatActivity {

    private final static int ADD_PRODUCT = 1;
    private AlarmManager mAlarmManager;
    private Random r = new Random();

    Button button1;
    Button button2;
    Button button3;
    ItemDatabaseHelper dbhelp;
    MyCursorAdapter adaptercr;

    FridgeApp app;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the AlarmManager Service
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        final PendingIntent alarmPendingIntent;

        app = (FridgeApp) getApplication();
        dbhelp = app.getDBHelper();
        adaptercr = app.getDBCursor();
        context = getApplicationContext();


        /*
        FragmentManager fm = getFragmentManager();
        FoodListFragment list = new FoodListFragment();
        fm.beginTransaction().add(android.R.id.content, list).commit();
        */

        //adapter = new ListViewAdapter(getApplicationContext());

        button2 = (Button) findViewById(R.id.click_button2);
        button3 = (Button) findViewById(R.id.click_button3);
//        setListAdapter(adapter);
        //setListAdapter(adaptercr);



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

                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                /*
                // Create an Intent to broadcast to the AlarmNotificationReceiver
                Intent alarmIntent = new Intent(MainActivity.this,
                        FoodExpireBroadcastReceiver.class);

                alarmIntent.setAction(FridgeApp.ACTION_NOTIFICATIONS);

                sendBroadcast(alarmIntent);
                */

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

        // Creating and Building the Dialog

        final AlertDialog levelDialog;
        final CharSequence[] items = {"Easy", "Medium", "Hard", "Very Hard", "Very Hard"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose refrigerator");
        final Cursor dialogCursor = dbhelp.getContainerListFromDB();
        builder.setSingleChoiceItems(dialogCursor, -1, dbhelp.CONTAINER_COLUMN_NAME, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialogCursor.moveToPosition(which);
                int id = dialogCursor.getInt(dialogCursor.getColumnIndexOrThrow(dbhelp.CONTAINER_COLUMN_ID));
                String name = dialogCursor.getString(dialogCursor.getColumnIndexOrThrow(dbhelp.CONTAINER_COLUMN_NAME));
                app.setSelectedFridge(id);
                adaptercr.changeCursor(update());
                Log.i("FRIDGELOG", "Firdge with id: " + id + " and name: " + name);
                dialog.dismiss();
            }
        });

        levelDialog = builder.create();
        levelDialog.show();

        return super.onOptionsItemSelected(item);
    }

    public Cursor update() {
        return dbhelp.getCompactListFromDb();
        //return dbhelp.getWritableDatabase().rawQuery("SELECT  * FROM " + ItemDatabaseHelper.TABLE_NAME, null);
    }
}
