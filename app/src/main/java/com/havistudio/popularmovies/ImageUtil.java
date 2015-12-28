package com.havistudio.popularmovies;

/**
 * Created by kostas on 28/12/2015.
 */
public class ImageUtil {

    public static String makeImageFullPath(String imageURL,String imageSize){
        return "http://image.tmdb.org/t/p/" + imageSize + imageURL;
    }
}
