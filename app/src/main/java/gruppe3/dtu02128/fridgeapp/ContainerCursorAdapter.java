package gruppe3.dtu02128.fridgeapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Marcus on 19-06-2015.
 */
public class ContainerCursorAdapter extends CursorAdapter {

    Context context;
    ItemDatabaseHelper dbhelp;
    Cursor cursor;

    //ArrayList<ContainerItem> items = new ArrayList<ContainerItem>();

    public ContainerCursorAdapter(Context context, Cursor c, ItemDatabaseHelper dbhelp) {
        super(context, c,0);
        this.context = context;
        this.dbhelp = dbhelp;
        cursor = c;
        //items.addAll();
    }

    public void add(String name, String type){
        //items.add(new ContainerItem(name,type));
        dbhelp.addContainerToDB(name, type);
        update();
        //notifyDataSetChanged();
    }

    public void add(ContainerItem item){
        //items.add(item);
        dbhelp.addContainerToDB(item.getName(), item.getType());
        //notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return cursor.getCount();
    }

    @Override
    public Object getItem(int position) {
        return cursor.getString(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    /*
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        RelativeLayout layout = (RelativeLayout)
                LayoutInflater.from(this.context).inflate(R.layout.list_container, null);

        TextView nameView = (TextView) layout.findViewById(R.id.name);
        nameView.setText(items.get(position).getName());

        TextView typeView = (TextView) layout.findViewById(R.id.type);
        typeView.setText(items.get(position).getType());

        Button deleteButton = (Button) layout.findViewById(R.id.delete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove(position);
            }
        });

        return layout;
    }
    */

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_container,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final String ID = cursor.getString(cursor.getColumnIndexOrThrow(dbhelp.CONTAINER_COLUMN_ID));
        final String nameContainer = cursor.getString(cursor.getColumnIndexOrThrow(dbhelp.CONTAINER_COLUMN_NAME));
        final String typeContainer = cursor.getString(cursor.getColumnIndexOrThrow(dbhelp.CONTAINER_COLUMN_TYPE));



        TextView nameView = (TextView) view.findViewById(R.id.name);
        nameView.setText(nameContainer);

        TextView typeView = (TextView) view.findViewById(R.id.type);
        typeView.setText(typeContainer);

        Button deleteButton = (Button) view.findViewById(R.id.delete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove(ID);
                update();
                notifyDataSetChanged();
                //view.invalidateViews();
                //listView.setAdapter(adapter);
            }
        });
    }

    public void remove(String ID) {
        dbhelp.removeContainer(ID);
    }

    public void update(){
        changeCursor(dbhelp.getContainerListFromDB());

    }

}
