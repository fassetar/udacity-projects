package com.anthonyfassett.com.apps.udacityandroidapp;

import android.os.Parcel;
import android.os.Parcelable;
import kaaes.spotify.webapi.android.models.Artist;

public class SearchArtistModel implements Parcelable {

    public String name;
    public String href;
    public String id;
    public String albumImageUrl;

    public SearchArtistModel(Artist artist) {
        this.name = artist.name;
        this.href = artist.href;
        this.id = artist.id;
        //I used at the model rather than the adapter.
        this.albumImageUrl = ImageHelper.getPreferredImageUrl(artist.images);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public SearchArtistModel createFromParcel(Parcel parcel) {
            return new SearchArtistModel(parcel);
        }

        public SearchArtistModel[] newArray(int size) {
            return new SearchArtistModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public SearchArtistModel(Parcel parcel) {
        this.name = parcel.readString();
        this.id = parcel.readString();
        this.href = parcel.readString();
        this.albumImageUrl = parcel.readString();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.name);
        parcel.writeString(this.id);
        parcel.writeString(this.href);
        parcel.writeString(this.albumImageUrl);
    }
}