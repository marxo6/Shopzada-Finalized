package com.utcc.shopzada;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.utcc.shopzada.Models.ProductModel;
import com.utcc.shopzada.Prevalent.Prevalent;
import com.utcc.shopzada.ViewHolder.ProductViewHolder;

import java.text.DecimalFormat;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ExploreFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExploreFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FirebaseDatabase database;
    private DatabaseReference dataRef;
    private View view;
    private MainActivity mainActivity;
    private RecyclerView recyclerView;

    public ExploreFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ExploreFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExploreFragment newInstance(String param1, String param2) {
        ExploreFragment fragment = new ExploreFragment();
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
        view = inflater.inflate(R.layout.fragment_explore, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.productRecyclerView);
        mainActivity = (MainActivity) getActivity();

        ImageView cartButton = (ImageView) view.findViewById(R.id.cartIcon);
        ImageView cartNotify = (ImageView) view.findViewById(R.id.cartNotify);

        database = FirebaseDatabase.getInstance("https://shopzadaproject-default-rtdb.asia-southeast1.firebasedatabase.app/");
        dataRef = database.getReference("Products");

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(mainActivity, 2));

        database.getReference("Cart Lists").child("Users").child(Prevalent.currentOnlineUser.getUsername())
                        .child("Products").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            cartNotify.setVisibility(View.VISIBLE);
                        } else {
                            cartNotify.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.replaceAndAddToBackStack(new MyCartFragment(), "My Cart");
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<ProductModel> options =
                new FirebaseRecyclerOptions.Builder<ProductModel>()
                        .setQuery(dataRef, ProductModel.class)
                        .build();

        FirebaseRecyclerAdapter<ProductModel, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<ProductModel, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull ProductModel model) {
                        String pattern = "###,###,###.##";
                        DecimalFormat decimalFormat = new DecimalFormat(pattern);

                        holder.productNameLabel.setText(model.getName());
                        holder.productPriceLabel.setText("" + decimalFormat.format(model.getPrice()));
                        Picasso.get().load(model.getImage()).into(holder.productImageView);

                        holder.cardHolder.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ProductFragment productFragment = new ProductFragment();
                                Bundle bundle = new Bundle();
                                bundle.putString("productId", model.getId());
                                productFragment.setArguments(bundle);
                                mainActivity.replaceAndAddToBackStack(productFragment, "Id: " + model.getId());
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_view_layout, parent, false);
                        ProductViewHolder productViewHolder = new ProductViewHolder(view);

                        return productViewHolder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}