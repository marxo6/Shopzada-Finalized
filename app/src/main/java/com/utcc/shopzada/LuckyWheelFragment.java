package com.utcc.shopzada;

import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bluehomestudio.luckywheel.LuckyWheel;
import com.bluehomestudio.luckywheel.OnLuckyWheelReachTheTarget;
import com.bluehomestudio.luckywheel.WheelItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.utcc.shopzada.Prevalent.Prevalent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LuckyWheelFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LuckyWheelFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private boolean discount;
    private List<WheelItem> wheelItemList;
    private String points;
    private int value;
    private FirebaseDatabase database;
    private DatabaseReference reference;

    public LuckyWheelFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LuckyWheelFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LuckyWheelFragment newInstance(String param1, String param2) {
        LuckyWheelFragment fragment = new LuckyWheelFragment();
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
        View view = inflater.inflate(R.layout.fragment_lucky_wheel, container, false);

        database = FirebaseDatabase.getInstance("https://shopzadaproject-default-rtdb.asia-southeast1.firebasedatabase.app/");
        reference = database.getReference("Users");

        MainActivity mainActivity = (MainActivity) getActivity();
        ImageView backPress = (ImageView) view.findViewById(R.id.backButton);
        ConstraintLayout spinButton = (ConstraintLayout) view.findViewById(R.id.spinButton);
        LuckyWheel luckyWheel = (LuckyWheel) view.findViewById(R.id.luckyWheelSpinner);
        TextView remainLuckyWheel = (TextView) view.findViewById(R.id.remainLuckyWheel);

        wheelItemList = new ArrayList<>();

        WheelItem wheelItem_1 = new WheelItem(ResourcesCompat.getColor(getResources(),
                R.color.mainRed, null), BitmapFactory.decodeResource(getResources(),
                R.drawable.ticket), "10% Discount");
        wheelItemList.add(wheelItem_1);

        WheelItem wheelItem_2 = new WheelItem(ResourcesCompat.getColor(getResources(),
                R.color.mainRedVariant, null), BitmapFactory.decodeResource(getResources(),
                R.drawable.ticket), "5% Discount");
        wheelItemList.add(wheelItem_2);

        WheelItem wheelItem_3 = new WheelItem(ResourcesCompat.getColor(getResources(),
                R.color.mainRed, null), BitmapFactory.decodeResource(getResources(),
                R.drawable.ticket), "15% Discount");
        wheelItemList.add(wheelItem_3);

        WheelItem wheelItem_4 = new WheelItem(ResourcesCompat.getColor(getResources(),
                R.color.mainRedVariant, null), BitmapFactory.decodeResource(getResources(),
                R.drawable.ticket), "150 Points");
        wheelItemList.add(wheelItem_4);

        WheelItem wheelItem_5 = new WheelItem(ResourcesCompat.getColor(getResources(),
                R.color.mainRed, null), BitmapFactory.decodeResource(getResources(),
                R.drawable.ticket), "50 Points");
        wheelItemList.add(wheelItem_5);

        WheelItem wheelItem_6 = new WheelItem(ResourcesCompat.getColor(getResources(),
                R.color.mainRedVariant, null), BitmapFactory.decodeResource(getResources(),
                R.drawable.ticket), "100 Points");
        wheelItemList.add(wheelItem_6);

        luckyWheel.addWheelItems(wheelItemList);
        remainLuckyWheel.setText("Lucky Points : " + Prevalent.currentOnlineUser.getLuckypoints());

        luckyWheel.setLuckyWheelReachTheTarget(new OnLuckyWheelReachTheTarget() {
            @Override
            public void onReachTarget() {
                WheelItem itemSelected = wheelItemList.get(Integer.parseInt(points) - 1);
                String reward = itemSelected.text;
                discount = true;

                if (Integer.parseInt(points) > 3) {
                    discount = false;
                }

                switch (Integer.parseInt(points)) {
                    case 1:
                        value = 10;
                        break;
                    case 2:
                        value = 5;
                        break;
                    case 3:
                        value = 15;
                        break;
                    case 4:
                        value = 150;
                        break;
                    case 5:
                        value = 50;
                        break;
                    case 6:
                        value = 100;
                        break;
                }

                String couponId = String.valueOf(System.currentTimeMillis());

                database.getReference("Coupons").child(Prevalent.currentOnlineUser.getUsername()).child("Code").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.hasChild(couponId)) {
                            String date, time;
                            Calendar calendar = Calendar.getInstance();
                            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
                            date = dateFormat.format(calendar.getTime());
                            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss a");
                            time = timeFormat.format(calendar.getTime());

                            HashMap<String, Object> couponDataMap = new HashMap<>();
                            couponDataMap.put("id", couponId);
                            couponDataMap.put("discount", discount);
                            couponDataMap.put("value", value);
                            couponDataMap.put("date", date);
                            couponDataMap.put("time", time);

                            database.getReference("Coupons").child(Prevalent.currentOnlineUser.getUsername())
                                    .child("Code").child(couponId).updateChildren(couponDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(mainActivity, "Coupon added!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                Toast.makeText(mainActivity, reward, Toast.LENGTH_SHORT).show();
            }
        });

        spinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Prevalent.currentOnlineUser.getLuckypoints() > 0) {
                    Random random = new Random();
                    points = String.valueOf(random.nextInt(6));
                    if (points.equals("0")) {
                        points = String.valueOf(1);
                    }
                    luckyWheel.rotateWheelTo(Integer.parseInt(points));
                    int remain = Prevalent.currentOnlineUser.getLuckypoints() - 1;
                    Prevalent.currentOnlineUser.setLuckypoints(remain);
                    HashMap<String, Object> luckyDataMap = new HashMap<>();
                    luckyDataMap.put("luckypoints", remain);
                    reference.child(Prevalent.currentOnlineUser.getUsername()).updateChildren(luckyDataMap);
                    remainLuckyWheel.setText("Lucky Points : " + remain);
                } else {
                    Toast.makeText(mainActivity, "Not enough lucky point!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        backPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.onBackPressed();
            }
        });

        return view;
    }
}