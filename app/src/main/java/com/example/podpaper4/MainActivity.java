package com.example.podpaper4;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

//import com.android.volley.AuthFailureError;
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
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
        podcasts = Podcast.getPodcasts();

        // Create an adapter
        mAdapter = new PodcastsAdapter(MainActivity.this, podcasts);

        // Bind adapter to list
        rvPodcasts.setAdapter(mAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();


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

        // We will start writing our code here.
    }

    private void connected() {
        //this next line starts playing a certain playlist!!
        //https:
//open.spotify.com/playlist/4ZgA4n77UZUqnzcly8siL4?uid=61c622228e63b298
      //  mSpotifyAppRemote.getPlayerApi().play("spotify:playlist:4ZgA4n77UZUqnzcly8siL4");

        URI temp = URI.create("spotify:playlist:4ZgA4n77UZUqnzcly8siL4");
        CallResult<ListItems> tempy = mSpotifyAppRemote.getContentApi().getRecommendedContentItems("playlist");
    mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(playerState -> {
                    final Track track = playerState.track;
                    Uri u = Uri.parse(track.imageUri.raw);
                    u.getPath();
                    Log.e("Hi this is emily!!", u.toString());
                    ImageUri image = track.imageUri;

                    mSpotifyAppRemote
                            .getImagesApi()
                            .getImage(playerState.track.imageUri)
                            .setResultCallback(
                            bitmap -> {
                                Podcast pod = new Podcast(track.name, bitmap, track.artist.toString(), track.album.toString());


                                podcasts.add(pod);
                                podcasts.add(pod);
                                podcasts.add(pod);
                                mAdapter.notifyDataSetChanged();

                            });


                    if (track != null) {
                        Log.d("MainActivity", track.name + " by " + track.artist.name);
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


    @Override
    protected void onStop() {
        super.onStop();
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
        // Aaand we will finish off here.
    }
}