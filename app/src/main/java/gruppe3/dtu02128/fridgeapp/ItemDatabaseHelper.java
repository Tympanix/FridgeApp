package gruppe3.dtu02128.fridgeapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Morten on 18-Jun-15.
 */
public class ItemDatabaseHelper extends SQLiteOpenHelper {
    final static String NAME = "food_db";
    final static int VERSION = 1;

    // Food database
    final static String TABLE_NAME = "food";
    final static String FOOD_NAME = "name";
    final static String EXPIRES_OPEN = "openexpire";
    final static String EXPIRE_DATE = "dateexpire";
    final static String OPEN = "open";
    final private Context context;
    final static String _ID = "_id";
    final static String[] columns = { _ID, FOOD_NAME};//, EXPIRES_OPEN };

    // Foodregister database
    final static String REGISTER_TABLE_NAME = "register";
    final static String REGISTER_COLUMN_ID = "_id";
    final static String REGISTER_COLUMN_NAME = "name";
    final static String REGISTER_COLUMN_EXPIRES_OPEN = "openexpire";

    public ItemDatabaseHelper(Context context) {
        super(context, NAME, null, VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create food database
        db.execSQL("CREATE TABLE food (" + _ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FOOD_NAME + " TEXT NOT NULL, " + EXPIRES_OPEN + " INTEGER NOT NULL, " + EXPIRE_DATE + " BIGINT, " + OPEN + " INTEGER NOT NULL)");

        // Create register database
        db.execSQL("CREATE TABLE " + REGISTER_TABLE_NAME + " (" +
                REGISTER_COLUMN_ID + " TEXT PRIMARY KEY UNIQUE, " +
                REGISTER_COLUMN_NAME + " TEXT NOT NULL, " +
                REGISTER_COLUMN_EXPIRES_OPEN + " INTEGER NOT NULL" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void deleteDatabase() {
        context.deleteDatabase(NAME);
    }
}
