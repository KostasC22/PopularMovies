package com.havistudio.popularmovies;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.havistudio.popularmovies.db.Contract;
import com.havistudio.popularmovies.db.MyDbHelper;

import java.util.HashSet;

/**
 * Created by kostas on 04/01/2016.
 */
public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    void deleteTheDatabase() {
        mContext.deleteDatabase(MyDbHelper.DATABASE_NAME);
    }

    public void setUp() {
        deleteTheDatabase();
    }

    public void testCreateDb() throws Throwable {

        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(Contract.FavoritiesEntry.TABLE_NAME);

        mContext.deleteDatabase(MyDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new MyDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        assertTrue("Error: Your database was created without both the location entry and weather entry tables",
                tableNameHashSet.isEmpty());

        c = db.rawQuery("PRAGMA table_info(" + Contract.FavoritiesEntry.TABLE_NAME + ")", null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> favoriteColumnHashSet = new HashSet<String>();
        favoriteColumnHashSet.add(Contract.FavoritiesEntry._ID);
        favoriteColumnHashSet.add(Contract.FavoritiesEntry.COLUMN_SELECTED);
        favoriteColumnHashSet.add(Contract.FavoritiesEntry.COLUMN_MOVIEDB_ID);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            favoriteColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required location entry columns",
                favoriteColumnHashSet.isEmpty());
        db.close();
    }

    public void testFavoritesTable() {
        insertFavorite();
    }

    public long insertFavorite() {

        MyDbHelper dbHelper = new MyDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createTestFavoriteValues();

        long favoriteRowId;
        favoriteRowId = db.insert(Contract.FavoritiesEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue(favoriteRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // Fourth Step: Query the database and receive a Cursor back
        // A cursor is your primary interface to the query results.
        Cursor cursor = db.query(
               Contract.FavoritiesEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        // Move the cursor to a valid database row and check to see if we got any records back
        // from the query
        assertTrue( "Error: No Records returned from location query", cursor.moveToFirst() );

        // Fifth Step: Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Error: Location Query Validation Failed",
                cursor, testValues);

        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse( "Error: More than one record returned from location query",
                cursor.moveToNext() );

        // Sixth Step: Close Cursor and Database
        cursor.close();
        db.close();
        return favoriteRowId;
    }
}
