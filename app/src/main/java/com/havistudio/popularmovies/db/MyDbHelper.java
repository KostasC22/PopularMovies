package com.havistudio.popularmovies.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.havistudio.popularmovies.db.Contract.FavoritiesEntry;
/**
 * Created by kostas on 02/01/2016.
 */
public class MyDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "popularmovies.db";

    public MyDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.i("MyDbHelper", "onCreate **************************************");
        final String SQL_CREATE_FAVORITIES_TABLE = "CREATE TABLE " + FavoritiesEntry.TABLE_NAME + " (" +
                FavoritiesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                FavoritiesEntry.COLUMN_SELECTED + " INTEGER NOT NULL, " +
                FavoritiesEntry.COLUMN_IMAGE + " INTEGER NOT NULL, " +
                FavoritiesEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                FavoritiesEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                FavoritiesEntry.COLUMN_AVERAGE_RATING + " TEXT NOT NULL, " +
                FavoritiesEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                FavoritiesEntry.COLUMN_MOVIEDB_ID + " INTEGER UNIQUE NOT NULL " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavoritiesEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
