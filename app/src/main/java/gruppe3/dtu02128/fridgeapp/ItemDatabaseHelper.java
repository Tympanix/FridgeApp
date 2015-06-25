package gruppe3.dtu02128.fridgeapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.Random;

/**
 * Created by Morten on 18-Jun-15.
 */
public class ItemDatabaseHelper extends SQLiteOpenHelper {
    private static FridgeApp app;

    final static String NAME = "food_db";
    final static int VERSION = 1;
    final static long MILL_ONE_DAY = 86400000;

    // Food database
    final static String TABLE_NAME = "food";
    final static String FOOD_NAME = "name";
    final static String EXPIRES_OPEN = "openexpire";
    final static String EXPIRE_DATE = "dateexpire";
    final static String OPEN_DATE = "opendate";
    final static String DATE_ADDED = "dateadded";
    final static String FOOD_FRIDGE_ID = "fridgeid";
    final static String OPEN = "isopen";
    final private Context context;
    final static String _ID = "_id";

    // Food register database
    final static String REGISTER_TABLE_NAME = "register";
    final static String REGISTER_COLUMN_ID = "_id";
    final static String REGISTER_COLUMN_NAME = "name";
    final static String REGISTER_COLUMN_EXPIRES_OPEN = "openexpire";

    // Compact list column names
    final static String COMPACT_COLUMN_NUMBER = "number";
    final static String COMPACT_COLUMN_EXPIRE = "expire";

    //Container table
    final static String CONTAINER_TABLE_NAME = "containers";
    final static String CONTAINER_COLUMN_ID = "_id";
    final static String CONTAINER_COLUMN_NAME = "name";
    final static String CONTAINER_COLUMN_TYPE = "type";


    Random r = new Random(55447347295858L);

    public ItemDatabaseHelper(Context context) {
        super(context, NAME, null, VERSION);
        this.context = context;
        this.app = (FridgeApp) context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create food database
        db.execSQL("CREATE TABLE food (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FOOD_NAME + " TEXT NOT NULL, " +
                EXPIRES_OPEN + " BIGINT NOT NULL, " +
                EXPIRE_DATE + " BIGINT, " +
                OPEN_DATE + " BIGINT, " +
                DATE_ADDED + " BIGINT, " +
                FOOD_FRIDGE_ID + " INTEGER, " +
                OPEN + " BOOLEAN NOT NULL" +
                ")");

        // Create register database
        db.execSQL("CREATE TABLE " + REGISTER_TABLE_NAME + " (" +
                REGISTER_COLUMN_NAME + " TEXT PRIMARY KEY UNIQUE COLLATE NOCASE, " +
                REGISTER_COLUMN_ID + " TEXT, " +
                REGISTER_COLUMN_EXPIRES_OPEN + " INTEGER NOT NULL" +
                ")");

        db.execSQL("CREATE TABLE " + CONTAINER_TABLE_NAME + " (" +
                CONTAINER_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                CONTAINER_COLUMN_NAME + " TEXT NOT NULL, " +
                CONTAINER_COLUMN_TYPE + " TEXT NOT NULL" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void deleteDatabase() {
        context.deleteDatabase(NAME);
    }

    public Cursor getFoodList(String name){
        String fridgeQuery = "";
        if (app.getSelectedFridge() != -1){
            fridgeQuery = " AND " + FOOD_FRIDGE_ID + " = " + app.getSelectedFridge();
        }

        return getReadableDatabase().rawQuery("SELECT *, '1' AS " + COMPACT_COLUMN_NUMBER + ", CASE WHEN " + OPEN + " THEN MIN("+ OPEN_DATE + " + " + EXPIRES_OPEN + ", " + EXPIRE_DATE + ") " +
                "ELSE " + EXPIRE_DATE + " END AS " + COMPACT_COLUMN_EXPIRE + " FROM " + TABLE_NAME +
                " WHERE " + FOOD_NAME + " = ? " + fridgeQuery + " ORDER BY " + COMPACT_COLUMN_EXPIRE + " ASC", new String[] {name});

    }

    public void removeItemById(String id){
        getWritableDatabase().delete(ItemDatabaseHelper.TABLE_NAME, ItemDatabaseHelper._ID + "=?",
                new String[]{id});
    }

    public void updateIsOpened(boolean open, String id){
        ContentValues cont = new ContentValues();
        long time = System.currentTimeMillis();
        cont.put(OPEN, open);
        cont.put(OPEN_DATE, time);
        getWritableDatabase().update(ItemDatabaseHelper.TABLE_NAME, cont, _ID + "=?", new String[]{id});
    }

    public Cursor getAllFromDb() {
        return getWritableDatabase().rawQuery("SELECT  * FROM " + TABLE_NAME, null);
    }

    public Cursor getCompactListFromDb(){
        String fridgeQuery = "";
        if (app.getSelectedFridge() != -1){
            fridgeQuery = " WHERE " + FOOD_FRIDGE_ID + " = " + app.getSelectedFridge();
        }

        String query = "SELECT " + _ID + ", " + FOOD_NAME + ", " + EXPIRE_DATE + ", " + EXPIRES_OPEN + ", " + OPEN_DATE + ", " + OPEN + ", " + DATE_ADDED + ", " +
                "count(" + FOOD_NAME + ") AS " + COMPACT_COLUMN_NUMBER + ", " +
                "MIN(CASE WHEN " + OPEN + " THEN MIN(" + OPEN_DATE + " + " + EXPIRES_OPEN + ", " + EXPIRE_DATE + ") " +
                "ELSE " + EXPIRE_DATE + " END) AS "+ COMPACT_COLUMN_EXPIRE +
                " FROM " + TABLE_NAME + fridgeQuery + " GROUP BY " + FOOD_NAME + " ORDER BY " + COMPACT_COLUMN_EXPIRE + " ASC;";

        return getWritableDatabase().rawQuery(query, null);

    }

    public boolean getOpenedById(String id){
        String query = "SELECT " + _ID + ", " + OPEN + " FROM " + TABLE_NAME + " WHERE " + _ID + " =?";
        Cursor cursor = getReadableDatabase().rawQuery(query, new String[] {id});

        if (cursor.getCount() < 1){
            return false;
        } else {
            cursor.moveToFirst();
            return (cursor.getInt(cursor.getColumnIndexOrThrow(OPEN)) > 0);
        }
    }

    public void loadTestData() {

        insertTestToDB("Milk", 5, false);
        insertTestToDB("Milk", 5, true);
        insertTestToDB("Milk", 5, false);
        insertTestToDB("Milk", 5, false);
        insertTestToDB("Milk", 5, true);
        insertTestToDB("Milk", 5, false);

        insertTestToDB("Bread", 12, false);
        insertTestToDB("Bread", 12, true);
        insertTestToDB("Bread", 12, false);

        insertTestToDB("Cheese", 7, false);
        insertTestToDB("Cheese", 7, true);

        insertTestToDB("Beer", 3, true);

        insertTestToDB("Tomatoes", 6, true);
        insertTestToDB("Tomatoes", 6, true);

    }

    public void insertTestToDB(String name, int openExpire, boolean open) {
        ContentValues cw = new ContentValues();
        cw.put(FOOD_NAME, name);
        cw.put(EXPIRES_OPEN, openExpire * MILL_ONE_DAY);
        long time = System.currentTimeMillis() + (r.nextInt(30)+1) * 3600*24*1000L;
        cw.put(EXPIRE_DATE, time);
        cw.put(OPEN_DATE, System.currentTimeMillis());
        cw.put(DATE_ADDED, System.currentTimeMillis() - 2 * MILL_ONE_DAY);
        cw.put(FOOD_FRIDGE_ID, app.getSelectedFridge());
        cw.put(OPEN, open);
        getWritableDatabase().insert(TABLE_NAME, null, cw);
        cw.clear();

        cw.put(REGISTER_COLUMN_NAME, name);
        cw.put(REGISTER_COLUMN_EXPIRES_OPEN, openExpire);
        try {
            getWritableDatabase().insertOrThrow(REGISTER_TABLE_NAME, null, cw);
        }  catch (Throwable exception) {
            //FOOD TYPE ALREADY EXISTS, MOVE ALONG
        }
        cw.clear();
    }

    public void insertItemToDb(String name, int expiresAfter, long expireDate) {
        //Log.i("Added Item", "It Works");
        ContentValues cw = new ContentValues();
        cw.put(FOOD_NAME, name);
        cw.put(EXPIRES_OPEN, (expiresAfter * MILL_ONE_DAY));
        cw.put(EXPIRE_DATE, expireDate);
        cw.put(OPEN_DATE, System.currentTimeMillis());
        cw.put(DATE_ADDED, System.currentTimeMillis());
        cw.put(FOOD_FRIDGE_ID, app.getSelectedFridge());
        cw.put(OPEN, false);
        getWritableDatabase().insert(TABLE_NAME, null, cw);
        cw.clear();
    }

    public String[] getProductNames() {
        Cursor c =  getWritableDatabase().rawQuery("SELECT  * FROM " + REGISTER_TABLE_NAME, null);
        String[] names = new String[c.getCount()];
        //Log.i("test", "Found " + c.getCount() + " items");
        while (c.moveToNext()) {
            names[c.getPosition()] = c.getString(c.getColumnIndexOrThrow(REGISTER_COLUMN_NAME));
        }
        return names;
    }

    public Cursor getRegisterByName(String name) {
        return getWritableDatabase().rawQuery("SELECT  * FROM " + ItemDatabaseHelper.REGISTER_TABLE_NAME +
                " WHERE " + ItemDatabaseHelper.REGISTER_COLUMN_NAME + " =?", new String[] {name});
    }

    public int getExpirationOpenFromRegister(String name){
        Cursor cursor = getReadableDatabase().rawQuery("SELECT " + REGISTER_COLUMN_EXPIRES_OPEN +
                " FROM " + REGISTER_TABLE_NAME + " WHERE " + REGISTER_COLUMN_NAME + " =?", new String[] {name});

        if (cursor.getCount() == 0){
            return -1;
        } else {
            cursor.moveToFirst();
            return cursor.getInt(cursor.getColumnIndexOrThrow(REGISTER_COLUMN_EXPIRES_OPEN));
        }
    }

    public Cursor getContainerListFromDB(){
        return getReadableDatabase().rawQuery("SELECT * FROM " + CONTAINER_TABLE_NAME, null);
    }

    public void addContainerToDB(String name, String type){
        ContentValues cw = new ContentValues();
        cw.put(CONTAINER_COLUMN_NAME, name);
        cw.put(CONTAINER_COLUMN_TYPE, type);
        getWritableDatabase().insert(CONTAINER_TABLE_NAME, null, cw);
        cw.clear();
    }

    public void removeContainer(String ID) {
        // Delete container
        getWritableDatabase().delete(CONTAINER_TABLE_NAME, _ID + "=?", new String[]{ID});

        // Delete food in container
        getWritableDatabase().delete(TABLE_NAME, FOOD_FRIDGE_ID + " =?", new String[] {ID});

        //Log.i("FRIDGELOG", "Container with ID: " + ID + " has been removed");
    }

    public void updateExpirationDate(long expirationDate, String id){
        ContentValues cont = new ContentValues();
        //Log.i("CHANGE", "Expire MILI: " + expirationDate);
        cont.put(EXPIRE_DATE, expirationDate);
        cont.put(DATE_ADDED,  System.currentTimeMillis());
        getWritableDatabase().update(ItemDatabaseHelper.TABLE_NAME, cont, ItemDatabaseHelper._ID + "=?", new String[]{id});
    }

    public Cursor getExpiredFood(int daysBefore){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, daysBefore);
        long time = calendar.getTimeInMillis();

        Cursor cursor = getReadableDatabase().rawQuery("SELECT *, CASE WHEN " + OPEN + " THEN MIN(" + OPEN_DATE + " + " + EXPIRES_OPEN + ", " + EXPIRE_DATE + ") " +
                "ELSE " + EXPIRE_DATE + " END AS " + COMPACT_COLUMN_EXPIRE + " FROM " + TABLE_NAME +
                " WHERE " + COMPACT_COLUMN_EXPIRE + " <= " + time, null);
        return cursor;
    }

    public long getExpirationDateByName(String id){
        Cursor cursor = getReadableDatabase().rawQuery("SELECT " + EXPIRE_DATE + " FROM " + TABLE_NAME + " WHERE " + _ID + " =?", new String[] {id});
        if (cursor.getCount() == 0){
            return -1;
        } else {
            cursor.moveToFirst();
            return cursor.getLong(cursor.getColumnIndexOrThrow(EXPIRE_DATE));
        }
    }
}
