package com.havistudio.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.havistudio.popularmovies.db.Contract;
import com.havistudio.popularmovies.db.MyDbHelper;
import com.havistudio.popularmovies.themoviedbapi.Movie;
import com.havistudio.popularmovies.themoviedbapi.Review;
import com.havistudio.popularmovies.themoviedbapi.ReviewDBAPITask;
import com.havistudio.popularmovies.themoviedbapi.Video;
import com.havistudio.popularmovies.themoviedbapi.VideoDBAPITask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int FAVORITES_LOADER = 0;

    private static final String[] FAVORITES_COLUMNS = {
            Contract.FavoritiesEntry._ID,
            Contract.FavoritiesEntry.COLUMN_SELECTED,
            Contract.FavoritiesEntry.COLUMN_MOVIEDB_ID
    };

    public static final int COL_FAVORITE_ID = 0;
    public static final int COL_FAVORITE_SELECTED = 1;
    public static final int COL_FAVORITE_MOVIEDB_ID = 2;

    private int tempSelectedFavorite = 0;

    @Bind(R.id.imageview_poster_img)
    ImageView posterImageView;
    @Bind(R.id.textview_overview_text)
    TextView textviewOverviewText;
    @Bind(R.id.textview_release_date)
    TextView textviewReleaseDate;
    @Bind(R.id.textview_average_rating)
    TextView textviewAverageRating;
    @Bind(R.id.is_favorite)
    Button buttonFavorite;
    @Bind(R.id.listview_user_comments)
    ListView mCommentsListView;
    @Bind(R.id.listview_trailer_buttons)
    ListView mButtonsListView;
    // context
    private final String LOG_TAG = MovieActivityFragment.class.getSimpleName();
    private VideoAdapter mVideoAdapter;
    private ReviewAdapter mReviewAdapter;
    private Context mContext;
    private String movieId;

    public MovieActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie, container, false);
        ButterKnife.bind(this, rootView);
        // get the context
        mContext = getActivity();
        // Get the intent information
        Intent intent = getActivity().getIntent();
        movieId = intent.getLongExtra("movieId", 0) + "";
        Log.i(LOG_TAG, movieId);


        Picasso.with(getActivity()).load(ImageUtil.makeImageFullPath(intent.getStringExtra("movieImage"), "w342")).into(posterImageView);
        textviewOverviewText.setText(intent.getStringExtra("movieOverview"));
        textviewReleaseDate.setText("Release Date: " + intent.getStringExtra("movieReleaseDate"));
        textviewAverageRating.setText("Average Rating: " + intent.getStringExtra("movieAverageVote"));

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(FAVORITES_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);

        List<Video> videosList;
        List<Review> reviewsList;
        // Trailers
        Video[] data = {new Video("0", "test", "test", "test")};
        videosList = new ArrayList<Video>(Arrays.asList(data));
        VideoDBAPITask videoTask = new VideoDBAPITask(mVideoAdapter, mContext);
        videoTask.execute(movieId);
        mVideoAdapter = new VideoAdapter(getActivity(), videosList, R.layout.listview_trailer_buttons);
        mButtonsListView.setAdapter(mVideoAdapter);
        // Comments
        Review[] dataReview = {new Review("test", "test", "test", "test")};
        reviewsList = new ArrayList<Review>(Arrays.asList(dataReview));
        ReviewDBAPITask sat = new ReviewDBAPITask(mReviewAdapter, mContext);
        sat.execute(movieId);
        mReviewAdapter = new ReviewAdapter(getActivity(), reviewsList, R.layout.listview_users_comments);
        mCommentsListView.setAdapter(mReviewAdapter);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        Uri favoritesUri = Contract.FavoritiesEntry.buildFavoritiesUri(Long.parseLong(movieId));
        CursorLoader temp = new CursorLoader(getActivity(), favoritesUri, FAVORITES_COLUMNS, null, null, null);
        Log.i("onCreateLoader", "movieId:" + movieId);
        Log.i("onCreateLoader", "favoritesUri:" + favoritesUri);
        Log.i("onCreateLoader", "CursorLoader:" + temp.toString());

        return new CursorLoader(getActivity(),
                favoritesUri,
                FAVORITES_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        Log.i("onLoadFinished", cursorLoader.toString());
        Log.i("onLoadFinished",cursor.toString());
        if (cursor != null && cursor.moveToFirst()) {
            int selectedFavorite = cursor.getInt(COL_FAVORITE_SELECTED);
            Log.i("onLoadFinished","selectedFavorite:"+selectedFavorite);
            if(selectedFavorite == 1){
                tempSelectedFavorite = 1;
                buttonFavorite.setText("Unfavorite");
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    @OnClick(R.id.is_favorite)
    public void makeFavorite() {
        Log.i("ButtonAT", "Button Pressed!");
        buttonFavorite.setEnabled(false);
        MakeFavoriteAsyncTask mfat = new MakeFavoriteAsyncTask();
        mfat.execute();
    }

    private class MakeFavoriteAsyncTask extends AsyncTask<Void, Void, Void> {

        private String tempText = "Unfavorite";

        @Override
        protected Void doInBackground(Void... params) {
            Log.i("doInBackground", "Start");
            Log.i("doInBackground",Contract.FavoritiesEntry.buildFavoritiesUri(Long.parseLong(movieId)).toString());
            Cursor favoriteCursor = getContext().getContentResolver().query(
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
                Log.i("doInBackground", "Id already exists!");
                if(tempSelectedFavorite == 0){
                    tempSelectedFavorite = 1;
                }else{
                    tempSelectedFavorite = 0;
                    tempText = "Favorite";
                }
                MyDbHelper dbHelper = new MyDbHelper(mContext);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues myValues = new ContentValues();
                myValues.put(Contract.FavoritiesEntry.COLUMN_SELECTED, tempSelectedFavorite);
                db.update(Contract.FavoritiesEntry.TABLE_NAME, myValues, Contract.FavoritiesEntry.COLUMN_MOVIEDB_ID + "= ?", new String[]{movieId});
            }else{
                Log.i("doInBackground","Id it doesn't exist!");
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
            buttonFavorite.setText(tempText);
            buttonFavorite.setEnabled(true);
        }

    }
}
