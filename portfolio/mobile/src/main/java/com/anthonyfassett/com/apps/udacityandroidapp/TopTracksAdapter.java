package com.anthonyfassett.com.apps.udacityandroidapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class TopTracksAdapter extends ArrayAdapter<TopTrackModel> {

    private static final int LAYOUT = R.layout.list_item_track;
    private LayoutInflater inflater;

    public TopTracksAdapter(Context context) {
        super(context, LAYOUT, new ArrayList<TopTrackModel>());
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TopTrackModel track = this.getItem(position);
        TopTrackViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(LAYOUT, parent, false);
            holder = new TopTrackViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (TopTrackViewHolder) convertView.getTag();
        }

        holder.albumTitle.setText(track.album);
        holder.trackTitle.setText(track.name);

        if (track.albumImageUrl != null) {
            Picasso.with(getContext()).load(track.albumImageUrl).into(holder.albumImage);
        } else {
            holder.albumImage.setImageResource(R.drawable.music);
        }

        return convertView;
    }

    public class TopTrackViewHolder {

        public TextView trackTitle;
        public TextView albumTitle;
        public ImageView albumImage;

        public TopTrackViewHolder(View convertView) {
            this.trackTitle = (TextView) convertView.findViewById(R.id.trackTitle);
            this.albumTitle = (TextView) convertView.findViewById(R.id.albumTitle);
            this.albumImage = (ImageView) convertView.findViewById(R.id.albumImage);
        }
    }
}
