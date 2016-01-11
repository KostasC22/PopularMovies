package com.havistudio.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.havistudio.popularmovies.themoviedbapi.Review;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kostas on 02/01/2016.
 */
public class ReviewAdapter extends BaseAdapter {

    private Context context;
    private List<Review> data = null;
    private int layoutResourceId;

    public ReviewAdapter(Context context, List<Review> data, int layoutResourceId) {
        this.context = context;
        this.data = data;
        this.layoutResourceId = layoutResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        ReviewHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new ReviewHolder();
            holder.author = (TextView) row.findViewById(R.id.textView_comment_author);
            holder.content = (TextView) row.findViewById(R.id.textView_comment_content);

            row.setTag(holder);
        } else {
            holder = (ReviewHolder) row.getTag();
        }

        Review review = data.get(position);
        Log.i("ReviewAdapter", review.toString());
        holder.author.setText(review.getAuthor());
        holder.content.setText(review.getContent());

        return row;
    }

    static class ReviewHolder {
        TextView author;
        TextView content;
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

    public void updateResults(List<Review> results) {
        data = results;
        //Triggers the list update
        notifyDataSetChanged();
    }

    public void updateResults(ArrayList<Review> results) {
        data = results;
        //Triggers the list update
        notifyDataSetChanged();
    }

    public void removeAll(){
        for(int i=0; i<data.size(); i++){
            data.remove(i);
        }
    }

    public List<Review> getData(){
        return data;
    }
}
