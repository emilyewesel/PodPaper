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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import org.parceler.Parcels;

import java.io.File;

import static com.example.podpaper4.MainActivity.FALLBACK_URL;

public class PodcastDetailsActivity extends AppCompatActivity {

    private ImageView imageDetails;
    private TextView titleDetails;
    private TextView captionDetails;
    File photoFile;
    private SpotifyAppRemote mSpotifyAppRemote;
    private String uri;
    boolean connected = false;
    public String photoFileName = "photo.jpg";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    private Button takePic;
    String TAG = "PodcastDetailsActivity";
    private ImageView selfie;



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

        Podcast pod = (Podcast) Parcels.unwrap(getIntent().getParcelableExtra("pod"));


        titleDetails.setText(pod.getTitle());
        captionDetails.setText(pod.getAuthor());
        imageDetails.setImageBitmap(pod.getThumbnailDrawable());
        uri = pod.getmUri();

        takePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
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

        // We will start writing our code here.
    }


    public void handlePlayPodcast(View view){
        while (true) {
            if(connected) {
                Log.e("spotify:track:" +uri, "is somehow not the same thing as " + "spotify:track:5HCyWlXZPP0y6Gqq8TgA20" );
                mSpotifyAppRemote.getPlayerApi().play(uri);
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
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private File getPhotoFileUri(String photoFileName) {
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
        }
    }
}