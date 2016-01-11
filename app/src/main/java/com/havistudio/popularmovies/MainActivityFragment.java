package com.havistudio.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;

import com.havistudio.popularmovies.themoviedbapi.Movie;
import com.havistudio.popularmovies.themoviedbapi.MovieDBAPITask;

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

    public String MOVIE_KEY = "movies";

    @Bind(R.id.order_movies)
    Spinner spinner;
    @Bind(R.id.gridView_movies)
    GridView mGridView;

    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private MainActivityMyAdapter mMovieAdapter;
    private Context mContext;

    public MainActivityFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(MOVIE_KEY, (ArrayList<Movie>) mMovieAdapter.getData());
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

        List<Movie> moviesList;

        if(savedInstanceState == null || !savedInstanceState.containsKey(MOVIE_KEY)) {
            Movie[] data = {new Movie(0, "test", "test","test", "test", "test")};
            moviesList = new ArrayList<Movie>(Arrays.asList(data));
            MovieDBAPITask sat = new MovieDBAPITask(mMovieAdapter, mContext);
            sat.execute(spinnerValue((String) spinner.getSelectedItem()));
        }else{
            moviesList = savedInstanceState.getParcelableArrayList(MOVIE_KEY);
        }
        mMovieAdapter = new MainActivityMyAdapter(getActivity(), moviesList, R.layout.gridview_layout_main);
        mGridView.setAdapter(mMovieAdapter);
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
