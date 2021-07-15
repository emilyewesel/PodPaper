package com.example.podpaper4;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.spotify.protocol.types.ImageUri;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.net.URI;
import java.util.List;

public class PodcastsAdapter extends RecyclerView.Adapter<PodcastsAdapter.VH> {
    private Activity mContext;
    private List<Podcast> mPodcasts;

    public PodcastsAdapter(Activity context, List<Podcast> podcasts) {
        mContext = context;
        if (podcasts == null) {
            throw new IllegalArgumentException("contacts must not be null");
        }
        mPodcasts = podcasts;
    }

    // Inflate the view based on the viewType provided.
    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_podcast, parent, false);
        return new VH(itemView, mContext);
    }

    // Display data at the specified position
    @Override
    public void onBindViewHolder(VH holder, int position) {
        Podcast podcast = mPodcasts.get(position);
        holder.rootView.setTag(podcast);
        holder.tvName.setText(podcast.getTitle());
        //Log.e("Hi this is emily!!", podcast.getThumbnailDrawable().getPath());
        //ImageUri u = podcast.getThumbnailDrawable();
        //Log.e("emily emily emily");
        holder.ivProfile.setImageBitmap(podcast.getThumbnailDrawable());
        //Picasso.get().load(u).into(holder.ivProfile);
        //Glide.with(mContext).load(u).centerCrop().into(holder.ivProfile);
    }


    @Override
    public int getItemCount() {
        return mPodcasts.size();
    }

    // Provide a reference to the views for each contact item
    public class VH extends RecyclerView.ViewHolder {
        final View rootView;
        final ImageView ivProfile;
        final TextView tvName;
        final View vPalette;

        public VH(View itemView, final Context context) {
            super(itemView);
            rootView = itemView;
            ivProfile = (ImageView)itemView.findViewById(R.id.ivProfile);
            tvName = (TextView)itemView.findViewById(R.id.tvName);
            vPalette = itemView.findViewById(R.id.vPalette);

            // Navigate to contact details activity on click of card view.
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Podcast contact = (Podcast) v.getTag();
                    if (contact != null) {
                        // Fire an intent when a contact is selected
                        // Pass contact object in the bundle and populate details activity.
                    }
                }
            });
        }
    }
}

