package com.havistudio.popularmovies;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kostas on 25/12/2015.
 */
public class MovieJSON {

    @SerializedName("page")
    private int page;
    @SerializedName("results")
    private List<Movie> mMovies = new ArrayList<Movie>();

    public MovieJSON(){}

    public List<Movie> getmMovies(){
        return mMovies;
    }
}
