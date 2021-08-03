package com.example.podpaper4;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import java.util.*;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Track;
import java.util.ArrayList;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.podpaper4.utils.findClosestWord;
import static com.example.podpaper4.utils.processText;
import static java.lang.Integer.valueOf;

public class MainActivity extends AppCompatActivity {
    private static final String CLIENT_ID = "496eef6c993a4b4a98c9402893592d15";
    // Landing page to indicate the OAuth flow worked in case Chrome for Android 25+ blocks navigation back to the app.
    public static final String FALLBACK_URL = "https://codepath.github.io/android-rest-client-template/success.html";

    private SpotifyAppRemote mSpotifyAppRemote;
    private EditText searchTerm;

    private RecyclerView rvPodcasts;
    private List<Podcast> podcasts;
    private List<Podcast> masterPodcasts;

    private PodcastsAdapter mAdapter;

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
        rvPodcasts.setHasFixedSize(true);
        final GridLayoutManager layout = new GridLayoutManager(MainActivity.this, 2);
        rvPodcasts.setLayoutManager(layout);
        podcasts = new ArrayList<>();
        mAdapter = new PodcastsAdapter(MainActivity.this, podcasts);
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
                        connected();
                    }
                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e("MainActivity failing to connect", throwable.getMessage(), throwable);
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
    }

    private void connected() {
        queryPods();
    }

    public void getCurrentlyPlayingPodcast(){
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
                                boolean contains = false;
                                for (Podcast podd: podcasts){
                                    if(podd.getTitle().equals(pod.getTitle())){
                                        contains = true;
                                    }
                                }
                                if (!contains) {
                                    pod.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e != null) {
                                                Log.e("ERORR!!! " + e, " Toast.LENGTH_SHORT");
                                            }
                                            podcasts.add(pod);
                                            mAdapter.notifyDataSetChanged();
                                            queryPods();
                                        }
                                    });
                                }
                                    });
                    if (track != null) {
                        Log.d("MainActivity", track.name + " by " + track.album.name);
                    }
                });
    }


    public void handleSearch(View view){
        podcasts.clear();
        podcasts.addAll(masterPodcasts);
        searchTerm = findViewById(R.id.searchTerm);
        String term = searchTerm.getText().toString();
        searchTerm.setText("");
        ArrayList<Podcast> podcasts11 = rank_documents(term);
        podcasts.clear();
        podcasts.addAll(podcasts11);
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

    /*
    This function iterates through the podcasts
     */
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

    /*
    Using the podcasts that the user
     */
    private void populate_user_preferences(){
        Set<String> keys = userPreferences.keySet();
        for (int i =0; i < masterPodcasts.size(); i ++) {
            if (masterPodcasts.get(i).getSelfie() != null) {
                for (String key : keys) {
                    userPreferences.put(key, valueOf(userPreferences.get(key) + featureScores.get(masterPodcasts.get(i).getObjectId()).get(key)));
                }
            }
        }
    }

    public void handleFYP(View view){
        ArrayList<Double> finalScores = getDotProducts();
        ArrayList<Podcast> podss =  getFYP(finalScores, 4);
        podcasts.clear();
        podcasts.addAll(podss);
        mAdapter.notifyDataSetChanged();
    }

    /*
    This function uses a dot product to calculate the similarity between each podcast and the users preferences.
     */
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

    private ArrayList<Podcast> rank_documents(String query){
        String [] wordsInQuery = processText(query);
        Map<String, Integer> wordCount = new HashMap<String, Integer>();

        for (int i = 0; i < wordsInQuery.length; i++) {
            String word = wordsInQuery[i];
            if (!inverted_index.containsKey(word)){
                word = findClosestWord(word, inverted_index.keySet());
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
        int k = 0;
        for (String key: keys){
            keyArray[k++] = key;
        }
        for (int i = 0; i < keyArray.length; i++){
            String word = keyArray[i];
            for (int j = 0; j < podcasts.size(); j ++){
                if(inverted_index.containsKey(word) && inverted_index.get(word).containsKey(podcasts.get(j).getObjectId())) {
                    Log.e("now we are ", "adding to the tf idf with the word " + word + wordCount.keySet());
                    double tf_idf = getTFIDFValue(word, podcasts.get(j).getObjectId());
                    double tfQ = 1 + Math.log10(wordCount.get(word));
                    if(tf_idf * tfQ > 0) {
                        Log.e("TAG", "the word " + word + " is in the podcast " + podcasts.get(j).getTitle());
                        documentScores[j] += tf_idf * tfQ;
                    }
                }
            }
        }
        Double [] documentScoresDoubles = new Double [podcasts.size()];
        for (int i =0; i < documentScoresDoubles.length; i ++){
            documentScoresDoubles[i] = new Double(documentScores[i]);
        }
        ArrayList<Double> list = new ArrayList<>(Arrays.asList(documentScoresDoubles));
        return getKMostRelevant(list, 3);
    }

    /*
    Used for finding the podcasts best suited to a search term, this function takes the precomputed
    scores and returns the best podcasts to display to the user searching.
     */
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
        return returnThis;
    }

    /*
    This function populates the for you page by finding the podcasts with the highest predicted rating.
     */
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
        return returnThis;
    }

    private void populate_vectors(){
        //pasta: {doc 1:{1, 2, 3}, doc 2: {2, 3,5}}
        //Putting the title of a podcast into a vector of words
        for (Podcast poddd: podcasts) {
            String [] keywords = processText(poddd.getTitle());
            String idNum = poddd.getObjectId();
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