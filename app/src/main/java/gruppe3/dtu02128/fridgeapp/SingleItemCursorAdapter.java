package gruppe3.dtu02128.fridgeapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class SingleItemCursorAdapter extends CursorAdapter {
    ItemDatabaseHelper dbhelp;
    private String id2;
    private String name;


    public SingleItemCursorAdapter(Context context, Cursor c, ItemDatabaseHelper dbhelp, String name) {
        super(context, c ,0);
        this.dbhelp = dbhelp;
        this.name = name;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        //Inflate for a single view
        return LayoutInflater.from(context).inflate(R.layout.list_item_detail,parent,false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        //Configure the view for each element
        final String id = cursor.getString(cursor.getColumnIndexOrThrow(dbhelp._ID));
        final String name = cursor.getString(cursor.getColumnIndexOrThrow(dbhelp.FOOD_NAME));
        final int openExpire = cursor.getInt(cursor.getColumnIndexOrThrow(dbhelp.EXPIRES_OPEN));
        final int isOpenInt = cursor.getInt(cursor.getColumnIndexOrThrow(dbhelp.OPEN));
        final boolean isOpen = cursor.getInt(cursor.getColumnIndexOrThrow(dbhelp.OPEN)) != 0;
        final Long expirationDate = cursor.getLong(cursor.getColumnIndexOrThrow(dbhelp.EXPIRE_DATE));
        SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat daysto = new SimpleDateFormat("dd");
        //       txt.setText(cursor.getString(cursor.getColumnIndexOrThrow("name")));

        //Get current date in millis
        Long millis = cursor.getLong(cursor.getColumnIndexOrThrow(dbhelp.COMPACT_COLUMN_EXPIRE));


        //Set title of product
        TextView txt = (TextView) view.findViewById(R.id.product_title);
        txt.setText(id + ": " + cursor.getString(cursor.getColumnIndexOrThrow("name")));

        // Alternate background colors
        View bg = view.findViewById(R.id.linearaa);
        if (cursor.getPosition() % 2 == 1) {
            bg.setBackgroundColor(view.getResources().getColor(R.color.abc_primary_text_material_dark));
        } else {
            bg.setBackgroundColor(Color.rgb(247, 247 ,247));
        }

        final ProgressBar progg = (ProgressBar) view.findViewById(R.id.progress);

        CheckBox check = (CheckBox) view.findViewById(R.id.open_check);
        TextView txt2 = (TextView) view.findViewById(R.id.daystoexpire);

        DateTime dateExpire = new DateTime(millis);
        DateTime dateToday = new DateTime();
        int days = Days.daysBetween(dateToday.toLocalDate(), dateExpire.toLocalDate()).getDays();

        String daysAppend;
        if (days == 1){
            daysAppend = " day";
        } else {
            daysAppend = " days";
        }

        check.setChecked(isOpen);

        txt2.setText(days + daysAppend);

        //Configure check box and listeners
        check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dbhelp.updateIsOpened(isChecked, id);
                changeCursor(dbhelp.getFoodList(name));
            }
        });


        // Configure remove button and listeners
        ImageButton butt = (ImageButton) view.findViewById(R.id.remove_button);
        butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbhelp.removeItemById(id);
                changeCursor(dbhelp.getFoodList(name));
            }
        });

        LinearLayout linlay = (LinearLayout) view.findViewById(R.id.clickme);
        linlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Todo: START ACTIVITY FOR VIEWING MULTIPLE ITEMS
                Log.i("CLICK", "CLICKED65");
                //mContext.changeDate(name, expirationDate);
                setID(id);

            }
        });

    }

    public void update(){
        changeCursor(dbhelp.getFoodList(name));

    }

    public void setID(String id) {
        id2 = id;
    }

    // TODO: Current time in mullis should not be here
    public void updateDate() {
        dbhelp.updateExpirationDate(System.currentTimeMillis(), id2);
    }


}
