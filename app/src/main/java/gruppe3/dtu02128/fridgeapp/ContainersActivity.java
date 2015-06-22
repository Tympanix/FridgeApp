package gruppe3.dtu02128.fridgeapp;

import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.hardware.display.VirtualDisplay;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;


public class ContainersActivity extends Activity {

    private ListView listView;
    private Button addButton;
    private ContainerAdapter adapter;
    static private final int REQUEST_CODE_ADD_CONTAINER = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_containers);

        listView = (ListView) findViewById(R.id.list_view);

        adapter = new ContainerAdapter(getApplicationContext());

        listView.setAdapter(adapter);


        addButton = (Button) findViewById(R.id.add_container_button);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //adapter.add("1234", "!!!!");
                Intent addContainerIntent = new Intent(getApplicationContext(),AddContainerActivity.class);
                startActivityForResult(addContainerIntent,REQUEST_CODE_ADD_CONTAINER);

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_containers, menu);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_ADD_CONTAINER){
            if(resultCode == RESULT_OK){
                adapter.add(data.getStringExtra("name"),data.getStringExtra("type"));
            }
        }
    }
}

