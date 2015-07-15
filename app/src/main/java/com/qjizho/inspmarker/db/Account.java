package com.qjizho.inspmarker.db;

import android.content.ContentValues;
import android.net.Uri;

/**
 * Created by qjizho on 15-7-13.
 */
public class Account {
    public final static String TABLE_NAME = "account";

    public static final String URI_AUTHORITY = "com.inspmarker.inspmark.accountprovider";

    public final static String COLUMN_ID = "_id";
    public final static String COLUMN_ACCOUNT_ID = "account_id";
    public final static String COLUMN_USERNAME = "username";
    public final static String COLUMN_FULL_NAME = "full_name";
    public final static String COLUMN_PROFILE_PICTURE = "profile_picture";
    public final static String COLUMN_BIO = "bio";
    public final static String COLUMN_ACCESS_TOKEN = "access_token";
    public final static String COLUMN_ACTIVED = "actived";

    public final static int NUM_COLUMN_ID = 0;
    public final static int NUM_ACCOUNT_ID = 1;
    public final static int NUM_USERNAME = 2;
    public final static int NUM_FULL_NAME =3;
    public final static int NUM_PROFILE_PICTURE = 4;
    public final static int NUM_BIO = 5;
    public final static int NUM_ACCESS_TOKEN = 6;
    public final static int NUM_ACTIVED = 7;

    public final static Uri CONTENT_URI_ACCOUNTS = Uri.parse("content://"
            + URI_AUTHORITY + "/accounts");
    public final static Uri CONTENT_URI_ACCOUNT = Uri.parse("content://"
            + URI_AUTHORITY + "/account");

    public long mId;
    public String mAccountId;
    public String mUserName;
    public String mFullName;
    public String mProfilePicture;
    public String mBio;
    public String mAccessToken;
    public int mActived;

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        if(mId != 0) {
            values.put(COLUMN_ID, mId);
        }
        values.put(COLUMN_ACCOUNT_ID, mAccountId);
        values.put(COLUMN_USERNAME, mUserName);
        values.put(COLUMN_FULL_NAME, mFullName);
        values.put(COLUMN_PROFILE_PICTURE, mProfilePicture);
        values.put(COLUMN_BIO, mBio);
        values.put(COLUMN_ACCESS_TOKEN, mAccessToken);
        values.put(COLUMN_ACTIVED, mActived);
        return values;
    }

}
