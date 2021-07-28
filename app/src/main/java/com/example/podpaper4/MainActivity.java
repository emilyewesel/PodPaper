package com.example.podpaper4;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import java.util.*;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


//import com.android.volley.AuthFailureError;
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.PlayerApi;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.client.Result;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.Image;
import com.spotify.protocol.types.ImageUri;
import com.spotify.protocol.types.ListItems;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//import java.net.URI;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


import java.util.concurrent.TimeUnit;

import static java.lang.Integer.valueOf;

public class MainActivity extends AppCompatActivity {
    private static final String CLIENT_ID = "496eef6c993a4b4a98c9402893592d15";
    public static final String REST_URL = "https://api.twitter.com/1.1";
    //public static final String REST_CONSUMER_KEY = BuildConfig.CONSUMER_KEY;       // Change this inside apikey.properties
     // Change this inside apikey.properties

    // Landing page to indicate the OAuth flow worked in case Chrome for Android 25+ blocks navigation back to the app.
    public static final String FALLBACK_URL = "https://codepath.github.io/android-rest-client-template/success.html";

    private SpotifyAppRemote mSpotifyAppRemote;

    private RecyclerView rvPodcasts;
    private List<Podcast> podcasts;
    private PodcastsAdapter mAdapter;
    private SharedPreferences sharedPreferences;
    //private RequestQueue queue;

    private String delimiters = " .',?;:";
    private Map<String, Map<String, ArrayList<Integer>>> inverted_index = new HashMap<String, Map<String, ArrayList<Integer>>>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rvPodcasts = findViewById(R.id.rvPodcasts);

        // allows for optimizations
        rvPodcasts.setHasFixedSize(true);

        // Define 2 column grid layout
        final GridLayoutManager layout = new GridLayoutManager(MainActivity.this, 2);

        // Unlike ListView, you have to explicitly give a LayoutManager to the RecyclerView to position items on the screen.
        // There are three LayoutManager provided at the moment: GridLayoutManager, StaggeredGridLayoutManager and LinearLayoutManager.
        rvPodcasts.setLayoutManager(layout);

        // get data
        podcasts = new ArrayList<>();
        //podcasts = Podcast.getPodcasts();

        // Create an adapter
        mAdapter = new PodcastsAdapter(MainActivity.this, podcasts);

        // Bind adapter to list
        rvPodcasts.setAdapter(mAdapter);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("aK4kNVy74NqUcpXO1AYezHdY7YmSU7YyyFMr9MOP")
                .clientKey("4V0CoiSO45wDWjAoetdE80q5DJPW4pyF2xSxqjmN")
                .server("https://parseapi.back4app.com")
                .build()
        );

        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(FALLBACK_URL)
                        .showAuthView(true)
                        .build();
        SpotifyAppRemote.connect(this, connectionParams,
                new Connector.ConnectionListener() {
                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        Log.d("MainActivity", "Connected! Yay!");

                        // Now you can start interacting with App Remote
                        connected();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e("MainActivity failing to connect", throwable.getMessage(), throwable);

                        // Something went wrong when attempting to connect! Handle errors here
                    }
                });

        queryPods();


    }

    @Override
    protected void onStart() {
        super.onStart();
        queryPods();


        // We will start writing our code here.
    }

    private void connected() {
        queryPods();
    mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(playerState -> {
                    final Track track = playerState.track;
                    Log.e("MainActivity", "we are dealing with the podcast " + track.name);

                    mSpotifyAppRemote
                            .getImagesApi()
                            .getImage(playerState.track.imageUri)
                            .setResultCallback(
                            bitmap -> {
                                Podcast pod = new Podcast();
                                ParseUser currentUser = ParseUser.getCurrentUser();
                                pod.setUser(currentUser);


                                pod.setTitle(track.name);
                                pod.setAuthor(track.album.name);
                                pod.setAlbumCover(conversionBitmapParseFile(bitmap));

                                pod.setUri(track.uri);
                                //Here we can save the podcast into the database if we choose to do so.

//                                boolean contains = false;
//                                for (Podcast podd: podcasts){
//                                    if(podd.getTitle().equals(pod.getTitle())){
//                                        contains = true;
//                                    }
//                                }
//                                Log.e("TAG", "About to add the podcast ");
//                                if (!contains) {
//                                    pod.saveInBackground(new SaveCallback() {
//                                        @Override
//                                        public void done(ParseException e) {
//                                            if (e != null) {
//                                                Log.e("ERORR!!! " + e, " Toast.LENGTH_SHORT");
//                                            }
//
//                                            Log.i("TAG", "save was successful");
//
//                                            podcasts.add(pod);
//                                            mAdapter.notifyDataSetChanged();
//                                            queryPods();
//                                            Log.e("MainActivity!", "done setting up the podcast and it was unique so I added it!!" + " there were "+ podcasts.size() + " podcasts but none of then were " + pod.getTitle());
//
//                                        }
//                                    });
//                                }
//                                else{
//                                    Log.e("MainActivity", "the podcast was already there so I didn't add it :(");
//                                }



                            });

                    if (track != null) {
                        Log.d("MainActivity", track.name + " by " + track.album.name);
                    }
                });

        PlayerApi playerApi = mSpotifyAppRemote.getPlayerApi();
        CallResult<PlayerState> playerStateCall = playerApi.getPlayerState();
        Result<PlayerState> playerStateResult = playerStateCall.await(10, TimeUnit.SECONDS);
        if (playerStateResult.isSuccessful()) {
            PlayerState playerState = playerStateResult.getData();
            mSpotifyAppRemote
                    .getImagesApi()
                    .getImage(playerState.track.imageUri, Image.Dimension.LARGE);
        } else {
            Throwable error = playerStateResult.getError();
        }


    }



    private void queryPods() {
        // specify what type of data we want to query - Podcast.class
        ParseQuery<Podcast> query = ParseQuery.getQuery(Podcast.class);
        // include data referred by user key
        query.include(Podcast.KEY_TITLE);
        query.include(Podcast.KEY_USER);
        // limit query to latest 20 items
        query.setLimit(20);
        // order posts by creation date (newest first)
        query.addDescendingOrder("createdAt");
        // start an asynchronous call for posts
        query.findInBackground(new FindCallback<Podcast>() {
            @Override
            public void done(List<Podcast> pods, ParseException e) {
                // check for errors
                if (e != null) {
                    Log.e("TAG", "Issue with getting posts", e);
                    return;
                }
//                // save received posts to list and notify adapter of new data
                podcasts.clear();
                podcasts.addAll(pods);
                mAdapter.notifyDataSetChanged();
            }
        });
        populate_vectors();
    }

    private void populate_vectors(){
        //pasta: {doc 1:{1, 2, 3}, doc 2: {2, 3,5}}
        //Putting the title of a podcast into a vector of words
        for (Podcast poddd: podcasts) {
            String title = poddd.getTitle();
            String idNum = poddd.getObjectId();
            String realTitle = "";
            for (int i = 0; i < title.length(); i++) {
                if (delimiters.indexOf(title.charAt(i)) != -1) {
                    realTitle += " ";
                } else {
                    realTitle += title.charAt(i);
                }

            }
            String[] keywords = realTitle.split(" ");

            //Now that we have the vectors, we will build the inverted index
            for (int i = 0; i < keywords.length; i++) {
                String word = keywords[i];
                if (inverted_index.containsKey(word)) {

                    if (inverted_index.containsKey(idNum)) {
                        inverted_index.get(word).get(idNum).add(valueOf(i));
                    } else {
                        ArrayList<Integer> temp = new ArrayList<>();
                        temp.add(valueOf(i));
                        inverted_index.get(word).put(idNum, temp);
                    }

                } else {
                    ArrayList<Integer> temp = new ArrayList<>();
                    temp.add(valueOf(i));
                    Map<String, ArrayList<Integer>> tempy = new HashMap<String, ArrayList<Integer>>();
                    tempy.put(idNum, temp);
                    inverted_index.put(word, tempy);

                }

            }
        }
        Log.e("MainActivity", inverted_index.toString());
    }
    public ParseFile conversionBitmapParseFile(Bitmap imageBitmap){
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
        byte[] imageByte = byteArrayOutputStream.toByteArray();
        ParseFile parseFile = new ParseFile("image_file.png",imageByte);
        return parseFile;
    }

    public void handleLogOut(View view){
        ParseUser.logOut();
        ParseUser currentUser = ParseUser.getCurrentUser(); // this will now be null
        Intent i = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(i);
    }
    @Override
    protected void onStop() {
        super.onStop();
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }
}