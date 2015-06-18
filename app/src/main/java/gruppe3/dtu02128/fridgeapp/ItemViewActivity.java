package gruppe3.dtu02128.fridgeapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;


public class ItemViewActivity extends Activity {


    static final int DATE_DIALOG_ID = 0;
    private static final String[] items = new String[] {
            "Milk", "Milk1", "Milk2", "milk3", "Eggs", "Butter", "Meat", "Juice"
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_view);




        AutoCompleteTextView autoItemName = (AutoCompleteTextView) findViewById(R.id.item_name);
        ArrayAdapter<String> nameAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, items);
        autoItemName.setAdapter(nameAdapter);





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
}
