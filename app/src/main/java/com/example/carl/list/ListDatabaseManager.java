package com.example.carl.list;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Carl on 10/11/2016.
 */

public class ListDatabaseManager
{
    public static final String KEY_ROWID = "_id";
    public static final String KEY_LISTNAME = "list_name";
    public static final String KEY_LIST_DESCRIPTION = "list_description";

    public static final String KEY_ITEMNAME = "item_name";
    public static final String KEY_LISTID = "list_id";

    private static final String DATABASE_NAME = "ListDatabase";
    private static final String DATABASE_TABLE_LIST = "List";
    private static final String DATABASE_TABLE_ITEM = "Item";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE_LIST =
            "create table " + DATABASE_TABLE_LIST +
                    "(" + KEY_ROWID + " integer primary key autoincrement, " +
                    KEY_LISTNAME + " text unique not null, " +
                    KEY_LIST_DESCRIPTION + " text);";

    private static final String DATABASE_CREATE_ITEM =
            "create table " + DATABASE_TABLE_ITEM +
                    "(" + KEY_ROWID + " integer primary key autoincrement, " +
                    KEY_ITEMNAME + " text, " +
                    KEY_LISTID + " integer not null, " +
                    "foreign key(" + KEY_LISTID + ") references List(" + KEY_ROWID + "));";

    private final Context context;
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public ListDatabaseManager(Context ctx)
    {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(DATABASE_CREATE_LIST);
            db.execSQL(DATABASE_CREATE_ITEM);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            // dB structure change..

        }
    }

    public ListDatabaseManager open() throws SQLException
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    public void close()
    {
        DBHelper.close();
    }

    public long insertList(String listName, String listDescription)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_LISTNAME, listName);
        initialValues.put(KEY_LIST_DESCRIPTION, listDescription);
        return db.insert(DATABASE_TABLE_LIST, null, initialValues);
    }

    public long insertItem(String itemName, long listId)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_ITEMNAME, itemName);
        initialValues.put(KEY_LISTID, listId);
        return db.insert(DATABASE_TABLE_ITEM, null, initialValues);
    }

    public boolean deleteList(long rowId)
    {
        return db.delete(DATABASE_TABLE_LIST, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public boolean deleteItem(long rowId)
    {
        return db.delete(DATABASE_TABLE_ITEM, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public Cursor getAllLists()
    {
        return db.query(DATABASE_TABLE_LIST, new String[]
                        {
                                KEY_ROWID,
                                KEY_LISTNAME,
                                KEY_LIST_DESCRIPTION
                        },
                null, null, null, null, null);
    }

    public Cursor getAllItems()
    {
        return db.query(DATABASE_TABLE_ITEM, new String[]
                        {
                                KEY_ROWID,
                                KEY_ITEMNAME,
                                KEY_LISTID
                        },
                null, null, null, null, null);
    }

    public Cursor getList(long rowId) throws SQLException
    {
        Cursor mCursor =
                db.query(true, DATABASE_TABLE_LIST, new String[]
                                {
                                        KEY_ROWID,
                                        KEY_LISTNAME,
                                        KEY_LIST_DESCRIPTION
                                },
                        KEY_ROWID + "=" + rowId,  null, null, null, null, null);

        if (mCursor != null)
        {
            mCursor.moveToFirst();
        }

        return mCursor;
    }

    public Cursor getItem(long rowId) throws SQLException
    {
        Cursor mCursor =
                db.query(true, DATABASE_TABLE_ITEM, new String[]
                                {
                                        KEY_ROWID,
                                        KEY_ITEMNAME
                                },
                        KEY_ROWID + "=" + rowId,  null, null, null, null, null);

        if (mCursor != null)
        {
            mCursor.moveToFirst();
        }

        return mCursor;
    }

    public boolean updateList(long rowId, String listName, String listDescription)
    {
        ContentValues args = new ContentValues();
        args.put(KEY_LISTNAME, listName);
        args.put(KEY_LIST_DESCRIPTION, listDescription);
        return db.update(DATABASE_TABLE_LIST, args, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public boolean updateItem(long rowId, String itemName)
    {
        ContentValues args = new ContentValues();
        args.put(KEY_ITEMNAME, itemName);
        return db.update(DATABASE_TABLE_ITEM, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
}
