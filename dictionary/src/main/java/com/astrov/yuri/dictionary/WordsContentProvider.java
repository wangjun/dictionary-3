package com.astrov.yuri.dictionary;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.sql.SQLException;

public class WordsContentProvider extends ContentProvider {
    public static final Uri CONTENT_URI = Uri.parse("com.astrov.yuri.wordsprovider");
    //public static final Uri CONTACT_CONTENT_URI = Uri.parse("content://"
    //        + CONTENT_URI + "/" + "words");

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "worddb";
    public static final String DATABASE_TABLE = "word";


    public static final String KEY_ROWID = "_id";  // Android requires exactly this key name
    public static final int INDEX_ROWID = 0;
    public static final String KEY_BASELANG = "baselang";
    public static final int INDEX_BASELANG = 1;
    public static final String KEY_TOLANG = "tolang";
    public static final int INDEX_TOLANG = 2;
    public static final String KEY_BASEWORD = "baseword";
    public static final int INDEX_BASEWORD = 3;
    public static final String KEY_TOWORD = "toword";
    public static final int INDEX_TOWORD = 4;

    static final String DATABASE_CREATE =
            "create table " + DATABASE_TABLE + " (" +
                    KEY_ROWID + " integer primary key autoincrement, " +
                    KEY_BASELANG + " text not null, " +
                    KEY_TOLANG + " text not null," +
                    KEY_BASEWORD + " text not null," +
                    KEY_TOWORD + " text not null " +
                    ");";

    DBHelper dbHelper;
    SQLiteDatabase db;

    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(CONTENT_URI.toString(), DATABASE_TABLE, 1);
        uriMatcher.addURI(CONTENT_URI.toString(), DATABASE_TABLE+"/#", 2);
    }

    public WordsContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case 1:
                count = db.delete(DATABASE_TABLE, selection, selectionArgs);
                break;
            case 2:
                String id = uri.getPathSegments().get(1);
                count = db.delete( DATABASE_TABLE, KEY_ROWID +  " = " + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" +
                                selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        long rowID = db.insert(DATABASE_TABLE, "", values);
        /**
         * If record is added successfully
         */
        if (rowID > 0)
        {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        dbHelper = new DBHelper(getContext());
        db = dbHelper.getWritableDatabase();
        return (db == null)? false:true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(DATABASE_TABLE);
        switch (uriMatcher.match(uri)) {
            case 1:
                break;
            case 2:
                qb.appendWhere( KEY_ROWID + "=" + uri.getPathSegments().get(1));
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        if (TextUtils.isEmpty(sortOrder)) {
            sortOrder = KEY_BASEWORD + " ASC";
        }
        Cursor c = qb.query(db, projection, selection, selectionArgs,
                                null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        int count = 0;

        switch (uriMatcher.match(uri)){
            case 1:
                count = db.update(DATABASE_TABLE, values,
                        selection, selectionArgs);
                break;
            case 2:
                count = db.update(DATABASE_TABLE, values, KEY_ROWID +
                        " = " + uri.getPathSegments().get(1) +
                        (!TextUtils.isEmpty(selection) ? " AND (" +
                                selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri );
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    //--
    private static class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
            ContentValues cv = new ContentValues();
            for (int i = 1; i <= 3; i++) {
                //cv.put(CONTACT_NAME, "name " + i);
                //cv.put(CONTACT_EMAIL, "email " + i);
                //db.insert(CONTACT_TABLE, null, cv);
                ;
            }
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}
