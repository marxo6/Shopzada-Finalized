package com.utcc.shopzada;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.utcc.shopzada.Models.BeepData;
import com.utcc.shopzada.Models.ProductModel;
import com.utcc.shopzada.ViewHolder.ProductViewHolder;
import com.utcc.shopzada.ViewHolder.VideoViewHolder;

import java.text.DecimalFormat;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FeedFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private MainActivity mainActivity;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private ViewPager2 beepPager;

    public FeedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FeedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FeedFragment newInstance(String param1, String param2) {
        FeedFragment fragment = new FeedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        mainActivity = (MainActivity) getActivity();
        beepPager = (ViewPager2) view.findViewById(R.id.beepPager);

        database = FirebaseDatabase.getInstance("https://shopzadaproject-default-rtdb.asia-southeast1.firebasedatabase.app/");
        reference = database.getReference("Beep Videos");

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<BeepData> options =
                new FirebaseRecyclerOptions.Builder<BeepData>()
                        .setQuery(reference, BeepData.class)
                        .build();

        FirebaseRecyclerAdapter<BeepData, VideoViewHolder> adapter =
                new FirebaseRecyclerAdapter<BeepData, VideoViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull VideoViewHolder holder, int position, @NonNull BeepData model) {
                        holder.beepClip.setVideoPath(model.getBeepUrl());
                        holder.beepUploader.setText(model.getUploader());
                        holder.beepDescription.setText(model.getDescription());
                        Picasso.get().load(model.getUploaderImage()).into(holder.beepUploaderImage);
                        if (model.isUploaderVerified()) {
                            holder.verifiedSymbol.setVisibility(View.VISIBLE);
                        }
                        holder.beepClip.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mediaPlayer) {
                                holder.loadingBar.cancelAnimation();
                                holder.loadingBar.setVisibility(View.GONE);
                                mediaPlayer.start();

                                float beepRatio = mediaPlayer.getVideoWidth() / (float) mediaPlayer.getVideoHeight();
                                float screenRatio = holder.beepClip.getWidth() / (float) holder.beepClip.getHeight();
                                float scale = beepRatio / screenRatio;
                                if (scale >= 1f) {
                                    holder.beepClip.setScaleX(scale);
                                } else {
                                    holder.beepClip.setScaleY(1f / scale);
                                }
                            }
                        });
                        holder.beepClip.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                mediaPlayer.start();
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.beep_view_layout, parent, false);
                        VideoViewHolder videoViewHolder = new VideoViewHolder(view);

                        return videoViewHolder;
                    }
                };
        beepPager.setAdapter(adapter);
        adapter.startListening();
    }
}