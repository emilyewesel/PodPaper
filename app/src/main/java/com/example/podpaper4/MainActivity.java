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
import android.widget.EditText;
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
    private EditText searchTerm;

    private RecyclerView rvPodcasts;
    private List<Podcast> podcasts;
    private List<Podcast> masterPodcasts;
    private List<Podcast> likedPodcasts;

    private PodcastsAdapter mAdapter;
    private SharedPreferences sharedPreferences;
    //private RequestQueue queue;

    String [] temp = {"turkey", "turkish", "prime", "minister", "latin", "iran", "germany", "syria",
            "foreign", "ethiopia", "korea", "myanmar", "italy", "peru", "haiti", "international"};
    public ArrayList<String> foreignWords = new ArrayList<String>(Arrays.asList(temp));
    private String [] scienceWordsyy = {"osmosis", "covid", "corona", "virus", "pandemic", "shot"};
    public ArrayList<String> scienceWords = new ArrayList<String>(Arrays.asList(scienceWordsyy));
    private String delimiters = " .',?;:-’";
    private Map<String, Map<String, ArrayList<Integer>>> inverted_index = new HashMap<String, Map<String, ArrayList<Integer>>>();

    private Map<String, Map<String, Integer>> featureScores = new HashMap<String, Map<String, Integer>>();

    private Map<String, Integer> userPreferences = new HashMap<>();


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
        userPreferences.put("scienceScore", 0);
        userPreferences.put("foreignScore", 0);
        userPreferences.put("newsScore", 0);


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


    public void handleSearch(View view){
        //queryPods();
        podcasts.clear();
        podcasts.addAll(masterPodcasts);
        searchTerm = findViewById(R.id.searchTerm);
        String term = searchTerm.getText().toString();
        searchTerm.setText("");
        int numPods = 3;
        ArrayList<Podcast> podcasts11 = rank_documents(term);
        podcasts.clear();
        podcasts.addAll(podcasts11);
        Log.e("changing the podcasts to beee", podcasts.toString());
        mAdapter.notifyDataSetChanged();


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
                // save received posts to list and notify adapter of new data
                masterPodcasts = pods;

                podcasts.clear();
                podcasts.addAll(pods);
                mAdapter.notifyDataSetChanged();
                populate_vectors();
                populate_feature_scores();
                populate_user_preferences();

            }
        });

    }

    private void populate_feature_scores() {
        for (int i =0; i < masterPodcasts.size(); i ++){
            String [] words = processText(masterPodcasts.get(i).getTitle());
            int foreignScore = 0;
            int scienceScore = 0;
            int newsScore = 0;
            for (int j = 0; j < words.length; j ++){
                if(foreignWords.contains(words[j])){
                    foreignScore ++;
                }
                if(scienceWords.contains(words[j])){
                    scienceScore ++;
                }
            }
            String author = masterPodcasts.get(i).getAuthor();
            if (author.equals("The Times: Daily news from the L.A. Times") || author.equals("The Daily")){
                newsScore += 3;
            }
            else if (author.equals("EMILYYY") || author.equals("Hopkins Podcast on Foreign Affairs")){
                foreignScore += 4;
            }
            else if (author.equals("Stuff You Should Know")){
                scienceScore += 1;
            }

            Map<String, Integer> scores = new HashMap<>();
            scores.put("scienceScore", scienceScore);
            scores.put("foreignScore", foreignScore);
            scores.put("newsScore", newsScore);
            featureScores.put(masterPodcasts.get(i).getObjectId(), scores);

        }
    }

    private void populate_user_preferences(){
        Set<String> keys = userPreferences.keySet();
        for (int i =0; i < masterPodcasts.size(); i ++) {
            if (masterPodcasts.get(i).getSelfie() != null) {
                for (String key : keys) {
                    userPreferences.put(key, valueOf(userPreferences.get(key) + featureScores.get(masterPodcasts.get(i).getObjectId()).get(key)));
                }
            }
        }
        Log.e("I think that the user likes!! ", userPreferences.keySet().toString() + userPreferences.values().toString());
    }

    public void handleFYP(View view){
        ArrayList<Double> finalScores = getDotProducts();

        ArrayList<Podcast> podss =  getFYP(finalScores, 4);

        podcasts.clear();
        podcasts.addAll(podss);
        mAdapter.notifyDataSetChanged();

    }

    public ArrayList<Double> getDotProducts(){
        Set<String> keys = userPreferences.keySet();
        ArrayList<Double> dotProducts = new ArrayList<>();

        for (int i = 0; i < masterPodcasts.size(); i ++){
            double fullScore  = 0;
            for (String key : keys) {
                fullScore += featureScores.get(masterPodcasts.get(i).getObjectId()).get(key) * userPreferences.get(key);
            }
            dotProducts.add(fullScore);
        }
        return dotProducts;
    }

    private double findEditDistance(String word1, String word2){
        double [] [] matrix = new double[word1.length() + 1][word2.length() +1];
        for (int i = 0; i < word1.length(); i ++){
            for (int j = 0; j <word2.length(); j ++){
                matrix[i][j] = 0;
            }
        }

        for (int i = 0; i <word2.length()+1; i++) {
            matrix[word1.length()][i] = i;
        }

        for (int i = 0; i < word1.length()+1; i++) {
            matrix[i][0] = word1.length() - i;
        }

        for (int i =1; i < word2.length() + 1; i++) {
            for (int j = 1; j < word1.length() + 1; j++) {
                double sub = matrix[word1.length() - j + 1][i - 1];
                if(word1.charAt(j - 1) != word2.charAt(i - 1)) {
                    sub += 2;
                }
                double e1 = matrix[word1.length() - j + 1][i] + 1;
                double e2 = matrix[word1.length() - j][i - 1]+ 1;

                ArrayList<Double> poss = new ArrayList<>();
                poss.add(sub);
                poss.add(e1);
                poss.add(e2);


                matrix[word1.length() - j][i] =  Collections.min(poss);
            }
        }
        String matrixx = "";
        for (int i =0; i < matrix.length; i ++){
            for (int j = 0; j < matrix[i].length; j++){
                matrixx += matrix[i][j] + " ";
            }
            matrixx += "\n";
        }

        Log.e("the matrix iss ", matrixx);
        return matrix[0][word2.length()];
    }

    private ArrayList<Podcast> rank_documents(String query){

        String [] wordsInQuery = processText(query);

        Set<String> wordsSeen = inverted_index.keySet();
        Map<String, Integer> wordCount = new HashMap<String, Integer>();

        for (int i = 0; i < wordsInQuery.length; i++) {
            String word = wordsInQuery[i];
            if (!inverted_index.containsKey(word)){
                word = findClosestWord(word);
            }



            if (wordCount.containsKey(word)){
                wordCount.put(word, valueOf(wordCount.get(word).intValue() +1));
            }
            else{
                wordCount.put(word, 1);
            }
        }

        double [] documentScores = new double [podcasts.size()];
        for (int i =0; i < documentScores.length; i ++){
            documentScores[i] = 0;
        }

        Set<String> keys = wordCount.keySet();
        String [] keyArray = new String[keys.size()];
        int k =0;
        for (String key: keys){
            keyArray[k++] = key;
        }
        for (int i = 0; i < keyArray.length; i++){
            String word = keyArray[i];
            for (int j = 0; j < podcasts.size(); j ++){
                Log.e("the keys are", inverted_index.keySet().toString());


                //Log.e("inverted index at word is ", "" + inverted_index.get(word).toString());
                //Log.e("and the id number is ", "" +podcasts.get(j).getObjectId() + "the podcasts array is " + podcasts.toString());
                if(inverted_index.containsKey(word) && inverted_index.get(word).containsKey(podcasts.get(j).getObjectId())) {
                    Log.e("now we are ", "adding to the tf idf with the word " + word + wordCount.keySet());
                    double tf_idf = getTFIDFValue(word, podcasts.get(j).getObjectId());
                    double tfQ = 1 + Math.log10(wordCount.get(word));
                    if(tf_idf * tfQ > 0) {
                        Log.e("TAG", "the word " + word + " is in the podcast " + podcasts.get(j).getTitle());
                        documentScores[j] += tf_idf * tfQ;
                    }
                }
            else{
                Log.e("no",  "longer adding to the inverted index :(");
                }
            }
        }

        Double [] documentScoresDoubles = new Double [podcasts.size()];
        for (int i =0; i < documentScoresDoubles.length; i ++){
            documentScoresDoubles[i] = new Double(documentScores[i]);
        }

        for (int e = 0; e < podcasts.size(); e++ ) {
            Log.e("the document ", podcasts.get(e).getTitle() +  " has a score of " + documentScores[e]);
        }
        ArrayList<Double> list = new ArrayList<>(Arrays.asList(documentScoresDoubles));
        return getKMostRelevant(list, 3);

    }

    private String findClosestWord(String fakeWord) {
        String bestWord = fakeWord;
        double bestDistance = 99999999;
        Set<String> realWords = inverted_index.keySet();
        Log.e("the REAL keys are", inverted_index.keySet().toString());
        for (String word : realWords){
            double editDistance = findEditDistance(fakeWord, word);
            Log.e("The word ", word + " has an edit disatnace of " + editDistance);
            if (editDistance < bestDistance){
                bestDistance = editDistance;
                bestWord = word;
            }

        }
        Log.e("The best word is ", bestWord);
        return bestWord;
    }

    private ArrayList<Podcast> getKMostRelevant(ArrayList<Double> listy, int numDesired){
        ArrayList<Podcast> returnThis = new ArrayList<Podcast>();
        ArrayList<Double> listyy = (ArrayList<Double>) listy.clone();
        for (int i =0; i < numDesired; i++){
            double highestScore = 0;
            int highestIndex = 0;
            for (int j = 0; j <listyy.size(); j ++){
                if (listyy.get(j) >= highestScore){
                    highestIndex = j;
                    highestScore = listyy.get(j);
                }
            }
            returnThis.add(podcasts.get(highestIndex));
            listyy.remove(highestIndex);
        }
        for (int e = 0; e < numDesired; e++ ) {
            Log.e("here are the most relevant documents!!!", returnThis.get(e).getTitle());
        }
        return returnThis;
    }

    private ArrayList<Podcast> getFYP(ArrayList<Double> listy, int numDesired){
        ArrayList<Podcast> returnThis = new ArrayList<Podcast>();
        ArrayList<Double> listyy = (ArrayList<Double>) listy.clone();
        for (int i =0; i < numDesired; i++){
            double highestScore = 0;
            int highestIndex = 0;
            for (int j = 0; j <listyy.size(); j ++){
                if (listyy.get(j) >= highestScore){
                    highestIndex = j;
                    highestScore = listyy.get(j);
                }
            }
            returnThis.add(masterPodcasts.get(highestIndex));
            listyy.remove(highestIndex);
        }
        for (int e = 0; e < numDesired; e++ ) {
            Log.e("here are the most relevant documents!!!", returnThis.get(e).getTitle());
        }
        return returnThis;
    }
    private String [] processText(String text){
        text = text.toLowerCase();
        String realTitle = "";
        for (int i = 0; i < text.length(); i++) {
            if (delimiters.indexOf(text.charAt(i)) != -1) {
                realTitle += " ";
            } else {
                realTitle += text.charAt(i);
            }

        }
        String[] keywords = realTitle.split(" ");
        return keywords;

    }

    private void populate_vectors(){
        //pasta: {doc 1:{1, 2, 3}, doc 2: {2, 3,5}}
        //Putting the title of a podcast into a vector of words
        for (Podcast poddd: podcasts) {
            String title = poddd.getTitle().toLowerCase();
            String idNum = poddd.getObjectId();
            String realTitle = "";
            for (int i = 0; i < title.length(); i++) {
                if (delimiters.indexOf(title.charAt(i)) != -1) {
                    realTitle += " ";
                } else {
                    //Log.e("TAG", title.charAt(i) +" is not in " + delimiters);
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
    public int getTFValue(String word, String doc){
        return inverted_index.get(word).get(doc).size();

    }
    public int getIDFValue(String word){
        return inverted_index.get(word).size();
    }

    public double getTFIDFValue(String word, String doc){
        return (1 + Math.log10(getTFValue(word, doc)))*(Math.log10(podcasts.size()/getIDFValue(word)));
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