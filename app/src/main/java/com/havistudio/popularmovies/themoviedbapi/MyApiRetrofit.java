package com.havistudio.popularmovies.themoviedbapi;

import java.util.Map;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.QueryMap;

/**
 * Created by kostas on 24/12/2015.
 */
public interface MyApiRetrofit {

    @GET("3/discover/movie")
    Call<MovieJson> getMovies(@QueryMap Map<String, String> options);


    @GET("3/movie/{id}/videos")
    Call<VideoJson> getVideos(@Path("id") long id, @QueryMap Map<String, String> options);

    @GET("3/movie/{id}/reviews")
    Call<ReviewJson> getReviews(@Path("id") long id, @QueryMap Map<String, String> options);
}
