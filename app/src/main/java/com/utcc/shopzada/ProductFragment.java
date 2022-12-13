package com.utcc.shopzada;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
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

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProductFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProductFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String productId;
    private MainActivity mainActivity;
    private FirebaseDatabase database;
    private DatabaseReference productRef;
    private TextView productNameHeader, productNameLabel, productDescriptionLabel, productOwnerLabel, amountValue, productPriceLabel;
    private ConstraintLayout increase, decrease, addToCartButton, followButton, followedButton;
    private ImageView productImageView, noImageIcon, verifiedSymbol;
    private CircleImageView productOwnerImageView;
    private int amount = 1;

    public ProductFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProductFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProductFragment newInstance(String param1, String param2) {
        ProductFragment fragment = new ProductFragment();
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
        View view = inflater.inflate(R.layout.fragment_product, container, false);
        mainActivity = (MainActivity) getActivity();
        ImageView backPress = (ImageView) view.findViewById(R.id.backButton);
        productNameHeader = (TextView) view.findViewById(R.id.productNameHeader);
        productNameLabel = (TextView) view.findViewById(R.id.productNameLabel);
        productDescriptionLabel = (TextView) view.findViewById(R.id.productDescriptionLabel);
        productOwnerLabel = (TextView) view.findViewById(R.id.productOwnerLabel);
        productPriceLabel = (TextView) view.findViewById(R.id.productPriceLabel);
        productImageView = (ImageView) view.findViewById(R.id.productImageView);
        productOwnerImageView = (CircleImageView) view.findViewById(R.id.productOwnerImageView);
        noImageIcon = (ImageView) view.findViewById(R.id.noImageIcon);
        verifiedSymbol = (ImageView) view.findViewById(R.id.verifiedSymbol);
        amountValue = (TextView) view.findViewById(R.id.amountValue);
        decrease = (ConstraintLayout) view.findViewById(R.id.decreaseButton);
        increase = (ConstraintLayout) view.findViewById(R.id.increaseButton);
        addToCartButton = (ConstraintLayout) view.findViewById(R.id.addToCartButton);
        followButton = (ConstraintLayout) view.findViewById(R.id.followButton);
        followedButton = (ConstraintLayout) view.findViewById(R.id.followedButton);

        productId = getArguments().getString("productId");
        database = FirebaseDatabase.getInstance("https://shopzadaproject-default-rtdb.asia-southeast1.firebasedatabase.app/");
        productRef = database.getReference("Products");

        backPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.onBackPressed();
            }
        });

        decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (amount > 1) {
                    amount -= 1;
                    amountValue.setText("" + amount);
                } else {
                    Toast.makeText(mainActivity, "Value cannot be less than 1!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (amount < 10) {
                    amount += 1;
                    amountValue.setText("" + amount);
                } else {
                    Toast.makeText(mainActivity, "Value cannot be more than 10!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItemToCart();
            }
        });

        getProductDetails(productId);

        return view;
    }

    private void addItemToCart() {
        String currentTime, currentDate;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        currentDate = dateFormat.format(calendar.getTime());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss a");
        currentTime = timeFormat.format(calendar.getTime());

        DatabaseReference cartRef = database.getReference("Cart Lists");
        HashMap<String, Object> cartDataMap = new HashMap<>();
        productRef.child(productId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    ProductModel model = snapshot.getValue(ProductModel.class);
                    cartDataMap.put("id", model.getId());
                    cartDataMap.put("name", model.getName());
                    cartDataMap.put("amount", amount);
                    cartDataMap.put("price", model.getPrice());
                    cartDataMap.put("owner", model.getOwner());
                    cartDataMap.put("image", model.getImage());
                    cartDataMap.put("date", currentDate);
                    cartDataMap.put("time", currentTime);

                    cartRef.child("Users").child(Prevalent.currentOnlineUser.getUsername()).child("Products")
                            .child(model.getId()).updateChildren(cartDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(mainActivity, "Added to cart!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getProductDetails(String productId) {
        productRef.child(productId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    ProductModel productModel = snapshot.getValue(ProductModel.class);
                    String pattern = "###,###,###.##";
                    DecimalFormat decimalFormat = new DecimalFormat(pattern);

                    productNameHeader.setText(productModel.getName());
                    productNameLabel.setText(productModel.getName());
                    productDescriptionLabel.setText(productModel.getDescription());
                    productOwnerLabel.setText(productModel.getOwner());
                    productPriceLabel.setText(decimalFormat.format(productModel.getPrice()));
                    if (productModel.getImage() != null) {
                        noImageIcon.setVisibility(View.GONE);
                        productImageView.setVisibility(View.VISIBLE);
                        Picasso.get().load(productModel.getImage()).into(productImageView);
                    }
                    if (!(productModel.getOwner()).equalsIgnoreCase(Prevalent.currentOnlineUser.getUsername())) {
                        database.getReference("Following").child(productModel.getOwner()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                snapshot = snapshot.child(Prevalent.currentOnlineUser.getUsername());
                                if (snapshot.exists()) {
                                    followedButton.setVisibility(View.VISIBLE);
                                    followButton.setVisibility(View.GONE);
                                } else {
                                    followedButton.setVisibility(View.GONE);
                                    followButton.setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    database.getReference("Users").child(productModel.getOwner()).child("imageUrl").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (task.isSuccessful() && !task.getResult().getValue().toString().equalsIgnoreCase("")) {
                                Picasso.get().load(task.getResult().getValue().toString()).into(productOwnerImageView);
                            }
                        }
                    });
                    database.getReference("Users").child(productModel.getOwner()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                UserModel userModel = snapshot.getValue(UserModel.class);
                                if (snapshot.child("imageUrl").exists() && !snapshot.child("imageUrl").getValue().toString().equalsIgnoreCase("")) {
                                    Picasso.get().load(userModel.getImageUrl()).into(productOwnerImageView);
                                }
                                if (userModel.isVerified()) {
                                    verifiedSymbol.setVisibility(View.VISIBLE);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    followButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            follow(productModel.getOwner());
                        }
                    });

                    followedButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            unfollow(productModel.getOwner());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void follow(String user) {
        DatabaseReference followRef = database.getReference("Following").child(user);
        DatabaseReference chatRef = database.getReference("Chat").child(Prevalent.currentOnlineUser.getUsername());

        HashMap<String, Object> followDataMap = new HashMap<>();
        followDataMap.put(Prevalent.currentOnlineUser.getUsername(), true);

        HashMap<String, Object> chatDataMap = new HashMap<>();
        chatDataMap.put("id", user);
        chatDataMap.put("chatable", true);

        followRef.updateChildren(followDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(mainActivity, "Followed!", Toast.LENGTH_SHORT).show();
                    followButton.setVisibility(View.GONE);
                    followedButton.setVisibility(View.VISIBLE);
                }
            }
        });

        chatRef.child(user).updateChildren(chatDataMap);
    }

    private void unfollow(String user) {
        DatabaseReference followRef = database.getReference("Following").child(user);
        DatabaseReference chatRef = database.getReference("Chat").child(Prevalent.currentOnlineUser.getUsername());

        followRef.child(Prevalent.currentOnlineUser.getUsername()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(mainActivity, "Unfollowed!", Toast.LENGTH_SHORT).show();
                    followButton.setVisibility(View.VISIBLE);
                    followedButton.setVisibility(View.GONE);
                }
            }
        });

        chatRef.child(user).removeValue();
    }
}