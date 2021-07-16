package com.example.podpaper4;

import android.graphics.Bitmap;
import android.os.Parcelable;

import com.spotify.protocol.types.Album;
import com.spotify.protocol.types.Artist;
import com.spotify.protocol.types.ImageUri;
import com.spotify.protocol.types.Uri;
//import com.spotify.protocol.types.Uri;

import org.parceler.Parcel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Parcel(analyze=Podcast.class)
public class Podcast implements Parcelable {
    private String mTitle;
    private Bitmap mThumbnailDrawable;
    private String mDescription;
    private String mAuthor;
    private String mUri;

    //private Uri uri;
//    public Podcast(String title, Uri thumbnailDrawable, String description, String author) {
//        this.mTitle = title;
//        this.mThumbnailDrawable = thumbnailDrawable;
//        this.mDescription = description;
//        this.mAuthor = author;
//    }

    public Podcast(String name, Bitmap u, String description, String author, String uri) {

        this.mTitle = name;
        this.mThumbnailDrawable = u;
        this.mDescription = description;
        this.mAuthor = author;
        this.mUri = uri;
    }

    public Podcast(){

    }


    protected Podcast(android.os.Parcel in) {
        mTitle = in.readString();
        mThumbnailDrawable = in.readParcelable(Bitmap.class.getClassLoader());
        mDescription = in.readString();
        mAuthor = in.readString();
        mUri = in.readString();
    }

    @Override
    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeParcelable(mThumbnailDrawable, flags);
        dest.writeString(mDescription);
        dest.writeString(mAuthor);
        dest.writeString(mUri);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Podcast> CREATOR = new Creator<Podcast>() {
        @Override
        public Podcast createFromParcel(android.os.Parcel in) {
            return new Podcast(in);
        }

        @Override
        public Podcast[] newArray(int size) {
            return new Podcast[size];
        }
    };

    public String getTitle() {
        return mTitle;
    }

    public Bitmap getThumbnailDrawable() {
        return mThumbnailDrawable;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getAuthor(){return mAuthor;}

    // Returns a list of contacts
    public static List<Podcast> getPodcasts() {
        List<Podcast> podcasts = new ArrayList<>();
        //podcasts.add(new Podcast("Biden's Infrastructure plan", R.drawable.daily, "Democrats and republicans", "the new york times"));
        //podcasts.add(new Podcast("Peru's election", R.drawable.hopkins, "Pedro Castillo wins", "the new york times"));
        //podcasts.add(new Podcast("What are glaciers", R.drawable.stuff, "Ice", "the new york times"));

        return podcasts;
    }

    public void setImage(Bitmap b) {
        mThumbnailDrawable = b;
    }
}
