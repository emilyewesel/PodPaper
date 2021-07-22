package com.example.podpaper4;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
//import com.parse.ParseObject;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.io.File;
import java.util.List;

import static com.example.podpaper4.MainActivity.FALLBACK_URL;

public class PodcastDetailsActivity extends AppCompatActivity {

    private ImageView imageDetails;
    private TextView titleDetails;
    private TextView captionDetails;
    private TextView tapText;
    File photoFile;
    private SpotifyAppRemote mSpotifyAppRemote;
    private String uri;
    boolean connected = false;
    public String photoFileName = "photo.jpg";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    private Button takePic;
    String TAG = "PodcastDetailsActivity";
    private ImageView selfie;
    private LottieAnimationView heart;
    private Podcast pod;
    ParseFile selfiePic;



    private static final String CLIENT_ID = "496eef6c993a4b4a98c9402893592d15";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_podcast_details);

        imageDetails = findViewById(R.id.imageDetail);
        titleDetails = findViewById(R.id.titleDetail);
        captionDetails = findViewById(R.id.captionDetail);
        takePic = findViewById(R.id.takePicture);
        selfie = findViewById(R.id.selfie);
        tapText = findViewById(R.id.tapText);
        heart = findViewById(R.id.heart);

        pod = (Podcast) Parcels.unwrap(getIntent().getParcelableExtra("pod"));


        titleDetails.setText(pod.getTitle());
        captionDetails.setText(pod.getAuthor());
        uri = pod.getUri();
//        ParseQuery<Podcast> query = ParseQuery.getQuery(Podcast.class);
//
//        query.findInBackground();


        String imageUrl = pod.getAlbumCover().getUrl();
        Picasso.get().load(imageUrl).into(imageDetails);
        //Glide.with(PodcastDetailsActivity.this).load(uri).into(imageDetails);
        //imageDetails.setImageBitmap(pod.getUri());
//        try {
//            imageDetails.setImageBitmap(BitmapFactory.decodeFile(pod.getAlbumCover().getFile().getAbsolutePath()));
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }





        if (pod.getSelfie() != null){
            Log.e("I HAVE A SELFIE I WANT TO SHARE", "by emily");
            //BitmapFactory.decodeFile(photoFile.getAbsolutePath());
            Glide.with(PodcastDetailsActivity.this).load(pod.getSelfie().getUrl()).into(selfie);
            Animation fadeIn = new AlphaAnimation(0, 1);
            fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
            fadeIn.setDuration(1000);

            Animation fadeOut = new AlphaAnimation(1, 0);
            fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
            fadeOut.setStartOffset(1000);
            fadeOut.setDuration(1000);

            AnimationSet animation = new AnimationSet(false); //change to false
            animation.addAnimation(fadeIn);
            animation.addAnimation(fadeOut);
            selfie.setAnimation(animation);
        }
        else{
            Log.e("no selfie :/", "emily");
        }


        imageDetails.setOnTouchListener(new View.OnTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(PodcastDetailsActivity.this, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    Log.d("TEST", "onDoubleTap");
                    heart.playAnimation();
                    heart.setVisibility(View.VISIBLE);
                    return super.onDoubleTap(e);
                }
            });
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("TEST", "Raw event: " + event.getAction() + ", (" + event.getRawX() + ", " + event.getRawY() + ")");
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });

        takePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
            }
        });
        ParseObject firstObject = new  ParseObject("FirstClass");
        firstObject.put("message","Hey ! First message from android. Parse is now connected");
        firstObject.saveInBackground(e -> {
            if (e != null){
                Log.e("MainActivity", e.getLocalizedMessage());
            }else{
                Log.d("MainActivity","Object saved.");
            }
        });



//        ParseFile image = post.getImage();
//        if (image != null) {
//            Glide.with(PostDetailsActivity.this).load(image.getUrl()).into(imageDetails);
//        }
//        String timeSince = calculateTimeAgo(post.getCreatedAt());
//        usernameDetails.setText(post.getUser().getUsername() + " " + timeSince);
//        captionDetails.setText(post.getDescription());

    }

    private void queryPods() {
//        // specify what type of data we want to query - Post.class
//        ParseQuery<Podcast> query = ParseQuery.getQuery(Podcast.class);
//        // include data referred by user key
//        query.include(Podcast.KEY_TITLE);
//        query.include(Podcast.KEY_USER);
//        // limit query to latest 20 items
//        query.setLimit(20);
//        // order posts by creation date (newest first)
//        query.addDescendingOrder("createdAt");
//        // start an asynchronous call for posts
//        query.findInBackground(new FindCallback<Podcast>() {
//            @Override
//            public void done(List<Podcast> pods, ParseException e) {
//                // check for errors
//                if (e != null) {
//                    Log.e("TAG", "Issue with getting posts", e);
//                    return;
//                }
//
//                // for debugging purposes let's print every post description to logcat
////                for (Podcast podd : pods) {
////                    if (podd.getObjectId().equals(pod.getObjectId())){
////                        //selfiePic = podd.getSelfie();
////                        Log.e("selfie pic isss ", "" +pod.getSelfie() + pod.getAuthor());
////                        //Glide.with(PodcastDetailsActivity.this).load(pod.getSelfie()).into(selfie);
////                    }
////                    else{
////                        Log.e("selfie pic is not ", "" +pod.getSelfie() + pod.getAuthor());
////                    }
////                    Log.i("TAG", "Post: " + pod.getTitle() + " " + pod.getSelfie());
////                }
//
////                adapter.clear();
////                allPosts.clear();
////                // save received posts to list and notify adapter of new data
////                allPosts.addAll(posts);
////                adapter.notifyDataSetChanged();
////                swipeContainer.setRefreshing(false);
//            }
//        });
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
                        connected = true;
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e("MainActivity failing to connect", throwable.getMessage(), throwable);

                        // Something went wrong when attempting to connect! Handle errors here
                    }
                });

        queryPods();
        // We will start writing our code here.
    }


    public void handlePlayPodcast(View view){
        while (true) {
            if(connected) {
                //Log.e("spotify:track:" +uri, "is somehow not the same thing as " + "spotify:track:5HCyWlXZPP0y6Gqq8TgA20" );
                mSpotifyAppRemote.getPlayerApi().play(uri);
                Toast.makeText(PodcastDetailsActivity.this, "Now playing " + titleDetails.getText(), Toast.LENGTH_SHORT).show();
                break;
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                //Bitmap resizedBitmap = BitmapScaler.scaleToFitWidth(takenImage, SOME_WIDTH);
                selfie.setImageBitmap(takenImage);
                savePod(photoFile);
                pod.setAuthor("EMILYYY");
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private File getPhotoFileUri(String photoFileName) {
        pod.setAuthor("EMILYYY");
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + photoFileName);

    }

    private void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);
        //savePod(photoFile);
        pod.setAuthor("EMILYYY");

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(PodcastDetailsActivity.this, "com.codepath.fileprovider.emily", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the image capture intent to take photo
            Log.e("about to start image for result", "yep");
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            pod.setAuthor("EMILYYY");

        }
    }

    private void savePod(/*String description, ParseUser currentUser*/File photoFile) {
//        Post post = new Post();
//        post.setDescription(description);
//        post.setImage(new ParseFile(photoFile));
//        post.setUser(currentUser);



        //String filePath = photoFile.getPath();
        //Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        pod.setSelfie(new ParseFile(photoFile));
        //Log.e("emily!!", pod.getmSelfie().toString());
//        //Toast.makeText(MainActivity.this, "Trying to save post!!!", Toast.LENGTH_SHORT).show();
        pod.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
                    Log.e("ERORR!!! " + e," Toast.LENGTH_SHORT");
                    Toast.makeText(PodcastDetailsActivity.this, "ERORR!!! " + e, Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG, "save was successful!!! YAY!!!!");
            }
        });
    }

}