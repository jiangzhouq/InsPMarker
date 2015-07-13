package com.qjizho.inspmarker.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.qjizho.inspmarker.db.Account;

/**
 * Created by qjizho on 15-7-13.
 */
public class AccountDBProvider extends ContentProvider{
    public static final String DB_NAME = "insp.db";

    public static final String URI_AUTHORITY = "com.qjizho.insp";

    private static final int ACCOUNT =1;
    private static final int ACCOUNTS = 2;
    public static final String URI_MIME_ACCOUNT
            = "vnd.android.cursor.item/com.qjizho.insp";
    public static final String URI_MIME_ACCOUNTS
            = "vnd.android.cursor.dir/com.qjizho.insp";

    private static final String TAG = AccountDBProvider.class.getSimpleName();

    private static UriMatcher mUriMatcher;
    private DBHelper dbHelper;

    static {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mUriMatcher.addURI(URI_AUTHORITY, "account/#", 1);
        mUriMatcher.addURI(URI_AUTHORITY, "accounts", 2);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DBHelper(this.getContext(), DB_NAME, 1);
        return true;
    }

    @Override
    public String getType(Uri uri) {
        Log.e("memo", "=======================================uri:" + uri);
        switch (mUriMatcher.match(uri)) {
            case ACCOUNT:
                return URI_MIME_ACCOUNT;
            case ACCOUNTS:
                return URI_MIME_ACCOUNTS;
            default:
                Log.e(TAG, "Unknown URI:" + uri);
                throw new IllegalArgumentException("Unknown URI:" + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId = db.insert(Account.TABLE_NAME, Account.COLUMN_ID, values);
        if (rowId > 0) {
            Uri rowUri = ContentUris.withAppendedId(uri, rowId);
            getContext().getContentResolver().notifyChange(rowUri, null);
            return rowUri;
        }
//		db.close();
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (mUriMatcher.match(uri)) {
            case ACCOUNTS:
                count = db.delete(Account.TABLE_NAME, selection, selectionArgs);
                break;
            case ACCOUNT:
                long id = ContentUris.parseId(uri);
                String where = Account.COLUMN_ID + "=" + id;
                if(selection != null && !selection.equals("")){
                    where = where + " and " + selection;
                }
                count = db.delete(Account.TABLE_NAME, where, selectionArgs);
                break;
            default:
                Log.e(TAG, "Unknown URI:" + uri);
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
//		db.close();
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int count;
        switch (mUriMatcher.match(uri)) {
            case ACCOUNTS:
                count = db.update(Account.TABLE_NAME, values, selection, selectionArgs);
                break;
            case ACCOUNT:
                long id = ContentUris.parseId(uri);
                String where = Account.COLUMN_ID + "=" + id;
                if(selection != null && !selection.equals("")){
                    where = where + " and " + selection;
                }
                count = db.update(Account.TABLE_NAME, values, where, selectionArgs);
                break;
            default:
                Log.e(TAG, "Unknown URI:" + uri);
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
//		db.close();
        getContext().getContentResolver().notifyChange(uri,null);
        return count;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        switch (mUriMatcher.match(uri)) {
            case ACCOUNTS:
                return db.query(Account.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
            case ACCOUNT:
                long id = ContentUris.parseId(uri);
                String where = Account.COLUMN_ID + "=" + id;
                if(selection != null && !selection.equals("")){
                    where = where + " and " + selection;
                }
                return db.query(Account.TABLE_NAME,projection,where,selectionArgs,null,null,sortOrder);
            default:
                Log.e(TAG, "Unknown URI:" + uri);
                throw new IllegalArgumentException("Unknown URI:" + uri);
        }
    }
}
