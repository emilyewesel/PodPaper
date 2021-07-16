package com.example.podpaper4;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.parceler.Parcels;

public class PodcastDetailsActivity extends AppCompatActivity {

    private ImageView imageDetails;
    private TextView titleDetails;
    private TextView captionDetails;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_podcast_details);

        imageDetails = findViewById(R.id.imageDetail);
        titleDetails = findViewById(R.id.titleDetail);
        captionDetails = findViewById(R.id.captionDetail);

        Podcast pod = (Podcast) Parcels.unwrap(getIntent().getParcelableExtra("pod"));

        titleDetails.setText(pod.getTitle());
        imageDetails.setImageBitmap(pod.getThumbnailDrawable());

//        ParseFile image = post.getImage();
//        if (image != null) {
//            Glide.with(PostDetailsActivity.this).load(image.getUrl()).into(imageDetails);
//        }
//        String timeSince = calculateTimeAgo(post.getCreatedAt());
//        usernameDetails.setText(post.getUser().getUsername() + " " + timeSince);
//        captionDetails.setText(post.getDescription());

    }

    public void handlePlayPodcast(){

    }
}