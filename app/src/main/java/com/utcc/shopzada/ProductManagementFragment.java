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
import com.utcc.shopzada.ViewHolder.ModeratorManagementViewHolder;
import com.utcc.shopzada.ViewHolder.ProductViewHolder;

import java.text.DecimalFormat;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProductManagementFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProductManagementFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FirebaseDatabase database;
    private DatabaseReference dataRef;
    private MainActivity mainActivity;
    private RecyclerView recyclerView;

    public ProductManagementFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProductManagementFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProductManagementFragment newInstance(String param1, String param2) {
        ProductManagementFragment fragment = new ProductManagementFragment();
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
        View view = inflater.inflate(R.layout.fragment_product_management, container, false);
        mainActivity = (MainActivity) getActivity();
        ImageView backPress = (ImageView) view.findViewById(R.id.backButton);
        recyclerView = (RecyclerView) view.findViewById(R.id.managementRecView);

        database = FirebaseDatabase.getInstance("https://shopzadaproject-default-rtdb.asia-southeast1.firebasedatabase.app/");
        dataRef = database.getReference("Products");

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

        FirebaseRecyclerAdapter<ProductModel, ModeratorManagementViewHolder> adapter =
                new FirebaseRecyclerAdapter<ProductModel, ModeratorManagementViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ModeratorManagementViewHolder holder, int position, @NonNull ProductModel model) {
                        String pattern = "###,###,###.##";
                        DecimalFormat decimalFormat = new DecimalFormat(pattern);

                        holder.productNameLabel.setText(model.getName());
                        holder.productPriceLabel.setText("" + decimalFormat.format(model.getPrice()));
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
                        database.getReference("Users").child(model.getOwner()).child("verified").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if (task.isSuccessful()) {
                                    boolean verified = Boolean.parseBoolean(task.getResult().getValue().toString());
                                    if (verified) {
                                        holder.verifiedSymbol.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        });
                        Picasso.get().load(model.getImage()).into(holder.productImageView);
                        holder.suspendButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(mainActivity, "Suspended!", Toast.LENGTH_SHORT).show();
                                database.getReference("Products").child(model.getId()).removeValue();
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ModeratorManagementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.moderator_manage_view_layout, parent, false);
                        ModeratorManagementViewHolder managementViewHolder = new ModeratorManagementViewHolder(view);

                        return managementViewHolder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}