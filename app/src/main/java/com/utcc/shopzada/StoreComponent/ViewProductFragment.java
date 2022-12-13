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
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.utcc.shopzada.MainActivity;
import com.utcc.shopzada.Models.ProductModel;
import com.utcc.shopzada.Prevalent.Prevalent;
import com.utcc.shopzada.R;
import com.utcc.shopzada.ViewHolder.ModeratorManagementViewHolder;
import com.utcc.shopzada.ViewHolder.StoreProductsViewHolder;

import java.text.DecimalFormat;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ViewProductFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewProductFragment extends Fragment {

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

    public ViewProductFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ViewProductFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ViewProductFragment newInstance(String param1, String param2) {
        ViewProductFragment fragment = new ViewProductFragment();
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
        View view = inflater.inflate(R.layout.fragment_view_product, container, false);

        ImageView backPress = (ImageView) view.findViewById(R.id.backButton);
        MainActivity mainActivity = (MainActivity) getActivity();
        recyclerView = (RecyclerView) view.findViewById(R.id.viewProductRecView);

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

        FirebaseRecyclerAdapter<ProductModel, StoreProductsViewHolder> adapter =
                new FirebaseRecyclerAdapter<ProductModel, StoreProductsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull StoreProductsViewHolder holder, int position, @NonNull ProductModel model) {
                        String pattern = "###,###,###.##";
                        DecimalFormat decimalFormat = new DecimalFormat(pattern);

                        holder.productNameLabel.setText(model.getName());
                        holder.productPriceLabel.setText("" + decimalFormat.format(model.getPrice()));
                        holder.productAmountLabel.setText("" + model.getAmount());
                        holder.productDescriptionLabel.setText(model.getDescription());
                        Picasso.get().load(model.getImage()).into(holder.productImageView);
                    }

                    @NonNull
                    @Override
                    public StoreProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.store_products_view_layout, parent, false);
                        StoreProductsViewHolder storeProductsViewHolder = new StoreProductsViewHolder(view);

                        return storeProductsViewHolder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}