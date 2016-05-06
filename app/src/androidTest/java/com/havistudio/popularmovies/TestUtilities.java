package com.havistudio.popularmovies;

import android.content.ContentValues;
import android.database.Cursor;
import android.test.AndroidTestCase;

import com.havistudio.popularmovies.db.Contract;

import java.util.Map;
import java.util.Set;

/**
 * Created by kostas on 04/01/2016.
 */
public class TestUtilities extends AndroidTestCase {

    static ContentValues createTestFavoriteValues() {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(Contract.FavoritiesEntry.COLUMN_SELECTED, 1);
        testValues.put(Contract.FavoritiesEntry.COLUMN_MOVIEDB_ID, 286217);

        return testValues;
    }


    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }
}
