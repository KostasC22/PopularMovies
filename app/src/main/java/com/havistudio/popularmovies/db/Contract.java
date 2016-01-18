package com.havistudio.popularmovies.db;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;
import android.util.Log;


/**
 * Created by kostas on 02/01/2016.
 */
public class Contract {

    public static final String CONTENT_AUTHORITY = "com.havistudio.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_FAVORITES = "favorites";

    public static long normalizeDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }

    public static final class FavoritiesEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITES;

        public static final String TABLE_NAME = "favorities";
        public static final String COLUMN_SELECTED = "selected";
        public static final String COLUMN_MOVIEDB_ID = "moviedb_id";
        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_AVERAGE_RATING = "average_rating";
        public static final String COLUMN_RELEASE_DATE = "release_date";

        public static Uri buildFavoritiesUri(long id) {
            return CONTENT_URI.buildUpon().appendPath("" + id).build();
        }

        public static long getMovieId(Uri uri) {
            String dateString = uri.getLastPathSegment();
            Log.i("FavoritiesEntry", ""+dateString);
            if (null != dateString && dateString.length() > 0)
                return Long.parseLong(dateString);
            else
                return 0;
        }
    }
}
