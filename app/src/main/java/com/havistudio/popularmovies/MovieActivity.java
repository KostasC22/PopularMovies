package com.havistudio.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

public class MovieActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.

            Bundle arguments = new Bundle();
            Log.i("onCreate", getIntent().getExtras().get("movie").toString());
//            Log.i("onCreate2", getIntent().getBundleExtra("movie").toString());
            //arguments.putParcelable(MovieActivityFragment.MOVIE_FRAGMENT, getIntent().getBundleExtra("movie"));
            Parcelable temp = (Parcelable)getIntent().getExtras().get("movie");
            Log.i("onCreate2",temp.toString());
            arguments.putParcelable(MovieActivityFragment.MOVIE_FRAGMENT, (Parcelable)getIntent().getExtras().get("movie"));

            MovieActivityFragment fragment = new MovieActivityFragment();
            fragment.setArguments(arguments);

        }
        // Get intent to extract the bundle information
        Intent intent = getIntent();
        // Toolbar of the Activity
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(intent.getStringExtra("movieTitle"));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
