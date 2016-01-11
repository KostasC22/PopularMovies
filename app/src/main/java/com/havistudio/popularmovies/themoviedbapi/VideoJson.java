package com.havistudio.popularmovies.themoviedbapi;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kostas on 31/12/2015.
 */
public class VideoJson {

    @SerializedName("id")
    private long id;
    @SerializedName("results")
    private List<Video> mVideos = new ArrayList<Video>();

    public VideoJson(){}

    public List<Video> getmVideos(){
        return mVideos;
    }

    @Override
    public String toString() {
        return "VideoJson{" +
                "id=" + id +
                ", mVideos=" + mVideos +
                '}';
    }
}
