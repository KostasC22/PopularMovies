package com.havistudio.popularmovies.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by kostas on 02/01/2016.
 */
public class MyContentProvider extends ContentProvider {

    private MyDbHelper mOpenHelper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static final SQLiteQueryBuilder sFavoriteByMovieIdQueryBuilder;

    static final int FAVORITES = 100;
    static final int FAVORITES_WITH_MOVIE_ID = 101;

    static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = Contract.CONTENT_AUTHORITY;

        matcher.addURI(authority, Contract.PATH_FAVORITES, FAVORITES);
        matcher.addURI(authority, Contract.PATH_FAVORITES + "/*", FAVORITES_WITH_MOVIE_ID);
        return matcher;
    }

    private static final String sMovieIdSelection = Contract.FavoritiesEntry.COLUMN_MOVIEDB_ID + " = ? ";

    static {
        sFavoriteByMovieIdQueryBuilder = new SQLiteQueryBuilder();
        sFavoriteByMovieIdQueryBuilder.setTables(Contract.FavoritiesEntry.TABLE_NAME);
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MyDbHelper(getContext());
        Log.i("MyContentProvider","Constructor ---------------------------------");
        return false;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case FAVORITES:
                return Contract.FavoritiesEntry.CONTENT_TYPE;
            case FAVORITES_WITH_MOVIE_ID:
                return Contract.FavoritiesEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor retCursor;
        int tempUriMatcher = sUriMatcher.match(uri);
        Log.i("MyContentProvider", "query method: "+tempUriMatcher);
        switch (tempUriMatcher) {
            case FAVORITES_WITH_MOVIE_ID:
                retCursor = getFavoriteByMovieId(uri, projection, sortOrder);
                break;
            case FAVORITES:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        Contract.FavoritiesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case FAVORITES:
                long _id = db.insert( Contract.FavoritiesEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri =  Contract.FavoritiesEntry.buildFavoritiesUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        if ( null == selection ) selection = "1";
        switch (match) {
            case FAVORITES:
                rowsDeleted = db.delete(Contract.FavoritiesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case FAVORITES:
                rowsUpdated = db.update(Contract.FavoritiesEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    private Cursor getFavoriteByMovieId(Uri uri, String[] projection, String sortOrder) {

        long mid = Contract.FavoritiesEntry.getMovieId(uri);
        Log.i("MyContentProvider", "getFavoriteByMovieId method: " + mid);
        Log.i("MyContentProvider", "getFavoriteByMovieId method: " + mOpenHelper.getReadableDatabase());
        String selection = sMovieIdSelection;
        String[] selectionArgs = new String[]{mid+""};



        return sFavoriteByMovieIdQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

    }
}
