package com.havistudio.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.havistudio.popularmovies.themoviedbapi.Movie;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.Callback{

    private static final String MOVIEACTIVITYFRAGMENT_TAG = "MAFTAG";
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (findViewById(R.id.fragment_detail_movie) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_detail_movie, new MovieActivityFragment(), MOVIEACTIVITYFRAGMENT_TAG)
                        .commit();
            }
        }else{
            mTwoPane = false;
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(Movie tempMovie) {
        Log.i("MA_onItemSelected", tempMovie.toString());
        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putParcelable(MovieActivityFragment.MOVIE_FRAGMENT, tempMovie);

            MovieActivityFragment fragment = new MovieActivityFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_detail_movie, fragment, MOVIEACTIVITYFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, MovieActivity.class)
                    .putExtra("movie", tempMovie);
            intent.putExtra("movieId", tempMovie.getId());
            intent.putExtra("movieTitle", tempMovie.getTitle());
            intent.putExtra("movieImage", tempMovie.getImage());
            intent.putExtra("movieOverview", tempMovie.getOverview());
            intent.putExtra("movieReleaseDate", tempMovie.getReleaseDate());
            intent.putExtra("movieAverageVote", tempMovie.getVoteAverage());
            startActivity(intent);
        }
    }
}
