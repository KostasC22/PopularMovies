package com.havistudio.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.havistudio.popularmovies.themoviedbapi.Video;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kostas on 02/01/2016.
 */
public class VideoAdapter extends BaseAdapter {

    private Context context;
    private List<Video> data = null;
    private int layoutResourceId;

    public VideoAdapter(Context context, List<Video> data, int layoutResourceId) {
        this.context = context;
        this.data = data;
        this.layoutResourceId = layoutResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        VideoHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new VideoHolder();
            holder.trailer = (Button) row.findViewById(R.id.trailer_button);

            row.setTag(holder);
        } else {
            holder = (VideoHolder) row.getTag();
        }

        final Video video = data.get(position);
        Log.i("VideoAdapter", video.toString());
        holder.trailer.setText("Trailer " + (position + 1) + " " + video.getSite());
        holder.trailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube://" + video.getKey()));
                context.startActivity(intent);
            }
        });

        return row;
    }

    static class VideoHolder {
        Button trailer;
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

    public void updateResults(List<Video> results) {
        data = results;
        notifyDataSetChanged();
    }

    public void removeAll() {
        for (int i = 0; i < data.size(); i++) {
            data.remove(i);
        }
    }

    public List<Video> getData() {
        return data;
    }
}
