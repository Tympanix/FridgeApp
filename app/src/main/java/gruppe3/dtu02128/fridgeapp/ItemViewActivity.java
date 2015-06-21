package gruppe3.dtu02128.fridgeapp;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class ItemViewActivity extends ListActivity {

    private EditText mItemExpiresOpened;
    private FridgeApp app;
    private ItemDatabaseHelper dbhelp;
    private MyCursorAdapter adaptercr;

    private final static int ADD_PRODUCT = 1;

    Button button1;
    ListViewAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_view);

        app = (FridgeApp) getApplication();
        dbhelp = app.getDBHelper();
        adaptercr = app.getDBCursor();

        mItemExpiresOpened = (EditText) findViewById(R.id.expires_opened);


        adapter = new ListViewAdapter(getApplicationContext());
        button1 = (Button) findViewById(R.id.add_item_add_button);
        setListAdapter(adapter);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Add item to database
                button1.setText("Add extra+");
                adapter.add();
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


}
