package com.havistudio.popularmovies.themoviedbapi;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kostas on 25/12/2015.
 */
public class MovieJson {

    @SerializedName("page")
    private int page;
    @SerializedName("results")
    private List<Movie> mMovies = new ArrayList<Movie>();

    public MovieJson(){}

    public List<Movie> getmMovies(){
        return mMovies;
    }
}
