package com.example.database.ContentProvider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * Created with IntelliJ IDEA.
 * User: sukhmeet
 * Date: 18/09/13
 * Time: 8:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class GigContentProvider extends ContentProvider {

    private static final String AUTH = "com.example.database.ContentProvider.GigNameContentProvider"; // path to content provider
    public static final Uri NAME_URI = Uri.parse("content://" + AUTH + "/" + DbHelper.TABLE_NAME);  // address to my db
    public static final Uri DATA_URI = Uri.parse("content://" + AUTH + "/" + DbHelper.TABLE_DATA);

    final static int NAME = 1;
    final static int DATA = 2;

    SQLiteDatabase sqLiteDatabase;
    DbHelper dbHelper;
    private String TAG = "GIGCONTENTPROVIDER";

    private final static UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTH, DbHelper.TABLE_NAME, NAME);
        uriMatcher.addURI(AUTH, DbHelper.TABLE_DATA, DATA);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        sqLiteDatabase = dbHelper.getWritableDatabase();
        long id = -1;
        switch (uriMatcher.match(uri)) {
            case NAME:
                id = sqLiteDatabase.insert(DbHelper.TABLE_NAME, null, contentValues);
                if (id != -1) {
                    uri = Uri.withAppendedPath(uri, Long.toString(id));
                }
                break;
            case DATA:
                id = sqLiteDatabase.insert(DbHelper.TABLE_DATA, null, contentValues);
                if (id != -1) {
                    uri = Uri.withAppendedPath(uri, Long.toString(id));
                }
                break;
            default:
                throw new SQLException("Failed to insert row into " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        Log.i(TAG, "Uri publish in insert "+ uri);
        return uri;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
        sqLiteDatabase = dbHelper.getReadableDatabase();
        switch (uriMatcher.match(uri)) {
            case NAME:
                cursor = sqLiteDatabase.query(DbHelper.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case DATA:
                cursor = sqLiteDatabase.query(DbHelper.TABLE_DATA, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new SQLException("Failed to insert row into " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        Log.i(TAG, "Uri publish in query "+ uri);
        return cursor;
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int rowDeleted = -1;
        sqLiteDatabase = dbHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case NAME:
                rowDeleted = sqLiteDatabase.delete(DbHelper.TABLE_NAME, selection, selectionArgs);
                break;
            case DATA:
                rowDeleted = sqLiteDatabase.delete(DbHelper.TABLE_DATA, selection, selectionArgs);
                break;
            default:
                throw new SQLException("Failed to deleted row into uri " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        Log.i(TAG, "Uri publish in delete "+ uri);
        return rowDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        int rowsUpdated = 0;
        sqLiteDatabase = dbHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case NAME:
                rowsUpdated = sqLiteDatabase.update(DbHelper.TABLE_NAME, contentValues, selection, selectionArgs);
                break;
            case DATA:
                rowsUpdated = sqLiteDatabase.update(DbHelper.TABLE_DATA, contentValues, selection, selectionArgs);
                break;
            default:
                throw new SQLException("Failed to update row into URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        Log.i(TAG, "Uri publish in update "+ uri);
        return rowsUpdated;
    }

}
// LINK : http://www.techotopia.com/index.php/Implementing_an_Android_Content_Provider