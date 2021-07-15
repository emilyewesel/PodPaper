package com.example.podpaper4;

import com.spotify.protocol.types.Album;
import com.spotify.protocol.types.Artist;
import com.spotify.protocol.types.ImageUri;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Podcast implements Serializable {
    private String mTitle;
    private int mThumbnailDrawable;
    private String mDescription;
    private String mAuthor;

    public Podcast(String title, int thumbnailDrawable, String description, String author) {
        this.mTitle = title;
        this.mThumbnailDrawable = thumbnailDrawable;
        this.mDescription = description;
        this.mAuthor = author;
    }

    public String getTitle() {
        return mTitle;
    }

    public int getThumbnailDrawable() {
        return mThumbnailDrawable;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getAuthor(){return mAuthor;}

    // Returns a list of contacts
    public static List<Podcast> getPodcasts() {
        List<Podcast> podcasts = new ArrayList<>();
        podcasts.add(new Podcast("Biden's Infrastructure plan", R.drawable.daily, "Democrats and republicans", "the new york times"));
        podcasts.add(new Podcast("Peru's election", R.drawable.hopkins, "Pedro Castillo wins", "the new york times"));
        podcasts.add(new Podcast("What are glaciers", R.drawable.stuff, "Ice", "the new york times"));

        return podcasts;
    }

}
