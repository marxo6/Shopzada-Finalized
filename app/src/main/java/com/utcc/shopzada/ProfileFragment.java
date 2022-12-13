package com.utcc.shopzada;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.utcc.shopzada.Prevalent.Prevalent;

import java.text.DecimalFormat;

import de.hdodenhof.circleimageview.CircleImageView;
import me.tankery.lib.circularseekbar.CircularSeekBar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FirebaseDatabase database;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        MainActivity mainActivity = (MainActivity) getActivity();
        ConstraintLayout luckyWheel = (ConstraintLayout) view.findViewById(R.id.luckyWheelPage);
        ConstraintLayout coupons = (ConstraintLayout) view.findViewById(R.id.myCouponsPage);
        ConstraintLayout account = (ConstraintLayout) view.findViewById(R.id.myAccountPage);
        ConstraintLayout store = (ConstraintLayout) view.findViewById(R.id.myStorePage);
        ConstraintLayout admin = (ConstraintLayout) view.findViewById(R.id.adminPage);
        ConstraintLayout orderPage = (ConstraintLayout) view.findViewById(R.id.orderPage);
        ImageView cartButton = (ImageView) view.findViewById(R.id.cartIcon);
        ImageView settingsButton = (ImageView) view.findViewById(R.id.settingsIcon);
        ImageView cartNotify = (ImageView) view.findViewById(R.id.cartNotify);
        TextView userIdLabel = (TextView) view.findViewById(R.id.userId);
        TextView userLevelLabel = (TextView) view.findViewById(R.id.userLevel);
        TextView userFollowingLabel = (TextView) view.findViewById(R.id.followingCount);
        TextView userLikesLabel = (TextView) view.findViewById(R.id.likesCount);
        TextView userLevelPointsLabel = (TextView) view.findViewById(R.id.expCount);
        TextView userCreditsLabel = (TextView) view.findViewById(R.id.userCredits);
        ImageView verifiedBadge = (ImageView) view.findViewById(R.id.verifiedSymbol);
        CircleImageView userImageDisplay = (CircleImageView) view.findViewById(R.id.userImage);
        CircularSeekBar levelProgress = (CircularSeekBar) view.findViewById(R.id.circularSeekBar);

        database = FirebaseDatabase.getInstance("https://shopzadaproject-default-rtdb.asia-southeast1.firebasedatabase.app/");

        userIdLabel.setText(Prevalent.currentOnlineUser.getUsername());
        userLevelLabel.setText("Level " + Prevalent.currentOnlineUser.getLevel());
        userLevelPointsLabel.setText("" + Prevalent.currentOnlineUser.getLevelpoints());
        if (!(Prevalent.currentOnlineUser.getImageUrl().equals("")) &&
            !(Prevalent.currentOnlineUser.getImageUrl().equals("0")) &&
            Prevalent.currentOnlineUser.getImageUrl() != null) {
            Picasso.get().load(Prevalent.currentOnlineUser.getImageUrl()).into(userImageDisplay);
        }
        if (Prevalent.currentOnlineUser.isVerified()) {
            verifiedBadge.setVisibility(View.VISIBLE);
        }
        if (Prevalent.currentOnlineUser.getPermission().equalsIgnoreCase("seller") ||
            Prevalent.currentOnlineUser.getPermission().equalsIgnoreCase("moderator")) {
            store.setVisibility(View.VISIBLE);
        }
        if (Prevalent.currentOnlineUser.getPermission().equalsIgnoreCase("moderator")) {
            admin.setVisibility(View.VISIBLE);
        }
        float levelPoint = Float.valueOf(Prevalent.currentOnlineUser.getLevelpoints());
        float max = 2000.0f;
        levelProgress.setProgress(Math.round((levelPoint / max)*100));

        String pattern = "###,###,###.##";
        DecimalFormat decimalFormat = new DecimalFormat(pattern);
        userCreditsLabel.setText("" + decimalFormat.format(Prevalent.currentOnlineUser.getCredits()));

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

        luckyWheel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.replaceAndAddToBackStack(new LuckyWheelFragment(), "Lucky");
            }
        });

        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.replaceAndAddToBackStack(new MyCartFragment(), "My Cart");
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.replaceAndAddToBackStack(new SettingFragment(), "Settings");
            }
        });

        coupons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.replaceAndAddToBackStack(new MyCouponsFragment(), "My Coupons");
            }
        });

        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.replaceAndAddToBackStack(new MyAccountFragment(), "My Account");
            }
        });

        store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.replaceAndAddToBackStack(new MyStoreFragment(), "My Store");
            }
        });

        admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.replaceAndAddToBackStack(new AdminManagementFragment(), "Admin Management");
            }
        });

        orderPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.replaceAndAddToBackStack(new MyOrderFragment(), "My Order");
            }
        });

        return view;
    }
}