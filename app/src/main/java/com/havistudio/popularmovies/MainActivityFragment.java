package com.havistudio.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;
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
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private MainActivityMyAdapter mMovieAdapter;
    private Context mContext;

    private GridView mGridView;

    @Bind(R.id.order_movies)
    private Spinner spinner;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        // get the context
        mContext = getActivity();

        // spinner
        spinner = (Spinner) rootView.findViewById(R.id.order_movies);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.orderby_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        mGridView = (GridView) rootView.findViewById(R.id.gridView_movies);

        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        Movie[] data = {new Movie("test", "test", "test")};
        List<Movie> spotifyArtist = new ArrayList<Movie>(Arrays.asList(data));

        mMovieAdapter = new MainActivityMyAdapter(getActivity(), spotifyArtist, R.layout.gridview_layout_main);

        MovieDBAPITask sat = new MovieDBAPITask();
        sat.execute(spinnerValue((String) spinner.getSelectedItem()));

        mGridView.setAdapter(mMovieAdapter);
    }

    public class MovieDBAPITask extends AsyncTask<String, Void, List<Movie>> {

        private final String LOG_TAG = MovieDBAPITask.class.getSimpleName();

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

            if (movies.size() == 0) {
                mMovieAdapter.removeAll();
                Toast.makeText(mContext, "No artist found with this name", Toast.LENGTH_LONG).show();
            } else {
                mMovieAdapter.updateResults(movies);
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
                String movieId = ""+ movieItemJSON.getLong("id");
                String movieTitle = movieItemJSON.getString("title");
                String movieImage = movieItemJSON.getString("poster_path");
                String fullPathImage = "http://image.tmdb.org/t/p/"+imageSize+movieImage;
                result.add(new Movie(movieId, movieTitle, fullPathImage));
            }

            return result;
        }
    }

    private static String spinnerValue(String value) {
        String result;
        if (value.equals("Most Popular")) {
            result = "popularity.desc";
        } else if (value.equals("Highest Rated")) {
            result = "vote_average.desc";
        } else {
            result = "popularity.desc";
        }
        return result;
    }
}
