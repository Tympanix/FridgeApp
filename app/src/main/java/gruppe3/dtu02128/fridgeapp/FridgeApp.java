package gruppe3.dtu02128.fridgeapp;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;

public class FridgeApp extends Application {

    private ItemDatabaseHelper dbhelp;
    private MyCursorAdapter adaptercr;
    private Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        dbhelp = new ItemDatabaseHelper(this);
        dbhelp.deleteDatabase();
        context = getApplicationContext();

        adaptercr = new MyCursorAdapter(this, update(), dbhelp);
    }

    public Cursor update() {
        return dbhelp.getWritableDatabase().rawQuery("SELECT  * FROM " + ItemDatabaseHelper.TABLE_NAME, null);
    }

    public Cursor getFromRegister(String id) {
        return dbhelp.getWritableDatabase().rawQuery("SELECT  * FROM " + ItemDatabaseHelper.REGISTER_TABLE_NAME +
                " WHERE " + ItemDatabaseHelper.REGISTER_COLUMN_ID + " =?", new String[] {id});
    }

    public ItemDatabaseHelper getDBHelper(){
        return dbhelp;
    }

    public MyCursorAdapter getDBCursor(){
        return adaptercr;
    }
}
