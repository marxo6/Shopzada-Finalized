package com.utcc.shopzada;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.utcc.shopzada.Prevalent.Prevalent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UploadBeepFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UploadBeepFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String beepId, downloadBeepKey, description;
    private MainActivity mainActivity;
    private Uri videoUri;
    private ImageView beepIcon, uploadButton;
    private VideoView beepVideo;
    private EditText descriptionField;
    private static final int VIDEOPICK_CODE = 1;
    private FirebaseDatabase database;
    private DatabaseReference dataRef;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    public UploadBeepFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UploadBeepFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UploadBeepFragment newInstance(String param1, String param2) {
        UploadBeepFragment fragment = new UploadBeepFragment();
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
        View view = inflater.inflate(R.layout.fragment_upload_beep, container, false);
        mainActivity = (MainActivity) getActivity();
        ImageView backPress = (ImageView) view.findViewById(R.id.backButton);
        beepIcon = (ImageView) view.findViewById(R.id.beepIcon);
        CardView addBeepButton = (CardView) view.findViewById(R.id.addVideoButton);
        beepVideo = (VideoView) view.findViewById(R.id.beepClip);
        descriptionField = (EditText) view.findViewById(R.id.descriptionInput);
        uploadButton = (ImageView) view.findViewById(R.id.uploadButton);

        storage = FirebaseStorage.getInstance("gs://shopzadaproject.appspot.com");
        database = FirebaseDatabase.getInstance("https://shopzadaproject-default-rtdb.asia-southeast1.firebasedatabase.app/");
        storageRef = storage.getReference("Beep Videos");
        dataRef = database.getReference("Beep Videos");

        backPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.onBackPressed();
            }
        });

        addBeepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openVideoGallery();
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadVideo();
            }
        });

        return view;
    }

    private void uploadVideo() {
        description = descriptionField.getText().toString();

        if (description.isEmpty()) {
            Toast.makeText(mainActivity, "Description cannot be empty!", Toast.LENGTH_SHORT).show();
            descriptionField.requestFocus();
            return;
        } else if (videoUri == null) {
            Toast.makeText(mainActivity, "Please select a video!", Toast.LENGTH_SHORT).show();
            return;
        } else {
            uploadToFirebase();
        }
    }

    private void uploadToFirebase() {
        String currentDate, currentTime;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        currentDate = dateFormat.format(calendar.getTime());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss a");
        currentTime = timeFormat.format(calendar.getTime());

        beepId = System.currentTimeMillis() + "_" + Prevalent.currentOnlineUser.getUsername();

        final UploadTask uploadTask = storageRef.child(beepId).putFile(videoUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mainActivity, "Error: " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        downloadBeepKey = storageRef.child(beepId).toString();
                        return storageRef.child(beepId).getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(mainActivity, "Beep uploaded to database!", Toast.LENGTH_SHORT).show();
                            downloadBeepKey = task.getResult().toString();
                            uploadDatabase();
                        }
                    }
                });
            }
        });
    }

    private void uploadDatabase() {
        HashMap<String, Object> beepDataMap = new HashMap<>();
        beepDataMap.put("id", beepId);
        beepDataMap.put("description", description);
        beepDataMap.put("owner", Prevalent.currentOnlineUser.getUsername());
        beepDataMap.put("ownerImage", Prevalent.currentOnlineUser.getImageUrl());
        beepDataMap.put("ownerVerified", Prevalent.currentOnlineUser.isVerified());
        beepDataMap.put("likes", 0);
        beepDataMap.put("views", 0);
        beepDataMap.put("beepUrl", downloadBeepKey);

        dataRef.child(beepId).updateChildren(beepDataMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(mainActivity, "Successful!", Toast.LENGTH_SHORT).show();
                            mainActivity.onBackPressed();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mainActivity, "Error: " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void openVideoGallery() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select a video"), VIDEOPICK_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == VIDEOPICK_CODE && resultCode == Activity.RESULT_OK && data != null) {
            videoUri = data.getData();
            setVideoView();
        }
    }

    private void setVideoView() {
        MediaController mediaController = new MediaController(mainActivity);
        mediaController.setAnchorView(beepVideo);
        beepVideo.setVisibility(View.VISIBLE);
        beepIcon.setVisibility(View.GONE);
        beepVideo.setMediaController(mediaController);
        beepVideo.setVideoURI(videoUri);
        beepVideo.requestFocus();
        beepVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                beepVideo.pause();
            }
        });
    }
}