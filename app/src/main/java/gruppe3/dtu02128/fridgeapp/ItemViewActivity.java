package gruppe3.dtu02128.fridgeapp;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class ItemViewActivity extends ListActivity {

    private Intent data;
    private String name;
    private FridgeApp app;

    private TextView title;

    private ItemDatabaseHelper dbhelp;
    private SingleItemCursorAdapter adaptercr;

    Button button1;
    ListViewAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_view);

        data = getIntent();
        name = data.getStringExtra("name");

        app = (FridgeApp) getApplication();
        dbhelp = app.getDBHelper();
        adaptercr = app.getAdapterDetail(this, name);


        adapter = new ListViewAdapter(getApplicationContext());

        // Set the title of the product
        title = (TextView) findViewById(R.id.item_title);
        title.setText(name);
        setListAdapter(adaptercr);


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


}
