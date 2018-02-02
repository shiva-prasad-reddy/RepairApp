package com.example.repair.homescreen_fragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.repair.R;
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


    private RecyclerView appliances_recyclerView;
    private List<RepairItem> appliances_repairItemList;
    private RepairItemAdapter appliances_mAdapter;



    private RecyclerView computer_recyclerView;
    private List<RepairItem> computer_repairItemList;
    private RepairItemAdapter computer_mAdapter;



    private ConstraintLayout location;
    private  BottomSheetDialog dialog;
    private TextInputEditText bottomSheetPincode;
    private TextView currentLocation;

    private static final Pincode PINCODE = new Pincode();
    private ShimmerFrameLayout mShimmerViewContainer;
    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;

    private LinearLayout content;

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


        sharedPreferences = getActivity().getApplicationContext().getSharedPreferences("REPAIRAPP",0);
        editor = sharedPreferences.edit();


        content = view.findViewById(R.id.content);
        mShimmerViewContainer = view.findViewById(R.id.shimmer_view_container);
        location = view.findViewById(R.id.SHOP_ADDRESS);
        currentLocation = view.findViewById(R.id.current_location);



        appliances_recyclerView = view.findViewById(R.id.appliances_recycler_view);
        appliances_repairItemList = new ArrayList < > ();
        appliances_mAdapter = new RepairItemAdapter(getActivity(), appliances_repairItemList, PINCODE, "APPLIANCES");
        appliances_recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        appliances_recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, 2, true));
        appliances_recyclerView.setItemAnimator(new DefaultItemAnimator());
        appliances_recyclerView.setAdapter(appliances_mAdapter);
        appliances_recyclerView.setNestedScrollingEnabled(false);

        computer_recyclerView = view.findViewById(R.id.computer_repair_recycler_view);
        computer_repairItemList = new ArrayList < > ();
        computer_mAdapter = new RepairItemAdapter(getActivity(), computer_repairItemList, PINCODE, "COMPUTER_REPAIR");
        computer_recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        computer_recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, 2, true));
        computer_recyclerView.setItemAnimator(new DefaultItemAnimator());
        computer_recyclerView.setAdapter(computer_mAdapter);
        computer_recyclerView.setNestedScrollingEnabled(false);


        View bottomSheetView = getLayoutInflater().inflate(R.layout.location_bottom_sheet, null);
        dialog = new BottomSheetDialog(getContext());
        dialog.setContentView(bottomSheetView);
        bottomSheetPincode = bottomSheetView.findViewById(R.id.bottom_pincode);
        bottomSheetView.findViewById(R.id.done).setOnClickListener(v -> {
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

        String URL = "https://repair-c8047.firebaseio.com/REPAIR_APP.json";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                URL, null, new Response.Listener < JSONObject > () {

            @Override
            public void onResponse(JSONObject response) {

                List < RepairItem > appliances_repairItems = new ArrayList < > ();
                List < RepairItem > computer_repairItems = new ArrayList < > ();

                Iterator<String> itr = response.keys();
                while(itr.hasNext()) {
                    String type = itr.next();
                    switch(type) {
                        case "APPLIANCES":
                            try {
                                JSONObject obj = response.getJSONObject(type);
                                Iterator < String > iterator = obj.keys();
                                while (iterator.hasNext()) {
                                    String key = iterator.next();
                                    try {
                                        JSONObject object = obj.getJSONObject(key);
                                        RepairItem item = new RepairItem();
                                        item.setTitle(object.get("name").toString());
                                        item.setImage(object.get("image").toString());
                                        appliances_repairItems.add(item);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            break;
                        case "COMPUTER_REPAIR":
                            try {
                                JSONObject obj = response.getJSONObject(type);
                                Iterator < String > iterator = obj.keys();
                                while (iterator.hasNext()) {
                                    String key = iterator.next();
                                    try {
                                        JSONObject object = obj.getJSONObject(key);
                                        RepairItem item = new RepairItem();
                                        item.setTitle(object.get("name").toString());
                                        item.setImage(object.get("image").toString());
                                        computer_repairItems.add(item);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            break;
                    }
                }






                appliances_repairItemList.clear();
                appliances_repairItemList.addAll(appliances_repairItems);
                appliances_mAdapter.notifyDataSetChanged();


                computer_repairItemList.clear();
                computer_repairItemList.addAll(computer_repairItems);
                computer_mAdapter.notifyDataSetChanged();

                mShimmerViewContainer.stopShimmerAnimation();
                mShimmerViewContainer.setVisibility(View.GONE);
                content.setVisibility(View.VISIBLE);
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