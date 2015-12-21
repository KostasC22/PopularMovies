package com.havistudio.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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

        try {

            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority("api.themoviedb.org")
                    .appendPath("3")
                    .appendPath("discover")
                    .appendPath("movie")
                    .appendQueryParameter("api_key", BuildConfig.MOVIE_DB_ORG_API_KEY)
                    .appendQueryParameter("sort_by", params[0]);
            String myUrl = builder.build().toString();
            URL url = new URL(myUrl);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;

            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            moviesJsonStr = buffer.toString();
            result = getMoviesDataFromJSON(moviesJsonStr);

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Error ", e);
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Error ", e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        return result;
    }

    @Override
    protected void onPostExecute(List<Movie> movies) {
        super.onPostExecute(movies);

        if (movies != null) {
            if (movies.size() == 0) {
                mMovieAdapter.removeAll();
                Toast.makeText(mContext, "No artist found with this name", Toast.LENGTH_LONG).show();
            } else {
                mMovieAdapter.updateResults(movies);
            }
        }
    }

    public List<Movie> getMoviesDataFromJSON(String input) throws JSONException {
        List<Movie> result = new ArrayList<Movie>();

        JSONObject moviesJSON = new JSONObject(input);
        Log.i("JSON", input);
        JSONArray itemsJSONArray = moviesJSON.getJSONArray("results");
        String imageSize = "w342";

        for (int i = 0; i < itemsJSONArray.length(); i++) {
            JSONObject movieItemJSON = itemsJSONArray.getJSONObject(i);
            String movieId = "" + movieItemJSON.getLong("id");
            String movieTitle = movieItemJSON.getString("title");
            String movieImage = movieItemJSON.getString("poster_path");
            String movieOverview = movieItemJSON.getString("overview");
            String movieReleaseDate = movieItemJSON.getString("release_date");
            String movieAverageRate = ""+movieItemJSON.getDouble("vote_average");
            String fullPathImage = "http://image.tmdb.org/t/p/" + imageSize + movieImage;
            result.add(new Movie(movieId, movieTitle, fullPathImage, movieOverview, movieReleaseDate, movieAverageRate));
        }

        return result;
    }
}