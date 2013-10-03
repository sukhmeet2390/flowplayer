package com.example.activity;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import com.example.database.ContentProvider.DbHelper;
import com.example.database.ContentProvider.GigContentProvider;
import com.example.model.HotSpotRectangle;

import java.util.ArrayList;

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

    public String findGigNameById(long id, Context context) {
        String selection = DbHelper.C_ID + " = " + (id) + " ";
        String sortOrder = null;
        String[] projection = {DbHelper.C_ID, DbHelper.C_NAME};

        Cursor cursor = context.getContentResolver().query(GigContentProvider.NAME_URI, projection, selection, null, sortOrder);

        if (cursor != null) {
            cursor.moveToFirst();
            return (String) cursor.getString(1).toString();
        }
        return null;
    }

    public Cursor queryGigByName(String query, Context context) {
        String selection = DbHelper.C_NAME + " LIKE ? ";
        String[] selectionArgs = new String[]{"%" + query + "%"};

        Cursor cursor = context.getContentResolver().query(GigContentProvider.NAME_URI, null, selection, selectionArgs, null);
        if (cursor.moveToFirst()) {
            return cursor;
        }
        return null;
    }

    public Uri findFirstImageByGigName(String name, Context context) {
        String[] projection = new String[]{DbHelper.C_IMAGE_URI};
        String selection = DbHelper.C_NAME + " = '" + name + "' and " + DbHelper.C_PARENT_URI + " = 'null'";
        Cursor cursor = context.getContentResolver().query(GigContentProvider.DATA_URI, projection, selection, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            return (Uri.parse(cursor.getString(0)));
        }
        return null;

    }
    public ArrayList<HotSpotRectangle> getHotSpotsOfImageByImageUri(Uri imageUri, String name, Context context) {
        String[] projection = new String[]{DbHelper.C_X1, DbHelper.C_Y1, DbHelper.C_X2, DbHelper.C_Y2, DbHelper.C_CHILD_URI};
        String selection = DbHelper.C_IMAGE_URI + "  = '"+imageUri+"' and "+DbHelper.C_NAME+ " = '"+name+"'";

        Cursor cursor = context.getContentResolver().query(GigContentProvider.DATA_URI, projection,selection, null, null);
        ArrayList<HotSpotRectangle> hotSpotRectangleArrayList = new ArrayList<HotSpotRectangle>();

        if (cursor.moveToFirst()) {
            do {
                HotSpotRectangle hr = new HotSpotRectangle(Float.parseFloat(cursor.getString(0)), Float.parseFloat(cursor.getString(1)),
                        Float.parseFloat(cursor.getString(2)), Float.parseFloat(cursor.getString(3)), Uri.parse(cursor.getString(4)));
                hotSpotRectangleArrayList.add(hr);
            } while (cursor.moveToNext());
            return hotSpotRectangleArrayList;

        }
        return null;
    }
}