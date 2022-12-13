package com.utcc.shopzada.StoreComponent;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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
import com.utcc.shopzada.MainActivity;
import com.utcc.shopzada.Models.ProductModel;
import com.utcc.shopzada.Prevalent.Prevalent;
import com.utcc.shopzada.R;
import com.utcc.shopzada.ViewHolder.ApprovementViewHolder;
import com.utcc.shopzada.ViewHolder.UpdateProductViewHolder;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ManageProductFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ManageProductFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private MainActivity mainActivity;
    private RecyclerView recyclerView;
    private FirebaseDatabase database;
    private DatabaseReference dataRef;

    public ManageProductFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ManageProductFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ManageProductFragment newInstance(String param1, String param2) {
        ManageProductFragment fragment = new ManageProductFragment();
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
        View view = inflater.inflate(R.layout.fragment_manage_product, container, false);

        ImageView backPress = (ImageView) view.findViewById(R.id.backButton);
        mainActivity = (MainActivity) getActivity();
        recyclerView = (RecyclerView) view.findViewById(R.id.managementRecView);

        database = FirebaseDatabase.getInstance("https://shopzadaproject-default-rtdb.asia-southeast1.firebasedatabase.app/");
        dataRef = database.getReference("Users Products");

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
                        .setQuery(dataRef.child(Prevalent.currentOnlineUser.getUsername()), ProductModel.class)
                        .build();

        FirebaseRecyclerAdapter<ProductModel, UpdateProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<ProductModel, UpdateProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull UpdateProductViewHolder holder, int position, @NonNull ProductModel model) {
                        holder.productNameLabel.setText(model.getName());
                        holder.productPriceLabel.setText("" + model.getPrice());
                        holder.productAmountLabel.setText("" + model.getAmount());
                        holder.productDescriptionLabel.setText(model.getDescription());
                        Picasso.get().load(model.getImage()).into(holder.productImageView);
                        holder.updateButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                HashMap<String, Object> productMap = new HashMap<>();
                                productMap.put("amount", Integer.parseInt(holder.productAmountLabel.getText().toString()));
                                productMap.put("price", Integer.parseInt(holder.productPriceLabel.getText().toString()));

                                database.getReference("Users Products").child(model.getOwner())
                                        .child(model.getId()).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(mainActivity, "Updated!", Toast.LENGTH_SHORT).show();
                                            database.getReference("Products").child(model.getId()).updateChildren(productMap);
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(mainActivity, "Error: " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(mainActivity, "Deleted!", Toast.LENGTH_SHORT).show();
                                database.getReference("Users Products").child(model.getOwner()).child(model.getId()).removeValue();
                                database.getReference("Products").child(model.getId()).removeValue();
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public UpdateProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.update_product_view_layout, parent, false);
                        UpdateProductViewHolder updateProductViewHolder = new UpdateProductViewHolder(view);

                        return updateProductViewHolder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}