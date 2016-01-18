package com.havistudio.popularmovies.themoviedbapi;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.havistudio.popularmovies.BuildConfig;
import com.havistudio.popularmovies.R;
import com.havistudio.popularmovies.ReviewAdapter;
import com.havistudio.popularmovies.VideoAdapter;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

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

    private ListView mButtonsListView;
    private VideoAdapter mVideoAdapter;
    private Context mContext;

    public VideoDBAPITask(VideoAdapter mMovieAdapter, Context mContext, ListView mButtonsListView) {
        this.mVideoAdapter = mMovieAdapter;
        this.mContext = mContext;
        this.mButtonsListView = mButtonsListView;
    }

    @Override
    protected List<Video> doInBackground(String... params) {
        List<Video> result = new ArrayList<Video>();

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String moviesJsonStr;

        if (params.length == 0) {
            return null;
        }

        //debug retrofit
//        OkHttpClient client = new OkHttpClient();
//        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//        client.interceptors().add(interceptor);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/")
                .addConverterFactory(GsonConverterFactory.create())
//                .client(client)
                .build();
        MyApiRetrofit service = retrofit.create(MyApiRetrofit.class);


        Map<String, String> temp = new HashMap<String, String>();
        temp.put("api_key", BuildConfig.MOVIE_DB_ORG_API_KEY);

        Call<VideoJson> call = service.getVideos(Long.parseLong(params[0]), temp);
        try {
            Response<VideoJson> tempo = call.execute();
            result = tempo.body().getmVideos();
            Log.i("getmVideos", result.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    protected void onPostExecute(List<Video> videos) {
        super.onPostExecute(videos);
        if (videos != null) {
            if (videos.size() == 0) {
                if(mVideoAdapter != null) {
                    mVideoAdapter.removeAll();
                }
                Toast.makeText(mContext, "No artist found with this name", Toast.LENGTH_LONG).show();
            } else {
                mVideoAdapter = new VideoAdapter(mContext, videos, R.layout.listview_trailer_buttons);
                mButtonsListView.setAdapter(mVideoAdapter);

            }
        }
    }
}
