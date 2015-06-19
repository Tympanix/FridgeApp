package gruppe3.dtu02128.fridgeapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by Morten on 18-Jun-15.
 */
public class MyCursorAdapter extends CursorAdapter {
    Context mContext;
    ItemDatabaseHelper dbhelp;

    public MyCursorAdapter(Context context, Cursor c,ItemDatabaseHelper dbhelp) {
        super(context, c,0);
        mContext = context;
        this.dbhelp = dbhelp;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        //Inflate for a single view
        Log.i("test","new view");
        return LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        Log.i("test", "Binding view");
        final Cursor cursor1 = cursor;
        final String id = cursor1.getString(cursor1.getColumnIndexOrThrow("_id"));
        SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat daysto = new SimpleDateFormat("dd");
 //       txt.setText(cursor.getString(cursor.getColumnIndexOrThrow("name")));

        //Get current date in millis
        Long millis = cursor1.getLong(cursor1.getColumnIndexOrThrow("dateexpire"));

        //Set title of product
        TextView txt = (TextView) view.findViewById(R.id.product_title);
        txt.setText(cursor1.getString(cursor1.getColumnIndexOrThrow("name")));

        //Set next text view
        Calendar cal = Calendar.getInstance();
        cal.getTimeInMillis();

        // + " " + cursor1.getString(cursor1.getColumnIndexOrThrow(ItemDatabaseHelper.EXPIRES_OPEN))

        TextView txt2 = (TextView) view.findViewById(R.id.title2);
        Long diff = TimeUnit.DAYS.convert(millis - cal.getTimeInMillis(), TimeUnit.MILLISECONDS);
        txt2.setText(diff.toString());

        //Configure check box and listeners
        CheckBox check = (CheckBox) view.findViewById(R.id.open_check);

        check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.i("test","changing checked state for" + id);
                int open = isChecked ? 1 : 0;
                ContentValues cont = new ContentValues();
                cont.put(ItemDatabaseHelper.OPEN,open);
                dbhelp.getWritableDatabase().update(ItemDatabaseHelper.TABLE_NAME, cont, ItemDatabaseHelper._ID + "=?", new String[]{id});
                changeCursor(dbhelp.getWritableDatabase().rawQuery("SELECT  * FROM " + ItemDatabaseHelper.TABLE_NAME, null));
            }
        });

        int open = cursor1.getInt(cursor1.getColumnIndexOrThrow("open"));
        if(open == 1) {
            check.setChecked(true);
        } else {
            check.setChecked(false);
        }

        Button butt = (Button) view.findViewById(R.id.remove_button);
        butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("test", id + " was clicked");
                dbhelp.getWritableDatabase().delete(ItemDatabaseHelper.TABLE_NAME, ItemDatabaseHelper._ID + "=?",
                        new String[]{id});
                changeCursor(dbhelp.getWritableDatabase().rawQuery("SELECT  * FROM " + ItemDatabaseHelper.TABLE_NAME, null));
            }
        });

    }
}
