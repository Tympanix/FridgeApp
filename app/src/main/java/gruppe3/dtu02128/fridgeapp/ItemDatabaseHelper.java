package gruppe3.dtu02128.fridgeapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Random;

/**
 * Created by Morten on 18-Jun-15.
 */
public class ItemDatabaseHelper extends SQLiteOpenHelper {
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

    Random r = new Random(55447347295858L);

    public ItemDatabaseHelper(Context context) {
        super(context, NAME, null, VERSION);
        this.context = context;
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
                OPEN + " BOOLEAN NOT NULL)");

        // Create register database
        db.execSQL("CREATE TABLE " + REGISTER_TABLE_NAME + " (" +
                REGISTER_COLUMN_NAME + " TEXT PRIMARY KEY UNIQUE, " +
                REGISTER_COLUMN_ID + " TEXT, " +
                REGISTER_COLUMN_EXPIRES_OPEN + " INTEGER NOT NULL" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void deleteDatabase() {
        context.deleteDatabase(NAME);
    }

    public Cursor getFoodList(String name){
        String query = "CASE WHEN " + OPEN + " THEN MIN("+ OPEN_DATE + " + " + EXPIRES_OPEN + ", " + EXPIRE_DATE + ") " +
                "ELSE " + EXPIRE_DATE + " END) AS " + COMPACT_COLUMN_EXPIRE;

        return getReadableDatabase().rawQuery("SELECT *, CASE WHEN " + OPEN + " THEN MIN("+ OPEN_DATE + " + " + EXPIRES_OPEN + ", " + EXPIRE_DATE + ") " +
                "ELSE " + EXPIRE_DATE + " END AS " + COMPACT_COLUMN_EXPIRE + " FROM " + TABLE_NAME +
                " WHERE " + FOOD_NAME + " =?", new String[] {name});

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
        getWritableDatabase().update(ItemDatabaseHelper.TABLE_NAME, cont, ItemDatabaseHelper._ID + "=?", new String[]{id});
    }

    public Cursor getAllFromDb() {
        return getWritableDatabase().rawQuery("SELECT  * FROM " + ItemDatabaseHelper.TABLE_NAME, null);
    }

    public Cursor getCompactListFromDb(){
        String query = "SELECT " + _ID + ", " + FOOD_NAME + ", " + EXPIRE_DATE + ", " + EXPIRES_OPEN + ", " + OPEN_DATE + ", " + OPEN + ", " + DATE_ADDED + ", " +
                "count(" + FOOD_NAME + ") AS " + COMPACT_COLUMN_NUMBER + ", " +
                "MIN(CASE WHEN " + OPEN + " THEN MIN(" + OPEN_DATE + " + " + EXPIRES_OPEN + ", " + EXPIRE_DATE + ") " +
                "ELSE " + EXPIRE_DATE + " END) AS "+ COMPACT_COLUMN_EXPIRE +
                " FROM " + TABLE_NAME + " GROUP BY " + FOOD_NAME + ";";
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

    public void insertTestToDB(String name, int openExpire, boolean open){
        ContentValues cw = new ContentValues();
        cw.put(FOOD_NAME, name);
        cw.put(EXPIRES_OPEN, openExpire * MILL_ONE_DAY);
        long time = System.currentTimeMillis() + (r.nextInt(30)+1) * 3600*24*1000L;
        cw.put(EXPIRE_DATE, time);
        cw.put(OPEN_DATE, System.currentTimeMillis());
        cw.put(DATE_ADDED, System.currentTimeMillis() - 2 * MILL_ONE_DAY);
        cw.put(OPEN, open);
        getWritableDatabase().insert(TABLE_NAME, null, cw);
        cw.clear();

        cw.put(REGISTER_COLUMN_NAME, name);
        cw.put(REGISTER_COLUMN_ID, 0);
        cw.put(REGISTER_COLUMN_EXPIRES_OPEN, openExpire);
        getWritableDatabase().insert(REGISTER_TABLE_NAME, null, cw);
        cw.clear();
    }

    public void insertItemToDb(String name, int expiresAfter, long expireDate){
        ContentValues cw = new ContentValues();
        cw.put(FOOD_NAME, name);
        cw.put(EXPIRES_OPEN, (expiresAfter * MILL_ONE_DAY));
        cw.put(EXPIRE_DATE, expireDate);
        cw.put(OPEN_DATE, System.currentTimeMillis());
        cw.put(DATE_ADDED, System.currentTimeMillis());
        cw.put(OPEN, false);
        getWritableDatabase().insert(TABLE_NAME, null, cw);
        cw.clear();
        cw.put(REGISTER_COLUMN_NAME, name);
        cw.put(REGISTER_COLUMN_ID, 1);
        cw.put(REGISTER_COLUMN_EXPIRES_OPEN,5);
        getWritableDatabase().insert(REGISTER_TABLE_NAME, null, cw);
        cw.clear();
    }

    public String[] getProductNames() {
        String query = "SELECT * "+ " FROM " + REGISTER_TABLE_NAME;
        Cursor c =  getWritableDatabase().rawQuery("SELECT  * FROM " + ItemDatabaseHelper.REGISTER_TABLE_NAME, null);
        String[] names = new String[c.getCount()];
        Log.i("test", "Found " + c.getCount() + " items");
        while (c.moveToNext()) {
            names[c.getPosition()] = c.getString(c.getColumnIndexOrThrow(REGISTER_COLUMN_NAME));
        }
        return names;
    }

    public Cursor getRegisterByName(String name) {
        return getWritableDatabase().rawQuery("SELECT  * FROM " + ItemDatabaseHelper.REGISTER_TABLE_NAME +
                " WHERE " + ItemDatabaseHelper.REGISTER_COLUMN_NAME + " =?", new String[] {name});
    }
}
