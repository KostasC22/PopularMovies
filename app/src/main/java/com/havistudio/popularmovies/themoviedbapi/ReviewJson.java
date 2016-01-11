package com.havistudio.popularmovies.themoviedbapi;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kostas on 31/12/2015.
 */
public class ReviewJson {

    @SerializedName("id")
    private long id;
    @SerializedName("page")
    private long page;
    @SerializedName("results")
    private List<Review> mReviews = new ArrayList<Review>();
    @SerializedName("total_pages")
    private long totalPages;
    @SerializedName("total_results")
    private long totalResults;

    public ReviewJson(){}

    public List<Review> getmReviews(){
        return mReviews;
    }
}
