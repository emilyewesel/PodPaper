package com.example.podpaper4;

import android.graphics.Bitmap;
import android.os.Parcelable;

//import com.parse.ParseClassName;
import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.spotify.protocol.types.Album;
import com.spotify.protocol.types.Artist;
import com.spotify.protocol.types.ImageUri;
import com.spotify.protocol.types.Uri;
//import com.spotify.protocol.types.Uri;

import org.parceler.Parcel;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@ParseClassName("Podcast")
@Parcel(analyze=Podcast.class)
public class Podcast extends ParseObject implements Parcelable {
    public static final String KEY_USER = "user";
    public static final String KEY_SELFIE = "selfie";
    public static final String KEY_TITLE = "Title";
    public static final String KEY_AUTHOR = "Author";
    public static final String KEY_ALBUMCOVER = "albumCover";
    public static final String KEY_Uri = "Uri";

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }
    public void setUser(ParseUser key){
        put(KEY_USER, key);

    }
    public ParseFile getSelfie() {
        return getParseFile(KEY_SELFIE);
    }

    public void setSelfie(ParseFile selfie) {
        put(KEY_SELFIE, selfie);
    }

    public void setTitle(String title) {
        put(KEY_TITLE, title);
    }

    public String getTitle() {
        return getString(KEY_TITLE);
    }

    public void setAuthor(String author) {
        put(KEY_AUTHOR, author);
    }

    public String getAuthor() {
        return getString(KEY_AUTHOR);
    }

    public void setUri(String uri) {
        put(KEY_Uri, uri);
    }

    public String getUri() {
        return getString(KEY_Uri);
    }

    public void setAlbumCover(ParseFile file) {
        put(KEY_ALBUMCOVER, file);
    }

    public ParseFile getAlbumCover() {
        return getParseFile(KEY_ALBUMCOVER);
    }


}
