package com.havistudio.popularmovies.themoviedbapi;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.havistudio.popularmovies.BuildConfig;
import com.havistudio.popularmovies.VideoAdapter;

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
public class VideoDBAPITask extends AsyncTask<String, Void, List<Video>> {

    private final String LOG_TAG = VideoDBAPITask.class.getSimpleName();

    private VideoAdapter mVideoAdapter;
    private Context mContext;

    public VideoDBAPITask(VideoAdapter mMovieAdapter, Context mContext){
        this.mVideoAdapter = mMovieAdapter;
        this.mContext = mContext;
    }

    @Override
    protected List<Video> doInBackground(String... params) {
        List<Video> result = new ArrayList<Video>();

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
//                .client(httpClient)
                .build();
        MyApiRetrofit service = retrofit.create(MyApiRetrofit.class);


        Map<String,String> temp = new HashMap<String,String>();
        temp.put("api_key", BuildConfig.MOVIE_DB_ORG_API_KEY);

        Call<VideoJson> call = service.getVideos(Long.parseLong(params[0]),temp);
        try {
            Response<VideoJson> tempo = call.execute();
            result = tempo.body().getmVideos();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    protected void onPostExecute(List<Video> videos) {
        super.onPostExecute(videos);
        Log.i(LOG_TAG,""+videos.size());
        if (videos != null) {
            if (videos.size() == 0) {
                mVideoAdapter.removeAll();
                Toast.makeText(mContext, "No artist found with this name", Toast.LENGTH_LONG).show();
            } else {
                if(mVideoAdapter != null){
                    mVideoAdapter.updateResults(videos);
                }
            }
        }
    }
}
