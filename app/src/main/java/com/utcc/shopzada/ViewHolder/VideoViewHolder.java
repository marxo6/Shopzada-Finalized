package com.utcc.shopzada.ViewHolder;

import android.media.MediaPlayer;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.squareup.picasso.Picasso;
import com.utcc.shopzada.Models.BeepData;
import com.utcc.shopzada.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class VideoViewHolder extends RecyclerView.ViewHolder {

    public VideoView beepClip;
    public TextView beepUploader, beepDescription;
    public CircleImageView beepUploaderImage;
    public ImageView verifiedSymbol;
    public LottieAnimationView loadingBar;

    public VideoViewHolder(View itemView) {
        super(itemView);

        beepClip = itemView.findViewById(R.id.beepClip);
        beepUploader = itemView.findViewById(R.id.uploader);
        beepDescription = itemView.findViewById(R.id.beepDescription);
        beepUploaderImage = itemView.findViewById(R.id.uploaderImage);
        verifiedSymbol = itemView.findViewById(R.id.verifiedSymbol);
        loadingBar = itemView.findViewById(R.id.loadingBar);
    }

}
