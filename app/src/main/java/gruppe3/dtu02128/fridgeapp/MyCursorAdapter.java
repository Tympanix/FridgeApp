package gruppe3.dtu02128.fridgeapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

/**
 * Created by Morten on 18-Jun-15.
 * Cursor adapter to display elements from
 * the database in a ListView
 */
public class MyCursorAdapter extends CursorAdapter {

    protected Context context;
    protected ItemDatabaseHelper dbhelp;



    public MyCursorAdapter(Context context, Cursor c,ItemDatabaseHelper dbhelp) {
        super(context, c, 0);
        this.context = context;
        this.dbhelp = dbhelp;
    }

    public void update(){
        changeCursor(dbhelp.getCompactListFromDb());
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        //Inflate for a single view
        return LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //Configure the view for each element
        final Context mContext = context;
        final String id = cursor.getString(cursor.getColumnIndexOrThrow(dbhelp._ID));
        final int openExpire = cursor.getInt(cursor.getColumnIndexOrThrow(dbhelp.EXPIRES_OPEN));
        final String itemName = cursor.getString(cursor.getColumnIndexOrThrow(dbhelp.FOOD_NAME));
        final int number = cursor.getInt(cursor.getColumnIndexOrThrow(dbhelp.COMPACT_COLUMN_NUMBER));
        final boolean isOpen = cursor.getInt(cursor.getColumnIndexOrThrow(dbhelp.OPEN)) != 0;
        final int isOpenInt = cursor.getInt(cursor.getColumnIndexOrThrow(dbhelp.OPEN));

        //Get expiration date from db
        long millis = cursor.getLong(cursor.getColumnIndexOrThrow(dbhelp.COMPACT_COLUMN_EXPIRE));
        long addedmillis = cursor.getLong(cursor.getColumnIndexOrThrow(dbhelp.DATE_ADDED));

        //Set title of product
        TextView txt = (TextView) view.findViewById(R.id.product_title);
        String append;
        if (number < 2){
            append = "";
        } else {
            append = " (" + number + ")";
        }
        txt.setText(itemName + append);

        // Alternate background colors
        View bg = view.findViewById(R.id.item_linlayout);
        if (cursor.getPosition() % 2 == 1) {
            bg.setBackgroundColor(view.getResources().getColor(R.color.list_item_dark));
        } else {
            bg.setBackgroundColor(view.getResources().getColor(R.color.list_item_light));
        }

        final ProgressBar progg = (ProgressBar) view.findViewById(R.id.progress);

        //Set on click for showing i single item type (ItemViewActivity)
        LinearLayout linlay = (LinearLayout) view.findViewById(R.id.clickme);
        linlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemOnClickOperation(itemName);
            }
        });

        //Set the "days until expire" text view
        final CheckBox check = (CheckBox) view.findViewById(R.id.open_check);
        TextView txt2 = (TextView) view.findViewById(R.id.daystoexpire);

        DateTime dateExpire = new DateTime(millis);
        DateTime dateAdded = new DateTime(addedmillis);
        DateTime dateToday = new DateTime();
        int daysToExpire = Days.daysBetween(dateToday.toLocalDate(), dateExpire.toLocalDate()).getDays();

        String daysAppend;
        if (daysToExpire == 1){
            daysAppend = " day";
        } else {
            daysAppend = " days";
        }

        txt2.setText(daysToExpire + daysAppend);

        // Set progress bar
        int daysFromAddToExpire = Days.daysBetween(dateAdded.toLocalDate(), dateExpire.toLocalDate()).getDays();
        int progress;

        if (daysFromAddToExpire == 0 && daysToExpire == 0){
            progress = progg.getMax();
        }
        else {
            progress = Math.round(((float) (daysFromAddToExpire-daysToExpire) / (float) daysFromAddToExpire) * progg.getMax());
        }

        progg.setProgress(progress);

        //Configure check box and listener
        check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                dbhelp.updateIsOpened(isChecked, id);
                update();

            }
        });

        check.setChecked(isOpen);

        if (number > 1){
            check.setClickable(false);
            check.setVisibility(View.GONE);
        } else {
            check.setClickable(true);
            check.setVisibility(View.VISIBLE);
        }

        // Configure remove button and listener
        ImageButton butt = (ImageButton) view.findViewById(R.id.remove_button);

        if (number > 1){
            butt.setImageResource(R.drawable.ic_menu_black_18dp);
            //check.setClickable(false);
            //check.setVisibility(View.GONE);
        } else {
            butt.setImageResource(R.drawable.ic_close_black_18dp);
            //check.setClickable(true);
            //check.setVisibility(View.VISIBLE);
        }

        butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemRemoveOperation(itemName, id, number);

            }
        });

    }

    private boolean showDialogCloseItem() {
        return false;

    }

    public void itemRemoveOperation(String itemName, String id, int number){
        if (number > 1){
            MyCursorAdapter.this.context.startActivity(new Intent(MyCursorAdapter.this.context, ItemViewActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra("name", itemName));
        } else {
            dbhelp.removeItemById(id);
            update();
        }
    }

    public void itemOnClickOperation(String itemName){
        context.startActivity(new Intent(MyCursorAdapter.this.context, ItemViewActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra("name", itemName));
    }


}
