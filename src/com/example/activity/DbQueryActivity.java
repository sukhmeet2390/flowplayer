package com.example.activity;

import android.app.Activity;
import android.content.ContentValues;
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
    DbHelper dbHelper;

    public DbQueryActivity(Context context) {
        this.dbHelper = new DbHelper(context);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public boolean updateChildImage(String imagePath, String parentImage, String gigName, Context context) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbHelper.C_CHILD_URI, imagePath);
        String selection = DbHelper.C_IMAGE_URI + " = '" + parentImage + "' and " + DbHelper.C_NAME + " = '" + gigName + "' and " + DbHelper.C_CHILD_URI + " = 'null'";
        if (!parentImage.equals("null")) context.getContentResolver().update(GigContentProvider.DATA_URI, contentValues, selection, null);
        return true;
    }

    public String findGigNameById(long id, Context context) {
        String selection = DbHelper.C_ID + " = " + (id) + " ";
        String sortOrder = null;
        String[] projection = {DbHelper.C_ID, DbHelper.C_NAME};

        Cursor cursor = context.getContentResolver().query(GigContentProvider.NAME_URI, projection, selection, null, sortOrder);

        if (cursor != null) {
            cursor.moveToFirst();
            return cursor.getString(1).toString();
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
        String selection = DbHelper.C_IMAGE_URI + "  = '" + imageUri + "' and " + DbHelper.C_NAME + " = '" + name + "'";

        Cursor cursor = context.getContentResolver().query(GigContentProvider.DATA_URI, projection, selection, null, null);
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

    public String deleteGigNameAndData(long id, Context context) {
        String where = DbHelper.C_ID + " = " + id;
        String name = findGigNameById(id, context);
        context.getContentResolver().delete(GigContentProvider.NAME_URI, where, null);
        where = DbHelper.C_NAME + "  = '" + name + "'";
        context.getContentResolver().delete(GigContentProvider.DATA_URI, where, null);
        return name;
    }

    public void insertName(String inputText, Context context){
        context.getContentResolver().insert(GigContentProvider.NAME_URI, com.example.Util.nameToContentValues(inputText));
    }

}