package com.utcc.shopzada;

import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bluehomestudio.luckywheel.LuckyWheel;
import com.bluehomestudio.luckywheel.OnLuckyWheelReachTheTarget;
import com.bluehomestudio.luckywheel.WheelItem;

import java.util.ArrayList;
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
    List<WheelItem> wheelItemList;
    String points;

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

        MainActivity mainActivity = (MainActivity) getActivity();
        ImageView backPress = (ImageView) view.findViewById(R.id.backButton);
        ConstraintLayout spinButton = (ConstraintLayout) view.findViewById(R.id.spinButton);
        LuckyWheel luckyWheel = (LuckyWheel) view.findViewById(R.id.luckyWheelSpinner);

        wheelItemList = new ArrayList<>();

        WheelItem wheelItem_1 = new WheelItem(ResourcesCompat.getColor(getResources(),
                R.color.mainRed, null), BitmapFactory.decodeResource(getResources(),
                R.drawable.ticket), "10% Discount");
        wheelItemList.add(wheelItem_1);

        WheelItem wheelItem_2 = new WheelItem(ResourcesCompat.getColor(getResources(),
                R.color.mainRedVariant, null), BitmapFactory.decodeResource(getResources(),
                R.drawable.ticket), "100 Points");
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
                R.drawable.ticket), "5% Discount");
        wheelItemList.add(wheelItem_6);

        luckyWheel.addWheelItems(wheelItemList);

        luckyWheel.setLuckyWheelReachTheTarget(new OnLuckyWheelReachTheTarget() {
            @Override
            public void onReachTarget() {
                WheelItem itemSelected = wheelItemList.get(Integer.parseInt(points) - 1);
                String reward = itemSelected.text;

                Toast.makeText(mainActivity, reward, Toast.LENGTH_SHORT).show();
            }
        });

        spinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Random random = new Random();
                points = String.valueOf(random.nextInt(6));
                if (points.equals("0")) {
                    points = String.valueOf(1);
                }
                luckyWheel.rotateWheelTo(Integer.parseInt(points));
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