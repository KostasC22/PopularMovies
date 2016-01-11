package com.havistudio.popularmovies.themoviedbapi;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.havistudio.popularmovies.BuildConfig;
import com.havistudio.popularmovies.ReviewAdapter;

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
 * Created by kostas on 31/12/2015.
 */
public class ReviewDBAPITask extends AsyncTask<String, Void, List<Review>>{

    private final String LOG_TAG = ReviewDBAPITask.class.getSimpleName();

    private ReviewAdapter mReviewAdapter;
    private Context mContext;

    public ReviewDBAPITask(ReviewAdapter mMovieAdapter, Context mContext){
        this.mReviewAdapter = mMovieAdapter;
        this.mContext = mContext;
    }

    @Override
    protected List<Review> doInBackground(String... params) {
        List<Review> result = new ArrayList<Review>();

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

        Call<ReviewJson> call = service.getReviews(Long.parseLong(params[0]), temp);
        try {
            Response<ReviewJson> tempo = call.execute();
            result = tempo.body().getmReviews();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    protected void onPostExecute(List<Review> reviews) {
        super.onPostExecute(reviews);
        Log.i(LOG_TAG, "" + reviews.size());
        if (reviews != null) {
            if (reviews.size() == 0) {
                mReviewAdapter.removeAll();
                Toast.makeText(mContext, "No artist found with this name", Toast.LENGTH_LONG).show();
            } else {
                if(mReviewAdapter != null){
                    mReviewAdapter.updateResults(reviews);
                }
            }
        }
    }
}
