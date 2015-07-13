package com.qjizho.inspmarker.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.qjizho.inspmarker.db.Account;

/**
 * Created by qjizho on 15-7-13.
 */
public class DBHelper extends SQLiteOpenHelper{
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "insp.db";
    public DBHelper(Context context) {
        this(context, DB_NAME, null, DB_VERSION);
    }
    public DBHelper(Context context, String name, int version) {
        this(context, name, null, version);
    }

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                        int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + Account.TABLE_NAME + " ("
                + Account.COLUMN_ID + " INTEGER PRIMARY KEY, "
                + Account.COLUMN_ACCOUNT_ID + " TEXT, " + Account.COLUMN_USERNAME
                + " TEXT, " + Account.COLUMN_FULL_NAME + " TEXT, "
                + Account.COLUMN_PROFILE_PICTURE + " TEXT, " + Account.COLUMN_BIO
                + " TEXT, " + Account.COLUMN_ACCESS_TOKEN + " TEXT, "
                + Account.COLUMN_ACTIVED+ " INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Account.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
