package gruppe3.dtu02128.fridgeapp;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class FridgeApp extends Application {

    private ItemDatabaseHelper dbhelp;
    private MyCursorAdapter adaptercr;
    private Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i("FRIDGELOG", "App started");

        dbhelp = new ItemDatabaseHelper(this);
        dbhelp.deleteDatabase();
        dbhelp.loadTestData();
        context = getApplicationContext();

        adaptercr = new MyCursorAdapter(this, update(), dbhelp);

    }

    public Cursor update() {
        return dbhelp.getCompactListFromDb();
    }

    public Cursor getFromRegister(String id) {
        return dbhelp.getWritableDatabase().rawQuery("SELECT  * FROM " + ItemDatabaseHelper.REGISTER_TABLE_NAME +
                " WHERE " + ItemDatabaseHelper.REGISTER_COLUMN_ID + " =?", new String[]{id});
    }



    public Cursor getFromRegister() {
        return dbhelp.getWritableDatabase().rawQuery("SELECT  * FROM " + ItemDatabaseHelper.REGISTER_TABLE_NAME, null);
    }


    public ItemDatabaseHelper getDBHelper(){
        return dbhelp;
    }

    public MyCursorAdapter getDBCursor(){
        return adaptercr;
    }

    public SingleItemCursorAdapter getAdapterDetail(ItemViewActivity context, String name) {
        return new SingleItemCursorAdapter(context, dbhelp.getFoodList(name), dbhelp);
    }

    public ContainerCursorAdapter getContainerAdapter(Context context){
        return new ContainerCursorAdapter(context,dbhelp.getContainerListFromDB(),dbhelp);
    }

}
