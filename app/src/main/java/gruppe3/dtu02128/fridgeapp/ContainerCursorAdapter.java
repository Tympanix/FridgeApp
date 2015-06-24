package gruppe3.dtu02128.fridgeapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Marcus on 19-06-2015.
 */
public class ContainerCursorAdapter extends CursorAdapter {

    Context context;
    ItemDatabaseHelper dbhelp;
    FridgeApp app;

    public ContainerCursorAdapter(Context context, Cursor c, ItemDatabaseHelper dbhelp) {
        super(context, c,0);
        this.context = context;
        this.dbhelp = dbhelp;
        app = (FridgeApp) context.getApplicationContext();
    }

    public void add(String name, String type){
        dbhelp.addContainerToDB(name, type);
        update();
        //notifyDataSetChanged();
    }

    public void add(ContainerItem item){
        //items.add(item);
        dbhelp.addContainerToDB(item.getName(), item.getType());
        //notifyDataSetChanged();
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
    public void bindView(View view, final Context context, final Cursor cursor) {
        final String ID = cursor.getString(cursor.getColumnIndexOrThrow(dbhelp.CONTAINER_COLUMN_ID));
        final String nameContainer = cursor.getString(cursor.getColumnIndexOrThrow(dbhelp.CONTAINER_COLUMN_NAME));
        final String typeContainer = cursor.getString(cursor.getColumnIndexOrThrow(dbhelp.CONTAINER_COLUMN_TYPE));



        TextView nameView = (TextView) view.findViewById(R.id.name);
        nameView.setText(nameContainer);

        TextView typeView = (TextView) view.findViewById(R.id.type);
        typeView.setText(typeContainer);

        // Alternate background colors
        View bg = view.findViewById(R.id.item_linlayout);
        if (cursor.getPosition() % 2 == 1) {
            bg.setBackgroundColor(view.getResources().getColor(R.color.list_item_dark));
        } else {
            bg.setBackgroundColor(view.getResources().getColor(R.color.list_item_light));
        }

        ImageButton deleteButton = (ImageButton) view.findViewById(R.id.delete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (cursor.getCount() <= 1){
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("You must have at least one container")
                            .setPositiveButton("Return", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                    // Create the AlertDialog object and return it
                    builder.create();
                    builder.show();
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Deleteing container " + nameContainer + " will also delete all items in it. Are you sure?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Delete the container
                                Log.i("FRIDGELOG", "Removing container with id: " + ID + " from database");
                                remove(ID);
                                if (String.valueOf(app.getSelectedFridge()).equals(ID)) {
                                    app.selectFirstFridgeInDB();
                                }

                                update();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                // Create the AlertDialog object and return it
                builder.create();
                builder.show();

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
