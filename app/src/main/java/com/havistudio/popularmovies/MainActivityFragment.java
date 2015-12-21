package com.havistudio.popularmovies;

import android.content.Context;
import android.content.Intent;
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
import butterknife.OnItemClick;
import butterknife.OnItemSelected;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();

    private MainActivityMyAdapter mMovieAdapter;
    private Context mContext;
    @Bind(R.id.order_movies)
    Spinner spinner;
    @Bind(R.id.gridView_movies)
    GridView mGridView;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);
        // get the context
        mContext = getActivity();

        // spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.orderby_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Movie[] data = {new Movie("test", "test", "test","test", "test", "test")};
        List<Movie> moviesList = new ArrayList<Movie>(Arrays.asList(data));
        mMovieAdapter = new MainActivityMyAdapter(getActivity(), moviesList, R.layout.gridview_layout_main);
        mGridView.setAdapter(mMovieAdapter);

        MovieDBAPITask sat = new MovieDBAPITask(mMovieAdapter, mContext);
        sat.execute(spinnerValue((String) spinner.getSelectedItem()));
    }

    @OnItemSelected(R.id.order_movies)
    void changeOnSpinner(int position) {
        MovieDBAPITask sat = new MovieDBAPITask(mMovieAdapter, mContext);
        sat.execute(spinnerValue((String) spinner.getItemAtPosition(position)));
    }

    @OnItemClick(R.id.gridView_movies)
    void clickOnGridViewItem(int position) {
        Log.i("itemclick", "itemposition: " + position);
        Movie movie = (Movie) mMovieAdapter.getItem(position);
        Log.i(LOG_TAG, mMovieAdapter.getItem(position).toString());
        Intent intent = new Intent(mContext, MovieActivity.class);
        intent.putExtra("movieId", movie.getId());
        intent.putExtra("movieTitle", movie.getTitle());
        intent.putExtra("movieImage", movie.getImage());
        intent.putExtra("movieOverview", movie.getOverview());
        intent.putExtra("movieReleaseDate", movie.getReleaseDate());
        intent.putExtra("movieAverageVote", movie.getVoteAverage());

        startActivity(intent);
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
