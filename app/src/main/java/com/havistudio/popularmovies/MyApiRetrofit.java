package com.havistudio.popularmovies;

import java.util.List;
import java.util.Map;

import retrofit.Call;
import retrofit.Callback;
import retrofit.http.FieldMap;
import retrofit.http.GET;
import retrofit.http.QueryMap;

/**
 * Created by kostas on 24/12/2015.
 */
public interface MyApiRetrofit {

    @GET("3/discover/movie")
    Call<MovieJSON> getMovies(@QueryMap Map<String, String> options);

}
