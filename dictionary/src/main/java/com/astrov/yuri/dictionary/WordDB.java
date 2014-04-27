package com.astrov.yuri.dictionary;

/**
 * Created by yrain on 26.04.14.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class WordDB {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "worddb";
    public static final String DATABASE_TABLE = "word";

    // Field names -- use the KEY_XXX constants here and in
    // client code, so it's all consistent and checked at compile-time.

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


    public static final String[] KEYS_ALL =
        { WordDB.KEY_ROWID, WordDB.KEY_BASELANG, WordDB.KEY_TOLANG, WordDB.KEY_BASEWORD,
         WordDB.KEY_TOWORD};

    private Context mContext;
    private SQLiteDatabase mDatabase;
    private WordDBHelper mHelper;

    /** Construct DB for this activity context. */
    public WordDB(Context context) {
        mContext = context;
    }

    /** Opens up a connection to the database. Do this before any operations. */
    public void open() throws SQLException {
        mHelper = new WordDBHelper(mContext);
        mDatabase = mHelper.getWritableDatabase();
    }

    /** Closes the database connection. Operations are not valid after this. */
    public void close() {
        mHelper.close();
        mHelper = null;
        mDatabase = null;
    }


    /**
     Creates and inserts a new row using the given values.
     Returns the rowid of the new row, or -1 on error.
     todo: values should not include a rowid I assume.
     */
    public long createRow(ContentValues values) {
        return mDatabase.insert(DATABASE_TABLE, null, values);
    }

    /**
     Updates the given rowid with the given values.
     Returns true if there was a change (i.e. the rowid was valid).
     */
    public boolean updateRow(long rowId, ContentValues values) {
        return mDatabase.update(DATABASE_TABLE, values,
                WordDB.KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     Deletes the given rowid.
     Returns true if any rows were deleted (i.e. the id was valid).
     */
    public boolean deleteRow(long rowId) {
        return mDatabase.delete(DATABASE_TABLE,
                WordDB.KEY_ROWID + "=" + rowId, null) > 0;
    }


    /** Returns a cursor for all the rows. Caller should close or manage the cursor. */
    public List<String> queryAll() {
        Cursor cursor = mDatabase.query(DATABASE_TABLE,
                KEYS_ALL,  // i.e. return all 4 columns
                null, null, null, null,
                WordDB.KEY_BASELANG + " ASC"  // order-by, "DESC" for descending
        );
        cursor.moveToFirst();
        List<String> result = new ArrayList<String>();
        while (!cursor.isAfterLast()) {
            String word  = cursor.getString(WordDB.INDEX_TOWORD);
            result.add(word);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return result;
        // Could pass for third arg to filter in effect:
        // TodoDatabaseHelper.KEY_STATE + "=0"

        // query() is general purpose, here we show the most common usage.
    }

    /** Returns a cursor for the given row id. Caller should close or manage the cursor. */
    public Cursor query(long rowId) throws SQLException {
        Cursor cursor = mDatabase.query(true, DATABASE_TABLE,
                KEYS_ALL,
                KEY_ROWID + "=" + rowId,  // select the one row we care about
                null, null, null, null, null);

        // cursor starts before first -- move it to the row itself.
        cursor.moveToFirst();
        return cursor;
    }
    /* Returns a cursor for the given from_lang,to_lang, baseWord */
    public List<String> queryWords (String from_lang, String to_lang, String baseWord) throws SQLException {
        String[] tableColumns = new String[] { KEY_TOWORD, };
        //String whereClause = "column1 = ? OR column1 = ?";
        Cursor cursor = mDatabase.query(DATABASE_TABLE, WordDB.KEYS_ALL,
                KEY_BASELANG +"="+from_lang + " " +
                KEY_TOLANG +"="+ to_lang  + " " +
                 KEY_BASEWORD +"="+ baseWord,
                null, null, null, null, null);

        cursor.moveToFirst();
        List<String> result = new ArrayList<String>();
        while (!cursor.isAfterLast()) {
            String word  = cursor.getString(WordDB.INDEX_TOWORD);
            result.add(word);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return result;
    }

    /** Creates a ContentValues hash for our data. Pass in to create/update. */
    public ContentValues createContentValues(String from_lang, String to_lang, String baseWord,
                                             String toWord) {
        ContentValues values = new ContentValues();
        values.put(WordDB.KEY_BASELANG, from_lang);
        values.put(WordDB.KEY_TOLANG, to_lang);
        values.put(WordDB.KEY_BASEWORD, baseWord);
        values.put(WordDB.KEY_TOWORD, toWord);
        return values;
    }

    public long createWordRow(String from_lang, String to_lang, String baseWord,
                              String toWord) {
        return createRow(createContentValues(from_lang, to_lang, baseWord,
                toWord));
    }



// Helper for database open, create, upgrade.
// Here written as a private inner class to TodoDB.
private class WordDBHelper extends SQLiteOpenHelper {
    // SQL text to create table (basically just string or integer)
    private static final String DATABASE_CREATE =
            "create table " + WordDB.DATABASE_TABLE + " (" +
                    WordDB.KEY_ROWID + " integer primary key autoincrement, " +
                    WordDB.KEY_BASELANG + " text not null, " +
                    WordDB.KEY_TOLANG + " text not null," +
                    WordDB.KEY_BASEWORD + " text not null," +
                    WordDB.KEY_TOWORD + " text not null " +
                    ");";

    // SQLITE does not have a complex type system, so although "done" is a boolean
    // to the app, here we store it as an integer with (0 = false)


    public WordDBHelper(Context context) {
        super(context, WordDB.DATABASE_NAME, null, WordDB.DATABASE_VERSION);
    }

    /** Creates the initial (empty) database. */
    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }


    /** Called at version upgrade time, in case we want to change/migrate
     the database structure. Here we just do nothing. */
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        // we do nothing for this case
    }
}
}