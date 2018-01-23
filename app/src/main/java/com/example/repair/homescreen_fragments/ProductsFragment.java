package com.example.repair.homescreen_fragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.repair.R;
import com.example.repair.pojo.Product;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProductsFragment extends Fragment {


    public ProductsFragment() {
        // Required empty public constructor
    }


    private SharedPreferences sharedPreferences;
    private RecyclerView productsRecyclerView;
    private ArrayList<Product> productItemsList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.products_fragment, container, false);


        sharedPreferences = getActivity().getApplicationContext().getSharedPreferences("TAPAIR",0);

/*



        productsRecyclerView = view.findViewById(R.id.products_recycler_view);
        productItemsList = new ArrayList< >();
        mAdapterk = new RepairFragment.RepairItemAdapter(getActivity(), repairItemList);

        location = view.findViewById(R.id.location);
        currentLocation = view.findViewById(R.id.current_location);


        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 3);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new RepairFragment.GridSpacingItemDecoration(2, dpToPx(2), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.setNestedScrollingEnabled(false);

        */
        return view;

    }

}
