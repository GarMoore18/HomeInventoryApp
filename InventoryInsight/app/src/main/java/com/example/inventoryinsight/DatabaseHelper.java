package com.example.inventoryinsight;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "inventorydb";
    public static final String ITEM_TABLE = "item_info";
    public static final String ITEM_ID = "id";
    public static final String ITEM_BARCODE = "barcode";
    public static final String ITEM_NAME = "name";
    public static final String ITEM_IMAGE = "image";

    public static final String LOCA_TABLE = "possible_locations";
    public static final String LOCA_ID = "id";
    public static final String LOCA_NAME = "name";

    public static final String COMBINE_TABLE = "combined_info";
    public static final String COMBINE_ID = "id";
    public static final String COMBINE_ITEM = "item_id";
    public static final String COMBINE_LOCA = "location_id";
    public static final String COMBINE_USER = "user_id";
    public static final String COMBINE_QUAN = "quantity";
    public static final String COMBINE_MOD = "last_modified";

    public static final String USER_TABLE = "user_access";
    public static final String USER_ID = "id";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String ADMIN = "admin";

    private static final int DB_VERSION = 2;

    //Constructor
    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + ITEM_TABLE + "("
                + ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ITEM_BARCODE + " CHAR,"
                + ITEM_NAME + " CHAR,"
                + ITEM_IMAGE + " BLOB)");
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + LOCA_TABLE + "("
                + LOCA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + LOCA_NAME + " VARCHAR)");
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + COMBINE_TABLE + "("
                + COMBINE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COMBINE_ITEM + " INTEGER,"
                + COMBINE_LOCA + " INTEGER,"
                + COMBINE_USER + " INTEGER,"
                + COMBINE_QUAN + " INTEGER,"
                + COMBINE_MOD + " TIMESTAMP,"
                + " FOREIGN KEY ("+COMBINE_ITEM+") REFERENCES "+ ITEM_TABLE + "("+ITEM_ID+"),"
                + " FOREIGN KEY ("+COMBINE_LOCA+") REFERENCES "+ LOCA_TABLE + "("+LOCA_ID+"),"
                + " FOREIGN KEY ("+COMBINE_USER+") REFERENCES "+ USER_TABLE + "("+USER_ID+"))");
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + USER_TABLE + "("
                + USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + USERNAME + " VARCHAR,"
                + PASSWORD + " VARCHAR,"
                + ADMIN + " BIT)");
    }

    public static int getDbVersion() {
        return DB_VERSION;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //context.deleteDatabase(DATABASE_NAME)
        if (newVersion != oldVersion) {
            //Log.d("NEW_VERSION", String.valueOf(newVersion));
            //Log.d("OLD_VERSION", String.valueOf(oldVersion));
            db.execSQL("DROP TABLE IF EXISTS " + ITEM_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + LOCA_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + COMBINE_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
            onCreate(db);
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Log.d("NEW_VERSION", String.valueOf(newVersion));
        //Log.d("OLD_VERSION", String.valueOf(oldVersion));
        if (newVersion != oldVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + ITEM_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + LOCA_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + COMBINE_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
            onCreate(db);
        }
    }

    public void addItem(String name, String barcode, String image) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues params = new ContentValues();
        params.put(ITEM_NAME, name);
        params.put(ITEM_BARCODE, barcode);
        params.put(ITEM_IMAGE, image);

        long id = db.insert(ITEM_TABLE, null, params);
        db.close();

        //Log.d("Testing addItem", "New record inserted in table items: " + id);
    }

    public void addLocation(String name) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues params = new ContentValues();
        params.put(LOCA_NAME, name);

        long id = db.insert(LOCA_TABLE, null, params);
        db.close();

        //Log.d("Testing addLocation", "New record inserted in table locations: " + id);
    }

    public void addCombined(int iid, int lid, int uid, int quantity, String time) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues params = new ContentValues();
        params.put(COMBINE_ITEM, iid);
        params.put(COMBINE_LOCA, lid);
        params.put(COMBINE_USER, uid);
        params.put(COMBINE_QUAN, quantity);
        params.put(COMBINE_MOD, time);

        long id = db.insert(COMBINE_TABLE, null, params);
        db.close();

        //Log.d("Testing addCombined", "New record inserted in table combined: " + id);
    }

    public void addUser(String username, String password, int admin) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues params = new ContentValues();
        params.put(USERNAME, username);
        params.put(PASSWORD, password);
        params.put(ADMIN, admin);

        long id = db.insert(USER_TABLE, null, params);
        db.close();

        //Log.d("Testing addUser", "New record inserted in table user: " + id);
    }
}
