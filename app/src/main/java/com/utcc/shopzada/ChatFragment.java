package com.utcc.shopzada;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.utcc.shopzada.Models.UserDisplayModel;
import com.utcc.shopzada.Prevalent.Prevalent;
import com.utcc.shopzada.ViewHolder.UserViewHolder;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {

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
    private RecyclerView recyclerView;

    public ChatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatFragment newInstance(String param1, String param2) {
        ChatFragment fragment = new ChatFragment();
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
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        mainActivity = (MainActivity) getActivity();
        recyclerView = (RecyclerView) view.findViewById(R.id.userRecyclerView);

        database = FirebaseDatabase.getInstance("https://shopzadaproject-default-rtdb.asia-southeast1.firebasedatabase.app/");
        reference = database.getReference("Chat");

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mainActivity));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<UserDisplayModel> options =
                new FirebaseRecyclerOptions.Builder<UserDisplayModel>()
                        .setQuery(reference.child(Prevalent.currentOnlineUser.getUsername()), UserDisplayModel.class)
                        .build();

        FirebaseRecyclerAdapter<UserDisplayModel, UserViewHolder> adapter =
                new FirebaseRecyclerAdapter<UserDisplayModel, UserViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull UserDisplayModel model) {
                        holder.userTextLabel.setText(model.getId());
                        database.getReference("Users").child(model.getId()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    if (!snapshot.child("imageUrl").getValue().toString().equalsIgnoreCase("")) {
                                        Picasso.get().load(snapshot.child("imageUrl").getValue().toString()).into(holder.userImageView);
                                    }
                                    if (snapshot.child("verified").getValue().equals(true)) {
                                        holder.verifiedSymbol.setVisibility(View.VISIBLE);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatview_layout, parent, false);
                        UserViewHolder userViewHolder = new UserViewHolder(view);

                        return userViewHolder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}