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


public class SingleItemCursorAdapter extends MyCursorAdapter {

    private String name;
    private ItemViewActivity activity;

    public SingleItemCursorAdapter(Context context, Cursor c, ItemDatabaseHelper dbhelp, String name) {
        super(context, c, dbhelp);
        this.name = name;
        this.activity = (ItemViewActivity) context;
    }

    @Override
    public void update() {
        changeCursor(dbhelp.getFoodList(name));
    }

    @Override
    public void itemOnClickOperation(String itemName) {
        // TODO: Should open a date picker fragment and call updateDate() below on return
        return;
    }

    public void updateDate() {
        // TODO: Implement changing the expiration date of an item
        return;
    }
    
}
