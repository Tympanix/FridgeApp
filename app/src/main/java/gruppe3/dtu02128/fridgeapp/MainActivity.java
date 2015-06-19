package gruppe3.dtu02128.fridgeapp;

import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.util.Calendar;
import java.util.Date;


public class MainActivity extends ListActivity {

    private final static int ADD_PRODUCT = 1;



    Button button1;
    Button button2;
    ListViewAdapter adapter;
    ItemDatabaseHelper dbhelp;

    MyCursorAdapter adaptercr;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbhelp = new ItemDatabaseHelper(this);
        dbhelp.deleteDatabase();
        context = getApplicationContext();

        adaptercr = new MyCursorAdapter(this,update(),dbhelp);

        //adapter = new ListViewAdapter(getApplicationContext());
        button1 = (Button) findViewById(R.id.click_button);
        button2 = (Button) findViewById(R.id.click_button2);
//        setListAdapter(adapter);
        setListAdapter(adaptercr);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button1.setText("Clicked");

                ContentValues cw = new ContentValues();
                cw.put(ItemDatabaseHelper.FOOD_NAME, "Apple");
                cw.put(ItemDatabaseHelper.EXPIRES_OPEN,5);
                cw.put(ItemDatabaseHelper.EXPIRE_DATE,500);
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
                startActivityForResult(inte,ADD_PRODUCT);
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
        cw.put(ItemDatabaseHelper.EXPIRE_DATE,cal.getTimeInMillis());

        dbhelp.getWritableDatabase().insert(ItemDatabaseHelper.TABLE_NAME,null,cw);
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
        return dbhelp.getWritableDatabase().rawQuery("SELECT  * FROM " + ItemDatabaseHelper.TABLE_NAME, null);
    }
}
