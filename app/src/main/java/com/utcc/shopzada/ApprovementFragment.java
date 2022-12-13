package com.utcc.shopzada;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.utcc.shopzada.Models.ProductModel;
import com.utcc.shopzada.Prevalent.Prevalent;
import com.utcc.shopzada.ViewHolder.ApprovementViewHolder;
import com.utcc.shopzada.ViewHolder.ProductViewHolder;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ApprovementFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ApprovementFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String ownerImageUrl;
    private MainActivity mainActivity;
    private RecyclerView recyclerView;
    private FirebaseDatabase database;
    private DatabaseReference dataRef;

    public ApprovementFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ApprovementFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ApprovementFragment newInstance(String param1, String param2) {
        ApprovementFragment fragment = new ApprovementFragment();
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
        View view = inflater.inflate(R.layout.fragment_approvement, container, false);
        mainActivity = (MainActivity) getActivity();
        ImageView backPress = (ImageView) view.findViewById(R.id.backButton);
        recyclerView = (RecyclerView) view.findViewById(R.id.unverifiedView);

        database = FirebaseDatabase.getInstance("https://shopzadaproject-default-rtdb.asia-southeast1.firebasedatabase.app/");
        dataRef = database.getReference("Products Unverified");

        backPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.onBackPressed();
            }
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mainActivity));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<ProductModel> options =
                new FirebaseRecyclerOptions.Builder<ProductModel>()
                        .setQuery(dataRef, ProductModel.class)
                        .build();

        FirebaseRecyclerAdapter<ProductModel, ApprovementViewHolder> adapter =
                new FirebaseRecyclerAdapter<ProductModel, ApprovementViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ApprovementViewHolder holder, int position, @NonNull ProductModel model) {
                        holder.productNameLabel.setText(model.getName());
                        holder.productPriceLabel.setText("" + model.getPrice());
                        holder.productAmountLabel.setText("" + model.getAmount());
                        holder.productDescriptionLabel.setText(model.getDescription());
                        holder.productOwnerLabel.setText(model.getOwner());
                        database.getReference("Users").child(model.getOwner()).child("imageUrl").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if (task.isSuccessful() && !task.getResult().getValue().toString().equalsIgnoreCase("")) {
                                    Picasso.get().load(task.getResult().getValue().toString()).into(holder.productOwnerImageView);
                                }
                            }
                        });
                        Picasso.get().load(model.getImage()).into(holder.productImageView);
                        holder.acceptButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                HashMap<String, Object> productMap = new HashMap<>();
                                productMap.put("id", model.getId());
                                productMap.put("name", model.getName());
                                productMap.put("description", model.getDescription());
                                productMap.put("amount", model.getAmount());
                                productMap.put("price", model.getPrice());
                                productMap.put("owner", model.getOwner());
                                productMap.put("image", model.getImage());
                                productMap.put("rating", model.getRating());
                                productMap.put("views", model.getViews());

                                database.getReference("Products").child(model.getId()).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(mainActivity, "Accepted!", Toast.LENGTH_SHORT).show();
                                            database.getReference("Users Products").child(model.getOwner()).child(model.getId()).updateChildren(productMap);
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(mainActivity, "Error: " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                                database.getReference("Products Unverified").child(model.getId()).removeValue();
                            }
                        });
                        holder.declineButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(mainActivity, "Declined!", Toast.LENGTH_SHORT).show();
                                database.getReference("Products Unverified").child(model.getId()).removeValue();
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ApprovementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.unverified_view_layout, parent, false);
                        ApprovementViewHolder approvementViewHolder = new ApprovementViewHolder(view);

                        return approvementViewHolder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}