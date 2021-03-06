package com.havistudio.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.havistudio.popularmovies.themoviedbapi.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kostas on 16/12/2015.
 */
public class MainActivityMyAdapter extends BaseAdapter {

    private Context context;
    private List<Movie> data = null;
    private int layoutResourceId;
    private String imageSize = "w342";

    public MainActivityMyAdapter(Context context, List<Movie> data, int layoutResourceId) {
        this.context = context;
        this.data = data;
        this.layoutResourceId = layoutResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        MovieHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new MovieHolder();
            holder.imgIcon = (ImageView) row.findViewById(R.id.grid_item_image);

            row.setTag(holder);
        } else {
            holder = (MovieHolder) row.getTag();
        }

        Movie movie = data.get(position);

        Picasso.with(context).load(ImageUtil.makeImageFullPath(movie.getImage(), "w342")).placeholder(R.mipmap.ic_launcher).error(R.mipmap.symbols_warning).fit().into(holder.imgIcon);

        return row;
    }

    static class MovieHolder {
        ImageView imgIcon;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void updateResults(List<Movie> results) {
        data = results;
        //Triggers the list update
        notifyDataSetChanged();
    }

    public void updateResults(ArrayList<Movie> results) {
        data = results;
        //Triggers the list update
        notifyDataSetChanged();
    }

    public void removeAll(){
        for(int i=0; i<data.size(); i++){
            data.remove(i);
        }
    }

    public List<Movie> getData(){
        return data;
    }
}
