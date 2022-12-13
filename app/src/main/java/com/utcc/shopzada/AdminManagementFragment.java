package com.utcc.shopzada;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminManagementFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminManagementFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AdminManagementFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdminManagementFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminManagementFragment newInstance(String param1, String param2) {
        AdminManagementFragment fragment = new AdminManagementFragment();
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
        View view = inflater.inflate(R.layout.fragment_admin_management, container, false);

        ImageView backPress = (ImageView) view.findViewById(R.id.backButton);
        MainActivity mainActivity = (MainActivity) getActivity();
        ConstraintLayout approvementPage = (ConstraintLayout) view.findViewById(R.id.approvementPage);
        ConstraintLayout managementPage = (ConstraintLayout) view.findViewById(R.id.managementPage);

        backPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.onBackPressed();
            }
        });

        approvementPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.replaceAndAddToBackStack(new ApprovementFragment(), "Approvement");
            }
        });

        managementPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.replaceAndAddToBackStack(new ProductManagementFragment(), "Management");
            }
        });

        return view;
    }
}