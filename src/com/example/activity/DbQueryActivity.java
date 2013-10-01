package com.example.activity;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import com.example.database.ContentProvider.DbHelper;

/**
 * Created with IntelliJ IDEA.
 * User: sukhmeet
 * Date: 01/10/13
 * Time: 5:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class DbQueryActivity extends Activity {
    private String TAG = "QUERIES";
    SQLiteDatabase database;
    DbHelper dbHelper;

    public DbQueryActivity(Context context) {
        this.dbHelper = new DbHelper(context);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    public boolean updateChildImage(String imagePath, String parentImage, String gigName) {
        Cursor cursor;
        database = dbHelper.getWritableDatabase();
        String select = "update " + DbHelper.TABLE_DATA + " set child_image = '" + imagePath + "' where image_uri = '" + parentImage + "' and name ='" + gigName + "' and child_image = 'null' ";
        Log.d(TAG, select);
        if(!parentImage.equals("null")) database.execSQL(select);
        database.close();
        return true;
    }
}