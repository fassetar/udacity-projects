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

public class SearchArtistAdapter extends ArrayAdapter<SearchArtistModel> {

    private static final int LAYOUT = R.layout.list_item_artist;
    private LayoutInflater inflater;

    public SearchArtistAdapter(Context context) {
        super(context, LAYOUT, new ArrayList<SearchArtistModel>());
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SearchArtistModel artist = this.getItem(position);
        SearchArtistViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(LAYOUT, parent, false);
            holder = new SearchArtistViewHolder(convertView);
            holder.albumTitle = (TextView) convertView.findViewById(R.id.artistText);
            holder.albumImage = (ImageView) convertView.findViewById(R.id.artistImage);
            convertView.setTag(holder);
        } else {
            holder = (SearchArtistViewHolder) convertView.getTag();
        }

        holder.albumTitle.setText(artist.name);

        if (artist.albumImageUrl != null) {
            Picasso.with(this.getContext()).load(artist.albumImageUrl).into(holder.albumImage);
        } else {
            holder.albumImage.setImageResource(R.drawable.music);
        }

        return convertView;
    }

    public class SearchArtistViewHolder {

        public TextView trackTitle;
        public TextView albumTitle;
        public ImageView albumImage;

        public SearchArtistViewHolder(View convertView) {
            this.trackTitle = (TextView) convertView.findViewById(R.id.trackTitle);
            this.albumTitle = (TextView) convertView.findViewById(R.id.albumTitle);
            this.albumImage = (ImageView) convertView.findViewById(R.id.albumImage);
        }
    }
}