package com.havistudio.popularmovies;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieActivityFragment extends Fragment {

    @Bind(R.id.imageview_poster_img)
    ImageView posterImageView;
    @Bind(R.id.textview_overview_text)
    TextView textviewOverviewText;
    @Bind(R.id.textview_release_date)
    TextView textviewReleaseDate;
    @Bind(R.id.textview_average_rating)
    TextView textviewAverageRating;

    public MovieActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie, container, false);
        ButterKnife.bind(this, rootView);
        // Get the intent information
        Intent intent = getActivity().getIntent();

        Picasso.with(getActivity()).load(intent.getStringExtra("movieImage")).into(posterImageView);
        textviewOverviewText.setText(intent.getStringExtra("movieOverview"));
        textviewReleaseDate.setText("Release Date: "+intent.getStringExtra("movieReleaseDate"));
        textviewAverageRating.setText("Average Rating: "+intent.getStringExtra("movieAverageVote"));

        return rootView;
    }
}
