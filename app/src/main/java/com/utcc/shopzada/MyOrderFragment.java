package com.utcc.shopzada;

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
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.utcc.shopzada.Models.OrderModel;
import com.utcc.shopzada.Models.ProductModel;
import com.utcc.shopzada.Models.UserModel;
import com.utcc.shopzada.Prevalent.Prevalent;
import com.utcc.shopzada.ViewHolder.CartViewHolder;
import com.utcc.shopzada.ViewHolder.OrderStatusViewHolder;

import java.text.DecimalFormat;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyOrderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyOrderFragment extends Fragment {

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

    public MyOrderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyOrderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyOrderFragment newInstance(String param1, String param2) {
        MyOrderFragment fragment = new MyOrderFragment();
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
        View view = inflater.inflate(R.layout.fragment_my_order, container, false);
        mainActivity = (MainActivity) getActivity();
        recyclerView = (RecyclerView) view.findViewById(R.id.orderRecyclerView);
        ImageView backPress = (ImageView) view.findViewById(R.id.backButton);

        database = FirebaseDatabase.getInstance("https://shopzadaproject-default-rtdb.asia-southeast1.firebasedatabase.app/");
        reference = database.getReference("Users Orders");

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

        FirebaseRecyclerOptions<OrderModel> options =
                new FirebaseRecyclerOptions.Builder<OrderModel>()
                        .setQuery(reference.child(Prevalent.currentOnlineUser.getUsername()), OrderModel.class)
                        .build();

        FirebaseRecyclerAdapter<OrderModel, OrderStatusViewHolder> adapter =
                new FirebaseRecyclerAdapter<OrderModel, OrderStatusViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull OrderStatusViewHolder holder, int position, @NonNull OrderModel model) {
                        String pattern = "###,###,###.##";
                        DecimalFormat decimalFormat = new DecimalFormat(pattern);

                        holder.orderId.setText(model.getId());
                        holder.orderDate.setText(model.getDate());
                        holder.orderTime.setText(model.getTime());
                        holder.orderStatus.setText(model.getShipment());
                        holder.orderAddress.setText(model.getAddress());
                        holder.orderOwner.setText(model.getOwner());
                    }

                    @NonNull
                    @Override
                    public OrderStatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orderview_layout, parent, false);
                        OrderStatusViewHolder orderStatusViewHolder = new OrderStatusViewHolder(view);

                        return orderStatusViewHolder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}