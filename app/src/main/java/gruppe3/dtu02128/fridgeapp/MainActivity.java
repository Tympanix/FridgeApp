package gruppe3.dtu02128.fridgeapp;

import android.app.ListActivity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;


public class MainActivity extends ListActivity {

    private final static int ADD_PRODUCT = 1;

    Button button1;
    Button button2;
    ListViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        adapter = new ListViewAdapter(getApplicationContext());
        button1 = (Button) findViewById(R.id.click_button);
        button2 = (Button) findViewById(R.id.click_button2);
        setListAdapter(adapter);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Add item to database


                button1.setText("Clicked");
                adapter.add();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inte = new Intent(MainActivity.this,AddProductActivity.class);
                startActivityForResult(inte,ADD_PRODUCT);
            }
        });
    }

    public void removeItem(View v) {
        
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Log.i("test","removing");
        adapter.remove(position);
        super.onListItemClick(l, v, position, id);
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
}
