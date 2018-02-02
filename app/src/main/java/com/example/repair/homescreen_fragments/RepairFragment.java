package com.example.repair.homescreen_fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.example.repair.R;
import com.example.repair.ShopServicesList;
import com.example.repair.app.AppController;
import com.example.repair.pojo.RepairItem;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class RepairFragment extends Fragment {


    private RecyclerView recyclerView;
    private List<RepairItem> repairItemList;
    private RepairItemAdapter mAdapter;
    private ConstraintLayout location;
    private  BottomSheetDialog dialog;

    private TextInputEditText bottomSheetPincode;
    private TextView currentLocation;

    private Pincode PINCODE;
    private ShimmerFrameLayout mShimmerViewContainer;
    SharedPreferences.Editor editor;
    SharedPreferences sharedPreferences;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mShimmerViewContainer.startShimmerAnimation();
    }

    @Override
    public void onPause() {
        mShimmerViewContainer.stopShimmerAnimation();
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.repair_fragment, container, false);


        sharedPreferences = getActivity().getApplicationContext().getSharedPreferences("TAPAIR",0);
        editor = sharedPreferences.edit();





        mShimmerViewContainer = view.findViewById(R.id.shimmer_view_container);
        recyclerView = view.findViewById(R.id.repair_recycler_view);
        repairItemList = new ArrayList < > ();

        PINCODE = new Pincode();

        mAdapter = new RepairItemAdapter(getActivity(), repairItemList, PINCODE);

        location = view.findViewById(R.id.location);
        currentLocation = view.findViewById(R.id.current_location);


        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 3);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, 2, true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.setNestedScrollingEnabled(false);

        View bottomSheetView = getLayoutInflater().inflate(R.layout.location_bottom_sheet, null);
        dialog = new BottomSheetDialog(getContext());
        dialog.setContentView(bottomSheetView);
        bottomSheetPincode = bottomSheetView.findViewById(R.id.bottom_pincode);
        bottomSheetView.findViewById(R.id.location_okay).setOnClickListener( v -> {
            PINCODE.setPINCODE(bottomSheetPincode.getText().toString());
            currentLocation.setText(PINCODE.getPINCODE());
            editor.putString("PINCODE",PINCODE.getPINCODE());
            editor.commit();
            dialog.dismiss();
        });


        location.setOnClickListener(v -> dialog.show());

        fetchRepairItems();
        return view;
    }



    private void fetchRepairItems() {

        String URL = "https://repair-c8047.firebaseio.com/repairs/electrical.json";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                URL, null, new Response.Listener < JSONObject > () {

            @Override
            public void onResponse(JSONObject response) {
                List < RepairItem > repairItems = new ArrayList < > ();
                Iterator < String > iterator = response.keys();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    try {
                        JSONObject object = response.getJSONObject(key);
                        RepairItem item = new RepairItem();
                        item.setTitle(object.get("name").toString());
                        item.setImage(object.get("image").toString());
                        repairItems.add(item);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                repairItemList.clear();
                repairItemList.addAll(repairItems);
                mAdapter.notifyDataSetChanged();
                mShimmerViewContainer.stopShimmerAnimation();
                mShimmerViewContainer.setVisibility(View.GONE);
                String pin = sharedPreferences.getString("PINCODE",null);
                if(pin == null) {
                    dialog.show();
                } else {
                    PINCODE.setPINCODE(pin);
                    bottomSheetPincode.setText(PINCODE.getPINCODE());
                    currentLocation.setText(PINCODE.getPINCODE());
                }

            }
        },null);

        AppController.getInstance().addToRequestQueue(request);
    }

    public static class Pincode {
        private String PINCODE;

        public void setPINCODE(String PINCODE) {
            this.PINCODE = PINCODE;
        }

        public String getPINCODE() {
            return PINCODE;
        }
    }

}