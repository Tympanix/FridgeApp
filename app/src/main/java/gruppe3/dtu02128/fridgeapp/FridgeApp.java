package gruppe3.dtu02128.fridgeapp;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

    SharedPreferences sp;
    SharedPreferences.Editor spedit;
    @Override
    public void onCreate() {
        super.onCreate();

        sp = getSharedPreferences(getString(R.string.shared_preference),Context.MODE_PRIVATE);
        spedit = sp.edit();
        SELECTED_FRIDGE = sp.getInt("selectedfridge", -1);

        //Log.i("FRIDGELOG","Selected fridge " + SELECTED_FRIDGE);
        setUpNotificationAlarm();

        dbhelp = new ItemDatabaseHelper(this);
        checkForFridges();
        context = getApplicationContext();

        adaptercr = new MyCursorAdapter(this, update(), dbhelp) {

        };

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


    public ContainerCursorAdapter getContainerAdapter(){
        return new ContainerCursorAdapter(this, dbhelp.getContainerListFromDB(),dbhelp);
    }

    public void setUpNotificationAlarm(){
        // Create an Intent to broadcast
        //Log.i("FRIDGELOG", "Broadcasting intent to update alarm");
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
        spedit.putInt("selectedfridge",id);
        spedit.commit();
    }

    public void checkForFridges(){
        Cursor cursor = dbhelp.getContainerListFromDB();

        if (cursor.getCount() == 0){
            createDefaultFridge();
            dbhelp.loadTestData();
        }
    }

    public void createDefaultFridge(){
        dbhelp.addContainerToDB("Default", getString(R.string.container_type_fridge));
        selectFirstFridgeInDB();
    }

    public void selectFirstFridgeInDB(){
        Cursor cursor = dbhelp.getContainerListFromDB();
        if (cursor.getCount() == 0){
            setSelectedFridge(-1);
            return;
        }
        cursor.moveToFirst();
        int fridgeId = cursor.getInt(cursor.getColumnIndexOrThrow(dbhelp.CONTAINER_COLUMN_ID));
        setSelectedFridge(fridgeId);
    }

}
