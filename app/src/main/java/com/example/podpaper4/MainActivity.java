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
    // testing github commits


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
        //this next line starts playing a certain playlist!!
        //https:
//open.spotify.com/playlist/4ZgA4n77UZUqnzcly8siL4?uid=61c622228e63b298
      //  mSpotifyAppRemote.getPlayerApi().play("spotify:playlist:4ZgA4n77UZUqnzcly8siL4");

        //URI temp = URI.create("spotify:playlist:4ZgA4n77UZUqnzcly8siL4");
        //CallResult<ListItems> tempy = mSpotifyAppRemote.getContentApi().getRecommendedContentItems("playlist");
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
                                //Podcast pod = new Podcast(track.name, bitmap, track.album.toString(), track.artist.name, track.uri);



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
            // have some fun with playerState
        } else {
            Throwable error = playerStateResult.getError();
            // try to have some fun with the error
        }
        // Then we will write some more code here.


    }

    /*public interface VolleyCallBack {

        void onSuccess();
    }

     */
    /*public List<Podcast> getRecentlyPlayedTracks(final VolleyCallBack callBack) {
        String endpoint = "https://api.spotify.com/v1/me/player/recently-played";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, endpoint, null, response -> {
                    Gson gson = new Gson();
                    JSONArray jsonArray = response.optJSONArray("items");
                    for (int n = 0; n < jsonArray.length(); n++) {
                        try {
                            JSONObject object = jsonArray.getJSONObject(n);
                            object = object.optJSONObject("track");
                            Podcast podcast = gson.fromJson(object.toString(), Podcast.class);
                            podcasts.add(podcast);
                            Log.e("HI EVERYONE!!!", "THINGS R WORKING");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    callBack.onSuccess();
                }, error -> {
                    // TODO: Handle error

                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = sharedPreferences.getString("token", "");
                String auth = "Bearer " + token;
                headers.put("Authorization", auth);
                return headers;
            }
        };
        queue.add(jsonObjectRequest);
        return podcasts;
        }
     */

    private void queryPods() {
        // specify what type of data we want to query - Post.class
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

                // for debugging purposes let's print every post description to logcat
//                for (Podcast podd : pods) {
//                    if (podd.getObjectId().equals(pod.getObjectId())){
//                        //selfiePic = podd.getSelfie();
//                        Log.e("selfie pic isss ", "" +pod.getSelfie() + pod.getAuthor());
//                        //Glide.with(PodcastDetailsActivity.this).load(pod.getSelfie()).into(selfie);
//                    }
//                    else{
//                        Log.e("selfie pic is not ", "" +pod.getSelfie() + pod.getAuthor());
//                    }
//                    Log.i("TAG", "Post: " + pod.getTitle() + " " + pod.getSelfie());
//                }

//                allPosts.clear();
//                // save received posts to list and notify adapter of new data
                podcasts.clear();
                podcasts.addAll(pods);
                mAdapter.notifyDataSetChanged();
//                swipeContainer.setRefreshing(false);
            }
        });
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
        // Aaand we will finish off here.
    }
}