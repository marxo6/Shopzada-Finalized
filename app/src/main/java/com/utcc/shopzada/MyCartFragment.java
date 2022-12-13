package com.utcc.shopzada;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.utcc.shopzada.Models.ProductModel;
import com.utcc.shopzada.Models.UserModel;
import com.utcc.shopzada.Prevalent.Prevalent;
import com.utcc.shopzada.ViewHolder.CartViewHolder;
import com.utcc.shopzada.ViewHolder.UpdateProductViewHolder;

import org.checkerframework.checker.units.qual.C;

import java.text.DecimalFormat;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyCartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyCartFragment extends Fragment {

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

    public MyCartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyCartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyCartFragment newInstance(String param1, String param2) {
        MyCartFragment fragment = new MyCartFragment();
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
        View view = inflater.inflate(R.layout.fragment_my_cart, container, false);
        mainActivity = (MainActivity) getActivity();
        ImageView backPress = (ImageView) view.findViewById(R.id.backButton);
        recyclerView = (RecyclerView) view.findViewById(R.id.cartView);
        ConstraintLayout checkoutBox = (ConstraintLayout) view.findViewById(R.id.checkoutBox);
        ImageView checkoutIcon = (ImageView) view.findViewById(R.id.checkoutIcon);

        database = FirebaseDatabase.getInstance("https://shopzadaproject-default-rtdb.asia-southeast1.firebasedatabase.app/");
        dataRef = database.getReference("Cart Lists");

        backPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.onBackPressed();
            }
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mainActivity));

        checkoutBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkout();
            }
        });

        checkoutIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkout();
            }
        });

        return view;
    }

    private void checkout() {
        dataRef.child("Users").child(Prevalent.currentOnlineUser.getUsername()).child("Products").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    mainActivity.replaceAndAddToBackStack(new CheckoutFragment(), "Checkout");
                } else {
                    Toast.makeText(mainActivity, "Your cart is empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<ProductModel> options =
                new FirebaseRecyclerOptions.Builder<ProductModel>()
                        .setQuery(dataRef.child("Users").child(Prevalent.currentOnlineUser.getUsername()).child("Products"), ProductModel.class)
                        .build();

        FirebaseRecyclerAdapter<ProductModel, CartViewHolder> adapter =
                new FirebaseRecyclerAdapter<ProductModel, CartViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull ProductModel model) {
                        String pattern = "###,###,###.##";
                        DecimalFormat decimalFormat = new DecimalFormat(pattern);

                        holder.productNameLabel.setText(model.getName());
                        holder.productPriceLabel.setText(decimalFormat.format(model.getPrice()));
                        holder.productAmountLabel.setText("" + model.getAmount());
                        Picasso.get().load(model.getImage()).into(holder.productImageView);
                        holder.productOwnerLabel.setText(model.getOwner());
                        database.getReference("Users").child(model.getOwner()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    UserModel userModel = snapshot.getValue(UserModel.class);
                                    if (snapshot.child("imageUrl").exists() && !userModel.getImageUrl().toString().equalsIgnoreCase("")) {
                                        Picasso.get().load(userModel.getImageUrl()).into(holder.productOwnerImageView);
                                    }
                                    if (userModel.isVerified()) {
                                        holder.verifiedSymbol.setVisibility(View.VISIBLE);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        holder.removeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dataRef.child("Users").child(Prevalent.currentOnlineUser.getUsername()).child("Products")
                                        .child(model.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(mainActivity, "Removed!", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_view_layout, parent, false);
                        CartViewHolder cartViewHolder = new CartViewHolder(view);

                        return cartViewHolder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}