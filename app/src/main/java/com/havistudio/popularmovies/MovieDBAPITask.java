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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Call;
import retrofit.CallAdapter;
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

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        MyApiRetrofit service = retrofit.create(MyApiRetrofit.class);

        Map<String,String> temp = new HashMap<String,String>();
        temp.put("api_key", BuildConfig.MOVIE_DB_ORG_API_KEY);
        temp.put("sort_by", params[0]);

        Call<MovieJSON> call = service.getMovies(temp);
        try {
            Response<MovieJSON> tempo = call.execute();
            result = tempo.body().getmMovies();
        } catch (IOException e) {
            e.printStackTrace();
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
                if(mMovieAdapter != null){
                    mMovieAdapter.updateResults(movies);
                }
            }
        }
    }

}