package com.anthonyfassett.com.apps.udacityandroidapp;

import android.os.Parcel;
import android.os.Parcelable;

import kaaes.spotify.webapi.android.models.Track;

public class TopTrackModel implements Parcelable {

    public String name;
    public String album;
    public String href;
    public String id;
    public String albumImageUrl;
    public String previewUrl;
    public String artistName;

    public TopTrackModel(Track track) {
        this.name = track.name;
        this.href = track.href;
        this.album = track.album.name;
        this.previewUrl = track.preview_url;
        this.id = track.id;
        this.albumImageUrl = ImageHelper.getPreferredImageUrl(track.album.images);
        this.artistName = track.artists.get(0).name;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public TopTrackModel createFromParcel(Parcel parcel) {
            return new TopTrackModel(parcel);
        }
        public TopTrackModel[] newArray(int size) {
            return new TopTrackModel[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    public TopTrackModel(Parcel parcel) {
        this.name = parcel.readString();
        this.id = parcel.readString();
        this.href = parcel.readString();
        this.album = parcel.readString();
        this.albumImageUrl = parcel.readString();
        this.previewUrl = parcel.readString();
        this.artistName = parcel.readString();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.name);
        parcel.writeString(this.id);
        parcel.writeString(this.href);
        parcel.writeString(this.album);
        parcel.writeString(this.albumImageUrl);
        parcel.writeString(this.previewUrl);
        parcel.writeString(this.artistName);
    }
}