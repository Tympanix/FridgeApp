package gruppe3.dtu02128.fridgeapp;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

public class FridgeApp extends Application {

    private ItemDatabaseHelper dbhelp;
    private MyCursorAdapter adaptercr;
    private Context context;

    public static final String ACTION_REG_ALARM = "gruppe3.dtu02128.fridgeapp.ACTION_REG_ALARM";
    public static final String ACTION_NOTIFICATIONS = "gruppe3.dtu02128.fridgeapp.ACTION_NOTIFICATIONS";
    public static final int DEFAULT_ALARM_HOUR = 16;
    public static final int DEFAULT_ALARM_MINUTE = 49;

    private static int SELECTED_FRIDGE = -1;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i("FRIDGELOG", "App started");
        setUpNotificationAlarm();

        dbhelp = new ItemDatabaseHelper(this);
        dbhelp.deleteDatabase();
        checkForFridges();
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

    public SingleItemCursorAdapter getAdapterDetail(String name) {
        return new SingleItemCursorAdapter(this, dbhelp.getFoodList(name), dbhelp, name);
    }

    public ContainerCursorAdapter getContainerAdapter(ContainersActivity context){
        return new ContainerCursorAdapter(context, dbhelp.getContainerListFromDB(),dbhelp);
    }

    public void setUpNotificationAlarm(){
        // Create an Intent to broadcast
        Log.i("FRIDGELOG", "Broadcasting intent to update alarm");
        Intent alarmIntent = new Intent(this,
                FoodExpireBroadcastReceiver.class);

        alarmIntent.setAction(ACTION_REG_ALARM);

        sendBroadcast(alarmIntent);

    }

    public int getSelectedFridge(){
        return SELECTED_FRIDGE;
    }

    public void setSelectedFridge(int id){
        SELECTED_FRIDGE = id;
    }

    public void checkForFridges(){
        Cursor cursor = dbhelp.getContainerListFromDB();

        if (cursor.getCount() == 0){
            dbhelp.addContainerToDB("Default", String.valueOf(R.string.container_type_fridge));
            Cursor cursor1 = dbhelp.getContainerListFromDB();
            cursor1.moveToFirst();
            int fridge = cursor1.getInt(cursor1.getColumnIndexOrThrow(dbhelp.CONTAINER_COLUMN_ID));
            setSelectedFridge(fridge);
        }
    }

}
