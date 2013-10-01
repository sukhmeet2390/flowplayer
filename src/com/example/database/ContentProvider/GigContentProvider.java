package com.example.database.ContentProvider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

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

    final static int COMMENTS = 1;
    final static int DATA = 2;

    SQLiteDatabase sqLiteDatabase;
    DbHelper dbHelper;

    private final static UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTH, DbHelper.TABLE_NAME, COMMENTS);
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
            case COMMENTS:
                id = sqLiteDatabase.insert(DbHelper.TABLE_NAME, null, contentValues);
                if (id != -1) {
                    uri = Uri.withAppendedPath(uri, Long.toString(id));
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                break;
            case DATA:
                id = sqLiteDatabase.insert(DbHelper.TABLE_DATA, null, contentValues);
                if (id != -1) {
                    uri = Uri.withAppendedPath(uri, Long.toString(id));
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                break;
            default:
                throw new SQLException("Failed to insert row into " + uri);
        }
        return uri;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
        sqLiteDatabase = dbHelper.getReadableDatabase();
        switch (uriMatcher.match(uri)) {
            case COMMENTS:
                cursor = sqLiteDatabase.query(DbHelper.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case DATA:
                cursor = sqLiteDatabase.query(DbHelper.TABLE_DATA, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new SQLException("Failed to insert row into " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }


    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }

}
