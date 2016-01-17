package com.havistudio.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;

import com.havistudio.popularmovies.db.Contract;
import com.havistudio.popularmovies.db.MyDbHelper;

/**
 * Created by kostas on 17/01/2016.
 */
public class MakeFavoriteAsyncTask extends AsyncTask<Void, Void, Void> {

    private String tempText = "Unfavorite";
    private Context mContext;
    private String movieId;
    private Button mButton;
    private int mFavorite;

    public MakeFavoriteAsyncTask(Context mContext, String movieId, Button mButton, int mFavorite){
        this.mContext = mContext;
        this.movieId = movieId;
        this.mButton = mButton;
        this.mFavorite = mFavorite;
    }

    @Override
    protected Void doInBackground(Void... params) {
        Cursor favoriteCursor = mContext.getContentResolver().query(
                Contract.FavoritiesEntry.buildFavoritiesUri(Long.parseLong(movieId)),
                new String[]{
                        Contract.FavoritiesEntry._ID,
                        Contract.FavoritiesEntry.COLUMN_SELECTED,
                        Contract.FavoritiesEntry.COLUMN_MOVIEDB_ID
                },
                Contract.FavoritiesEntry.COLUMN_MOVIEDB_ID + " = ?",
                new String[]{movieId},
                null);

        if (favoriteCursor.moveToFirst()) {
            if(mFavorite == 0){
                mFavorite = 1;
            }else{
                mFavorite = 0;
                tempText = "Favorite";
            }
            MyDbHelper dbHelper = new MyDbHelper(mContext);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues myValues = new ContentValues();
            myValues.put(Contract.FavoritiesEntry.COLUMN_SELECTED, mFavorite);
            db.update(Contract.FavoritiesEntry.TABLE_NAME, myValues, Contract.FavoritiesEntry.COLUMN_MOVIEDB_ID + "= ?", new String[]{movieId});
        }else{
            MyDbHelper dbHelper = new MyDbHelper(mContext);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues myValues = new ContentValues();
            myValues.put(Contract.FavoritiesEntry.COLUMN_SELECTED, 1);
            myValues.put(Contract.FavoritiesEntry.COLUMN_MOVIEDB_ID, movieId);
            db.insert(Contract.FavoritiesEntry.TABLE_NAME, null, myValues);
        }

        return null;
    }

    protected void onPostExecute(Void result) {
        mButton.setText(tempText);
        mButton.setEnabled(true);
    }

}

