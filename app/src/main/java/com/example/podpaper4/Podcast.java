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





 /*   private String mTitle;
    private Bitmap mThumbnailDrawable;
    private String mDescription;
    private String mAuthor;
    private String mUri;
    private Bitmap mSelfie;

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
        //this.mSelfie = u;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public Bitmap getmThumbnailDrawable() {
        return mThumbnailDrawable;
    }

    public void setmThumbnailDrawable(Bitmap mThumbnailDrawable) {
        this.mThumbnailDrawable = mThumbnailDrawable;
    }

    public String getmDescription() {
        return mDescription;
    }

    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public String getmAuthor() {
        return mAuthor;
    }

    public void setmAuthor(String mAuthor) {
        this.mAuthor = mAuthor;
    }

    public void setmUri(String mUri) {
        this.mUri = mUri;
    }

    public Bitmap getmSelfie() {
        return mSelfie;
    }

    public void setmSelfie(Bitmap mSelfie) {
        this.mSelfie = mSelfie;
    }

    public Podcast(){

    }


    protected Podcast(android.os.Parcel in) {
        mTitle = in.readString();
        mThumbnailDrawable = in.readParcelable(Bitmap.class.getClassLoader());
        mDescription = in.readString();
        mAuthor = in.readString();
        mUri = in.readString();
        mSelfie = in.readParcelable(Bitmap.class.getClassLoader());
    }

    @Override
    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeParcelable(mThumbnailDrawable, flags);
        dest.writeString(mDescription);
        dest.writeString(mAuthor);
        dest.writeString(mUri);
        dest.writeParcelable(mSelfie, flags);
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

    public String getmUri(){
        return mUri;
    }

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
    }*/


}
