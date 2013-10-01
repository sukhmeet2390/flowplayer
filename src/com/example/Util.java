package com.example;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import com.example.database.ContentProvider.DbHelper;

/**
 * Created with IntelliJ IDEA.
 * User: sukhmeet
 * Date: 01/10/13
 * Time: 4:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class Util {

    public static ContentValues nameToContentValues(String name) {
        ContentValues values = new ContentValues();
        values.put(DbHelper.C_NAME, name);
        return values;
    }

    public static ContentValues dataToContentValues(String name, String imagePath, String parentImagePath,String childImagePath,
                                                     int x1, int y1, int x2, int y2) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(DbHelper.C_NAME, name);
        contentValues.put(DbHelper.C_IMAGE_URI, imagePath);
        contentValues.put(DbHelper.C_PARENT_URI, parentImagePath);

        contentValues.put(DbHelper.C_X1, x1);
        contentValues.put(DbHelper.C_Y1, y1);
        contentValues.put(DbHelper.C_X2, x2);
        contentValues.put(DbHelper.C_Y2, y2);
        contentValues.put(DbHelper.C_CHILD_URI, childImagePath);

        return contentValues;
    }

}
