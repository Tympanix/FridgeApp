package gruppe3.dtu02128.fridgeapp;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.Calendar;
import java.util.Random;


public class MainActivity extends AppCompatActivity {

    private final static int ADD_PRODUCT = 1;
    private Random r = new Random();

    Button addItemButton;

    ItemDatabaseHelper dbhelp;
    MyCursorAdapter adaptercr;

    FridgeApp app;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        app = (FridgeApp) getApplication();
        dbhelp = app.getDBHelper();
        adaptercr = app.getDBCursor();
        context = getApplicationContext();


        /*
        FragmentManager fm = getFragmentManager();
        FoodListFragment list = new FoodListFragment();
        fm.beginTransaction().add(android.R.id.content, list).commit();
        */

        addItemButton = (Button) findViewById(R.id.add_item_button);

        // Set onClickListener for adding new items
        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddProductActivity.class);
                startActivityForResult(intent, ADD_PRODUCT);
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
        int amount = data.getIntExtra("number",1);
        cal.set(year, month, day);

        while(amount > 0) {
            dbhelp.insertItemToDb(name, expiresafter, cal.getTimeInMillis());
            amount--;
        }

        ContentValues cw = new ContentValues();
        cw.put(dbhelp.REGISTER_COLUMN_NAME, name);
        cw.put(dbhelp.REGISTER_COLUMN_EXPIRES_OPEN, expiresafter);
        //Only add Barcode if it exists

        if(number != 1) {
            cw.put(dbhelp.REGISTER_COLUMN_ID,number);
        }
        try {
            dbhelp.getWritableDatabase().insertOrThrow(dbhelp.REGISTER_TABLE_NAME, null, cw);
        } catch (Throwable exception) {
            //FOOD TYPE ALREADY EXISTS, MOVE ALONG
        }
        cw.clear();

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

        if (id == R.id.action_settings) {
            // Show settings menu
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_containers){
            showDialogChangeContainer();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public Cursor update() {
        return dbhelp.getCompactListFromDb();
    }

    public void showDialogChangeContainer(){
        // Creating dialog for changing container
        final AlertDialog levelDialog;
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
    }
}
