package com.utcc.shopzada;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.utcc.shopzada.ViewHolder.ReceiptDisplayViewHolder;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CheckoutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CheckoutFragment extends Fragment {

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
    private DatabaseReference reference;
    private DecimalFormat decimalFormat;
    private int subTotal = 0, total = 0;
    private float vat = 0;
    private EditText addressEdit, codeEdit;
    private String address;
    private TextView subTotalLabel, discountLabel, vatLabel, totalLabel, pointsLabel;

    public CheckoutFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CheckoutFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CheckoutFragment newInstance(String param1, String param2) {
        CheckoutFragment fragment = new CheckoutFragment();
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
        View view = inflater.inflate(R.layout.fragment_checkout, container, false);
        mainActivity = (MainActivity) getActivity();
        ImageView backPress = (ImageView) view.findViewById(R.id.backButton);
        recyclerView = (RecyclerView) view.findViewById(R.id.receiptRecView);
        subTotalLabel = (TextView) view.findViewById(R.id.subTotalLabel);
        vatLabel = (TextView) view.findViewById(R.id.vatLabel);
        totalLabel = (TextView) view.findViewById(R.id.totalLabel);
        pointsLabel = (TextView) view.findViewById(R.id.pointsLabel);
        addressEdit = (EditText) view.findViewById(R.id.addressInput);
        codeEdit = (EditText) view.findViewById(R.id.discountInput);
        ConstraintLayout cashButton = (ConstraintLayout) view.findViewById(R.id.cashButton);
        ConstraintLayout pointsButton = (ConstraintLayout) view.findViewById(R.id.pointsButton);

        database = FirebaseDatabase.getInstance("https://shopzadaproject-default-rtdb.asia-southeast1.firebasedatabase.app/");
        reference = database.getReference("Cart Lists");

        backPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.onBackPressed();
            }
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mainActivity));

        String pattern = "###,###,###.##";
        decimalFormat = new DecimalFormat(pattern);

        cashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                placeOrderCash();
            }
        });

        return view;
    }

    private void placeOrderCash() {
        address = addressEdit.getText().toString();

        if (address.isEmpty()) {
            Toast.makeText(mainActivity, "Address cannot be empty!", Toast.LENGTH_SHORT).show();
            addressEdit.requestFocus();
            return;
        } else {
            confirmOrder();
        }
    }

    private void confirmOrder() {
        String currentDate, currentTime;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        currentDate = dateFormat.format(calendar.getTime());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss a");
        currentTime = timeFormat.format(calendar.getTime());
        String orderId = System.currentTimeMillis() + "_" + Prevalent.currentOnlineUser.getUsername();
        DatabaseReference orderRef = database.getReference("Orders");
        HashMap<String, Object> orderDataMap = new HashMap<>();
        orderDataMap.put("id", orderId);
        orderDataMap.put("owner", Prevalent.currentOnlineUser.getUsername());
        orderDataMap.put("address", address);
        orderDataMap.put("total", (subTotal + vat));
        orderDataMap.put("date", currentDate);
        orderDataMap.put("time", currentTime);
        orderDataMap.put("shipment", "Not Shipped");

        orderRef.child(orderId).updateChildren(orderDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    database.getReference("Users Orders").child(Prevalent.currentOnlineUser.getUsername()).child(orderId).updateChildren(orderDataMap);
                    database.getReference("Cart Lists").child("Users").child(Prevalent.currentOnlineUser.getUsername())
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(mainActivity, "Your order has been placed!", Toast.LENGTH_SHORT).show();
                                        mainActivity.onBackPressed();
                                        mainActivity.onBackPressed();
                                    }
                                }
                            });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<ProductModel> options =
                new FirebaseRecyclerOptions.Builder<ProductModel>()
                        .setQuery(reference.child("Users").child(Prevalent.currentOnlineUser.getUsername()).child("Products"), ProductModel.class)
                        .build();

        FirebaseRecyclerAdapter<ProductModel, ReceiptDisplayViewHolder> adapter =
                new FirebaseRecyclerAdapter<ProductModel, ReceiptDisplayViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ReceiptDisplayViewHolder holder, int position, @NonNull ProductModel model) {
                        Integer totalEach = model.getPrice() * model.getAmount();
                        subTotal += totalEach;
                        vat += (float) (subTotal * 0.07);

                        subTotalLabel.setText(decimalFormat.format(subTotal));
                        vatLabel.setText(decimalFormat.format(vat));
                        totalLabel.setText(decimalFormat.format(subTotal + vat));
                        pointsLabel.setText("Use " + decimalFormat.format((subTotal + vat) * 2) + " Points");

                        holder.productNameLabel.setText(model.getName());
                        holder.productPriceLabel.setText(decimalFormat.format(totalEach));
                        holder.productAmountLabel.setText("" + model.getAmount());
                    }

                    @NonNull
                    @Override
                    public ReceiptDisplayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.receipt_item_layout, parent, false);
                        ReceiptDisplayViewHolder receiptDisplayViewHolder = new ReceiptDisplayViewHolder(view);

                        return receiptDisplayViewHolder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}