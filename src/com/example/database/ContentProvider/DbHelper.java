package com.example.database.ContentProvider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created with IntelliJ IDEA.
 * User: sukhmeet
 * Date: 18/09/13
 * Time: 4:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class DbHelper extends SQLiteOpenHelper {
    private String TAG = "DBHELPER";
    public static int VERSION = 1;

    public static String DB_NAME = "player.db";
    public static String TABLE_NAME = "GIG_NAME_TABLE";
    public static String TABLE_DATA = "GIG_DATA_TABLE";

    public static String C_ID = "_id";
    public static String C_NAME = "name";
    private final String createNameDB = String.format("create table if not exists  %s ( %s integer primary key autoincrement,"
            +" %s text )", TABLE_NAME, C_ID, C_NAME);


    public static String C_IMAGE_URI = "image_uri";
    public static String C_PARENT_URI = "parent_uri";
    public static String C_CHILD_URI = "child_image";
    public static String C_X1 = "x1";
    public static String C_Y1 = "y1";
    public static String C_X2 = "x2";
    public static String C_Y2 = "y2";

    private final String createDataDB = String.format("create table if not exists %s ( %s integer primary key autoincrement not null , %s text " +
                                        ", %s text , %s text , %s text , %s int, %s int, %s int, %s int )"
            , TABLE_DATA, C_ID, C_NAME, C_IMAGE_URI, C_PARENT_URI, C_CHILD_URI,  C_X1, C_Y1, C_X2, C_Y2);


    public DbHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
        Log.d(TAG, "In create DbHelper  "+ DB_NAME);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        Log.d(TAG, "onCreate database " + createNameDB);
        database.execSQL(createNameDB);
        Log.d(TAG, "onCreate database " + createDataDB);
        database.execSQL(createDataDB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.d(TAG, "On db update from " + oldVersion + " to " + newVersion);
        database.execSQL("Drop table  if exists " + TABLE_NAME);
        database.execSQL("Drop table if exists "+ TABLE_DATA);
        onCreate(database);

    }
}
