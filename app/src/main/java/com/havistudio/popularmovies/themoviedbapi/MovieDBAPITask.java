package com.havistudio.popularmovies.themoviedbapi;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.havistudio.popularmovies.BuildConfig;
import com.havistudio.popularmovies.MainActivityMyAdapter;
import com.havistudio.popularmovies.db.Contract;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by kostas on 21/12/2015.
 */
public class MovieDBAPITask extends AsyncTask<String, Void, List<Movie>> {

    private final String LOG_TAG = MovieDBAPITask.class.getSimpleName();

    private MainActivityMyAdapter mMovieAdapter;
    private Context mContext;

    public MovieDBAPITask(MainActivityMyAdapter mMovieAdapter, Context mContext){
        this.mMovieAdapter = mMovieAdapter;
        this.mContext = mContext;
    }

    @Override
    protected List<Movie> doInBackground(String... params) {
        List<Movie> result = new ArrayList<Movie>();

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String moviesJsonStr;

        if (params.length == 0) {
            return null;
        }
        if(params[0].equals("favorites")){
            Log.i("cursor", Contract.FavoritiesEntry.CONTENT_URI.toString());
            Cursor favoriteCursor = mContext.getContentResolver().query(
                    Contract.FavoritiesEntry.CONTENT_URI,
                    new String[]{
                            Contract.FavoritiesEntry._ID,
                            Contract.FavoritiesEntry.COLUMN_SELECTED,
                            Contract.FavoritiesEntry.COLUMN_MOVIEDB_ID,
                            Contract.FavoritiesEntry.COLUMN_IMAGE,
                            Contract.FavoritiesEntry.COLUMN_OVERVIEW,
                            Contract.FavoritiesEntry.COLUMN_TITLE,
                            Contract.FavoritiesEntry.COLUMN_AVERAGE_RATING,
                            Contract.FavoritiesEntry.COLUMN_RELEASE_DATE
                    },
                    Contract.FavoritiesEntry.COLUMN_SELECTED + " = ?",
                    new String[]{"1"},
                    null);

            while (favoriteCursor.moveToNext()) {
                result.add(new Movie(favoriteCursor.getLong(2), favoriteCursor.getString(5), favoriteCursor.getString(3)
                        , favoriteCursor.getString(4), favoriteCursor.getString(6), favoriteCursor.getString(7)));
            }

        }else {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://api.themoviedb.org/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            MyApiRetrofit service = retrofit.create(MyApiRetrofit.class);

            Map<String, String> temp = new HashMap<String, String>();
            temp.put("api_key", BuildConfig.MOVIE_DB_ORG_API_KEY);
            temp.put("sort_by", params[0]);

            Call<MovieJson> call = service.getMovies(temp);
            try {
                Response<MovieJson> tempo = call.execute();
                result = tempo.body().getmMovies();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    protected void onPostExecute(List<Movie> movies) {
        super.onPostExecute(movies);

        if (movies != null) {
            if (movies.size() == 0) {
                if(mMovieAdapter != null) {
                    mMovieAdapter.removeAll();
                }
                Toast.makeText(mContext, "No artist found with this name", Toast.LENGTH_LONG).show();
            } else {
                if(mMovieAdapter != null){
                    mMovieAdapter.updateResults(movies);
                }
            }
        }
    }

}