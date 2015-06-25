package gruppe3.dtu02128.fridgeapp;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
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


public class SingleItemCursorAdapter extends MyCursorAdapter {

    private String name;
    private ItemViewActivity activity;
    private ItemDatabaseHelper dbHelp;

    public SingleItemCursorAdapter(Context context, Cursor c, ItemDatabaseHelper dbhelp, String name) {
        super(context, c, dbhelp);
        this.name = name;
        this.activity = (ItemViewActivity) context;
        this.dbHelp = dbhelp;

    }


    @Override
    public void update() {
        changeCursor(dbhelp.getFoodList(name));
    }

    @Override
    public void itemOnClickOperation(String itemName, String id) {

        Boolean changeDate = true;
        activity.resetChangeDateCounter();
        activity.setChangeDate(changeDate);
        Bundle bund = new Bundle();
        bund.putBoolean("CHANGEDATE", changeDate);
        bund.putString("ITEM_TITLE", name);
        bund.putString("Item_ID" ,id);

        long expDate = dbhelp.getExpirationDateByName(id);
        activity.setID(id);

        //Log.i("DATE", String.valueOf(expDate));
        bund.putLong("EXPIRATION_DATE", expDate);
        // Create a new DatePickerFragment
        DialogFragment newFragment = new DatePickerFragment();

        newFragment.setArguments(bund);
        // Display DatePickerFragment
        newFragment.show(activity.getFragmentManager(), "DatePicker");

        return;
    }

    public void updateDate() {
        return;
    }
    
}
