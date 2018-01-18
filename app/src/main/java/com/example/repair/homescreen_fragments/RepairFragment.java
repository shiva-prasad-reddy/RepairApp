package com.example.repair.homescreen_fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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


    public RepairFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.repair_fragment, container, false);

        recyclerView = view.findViewById(R.id.repair_recycler_view);
        repairItemList = new ArrayList < > ();
        mAdapter = new RepairItemAdapter(getActivity(), repairItemList);

        location = view.findViewById(R.id.location);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 3);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(2), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.setNestedScrollingEnabled(false);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.location_bottom_sheet, null);
        dialog = new BottomSheetDialog(getContext());
        dialog.setContentView(bottomSheetView);

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        fetchRepairItems();
        return view;
    }



    private void fetchRepairItems() {

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog_style);


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
                // refreshing recycler view
                mAdapter.notifyDataSetChanged();
                progressDialog.cancel();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        AppController.getInstance().addToRequestQueue(request);
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    class RepairItemAdapter extends RecyclerView.Adapter < RepairItemAdapter.MyViewHolder > {
        private Context context;
        private List < RepairItem > repairItemList;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView name;
            public ImageView thumbnail;
            public CardView cardView;

            public MyViewHolder(final View view) {
                super(view);
                name = view.findViewById(R.id.title);
                thumbnail = view.findViewById(R.id.thumbnail);
                cardView = view.findViewById(R.id.card_view);

            }
        }


        public RepairItemAdapter(Context context, List < RepairItem > repairItemList) {
            this.context = context;
            this.repairItemList = repairItemList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.single_repair_item, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            final RepairItem repairItem = repairItemList.get(position);
            holder.name.setText(repairItem.getTitle());
            Glide.with(context).load(repairItem.getImage()).into(holder.thumbnail);
            holder.cardView.setContentDescription(repairItem.getTitle());
            holder.thumbnail.setContentDescription(repairItem.getTitle());
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent viewShopsList = new Intent(getContext(), ShopServicesList.class);
                    viewShopsList.putExtra("product_name",v.getContentDescription());
                    viewShopsList.putExtra("location","500044");
                    startActivity(viewShopsList);
                }
            });
            holder.thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent viewShopsList = new Intent(getContext(), ShopServicesList.class);
                    viewShopsList.putExtra("product_name",v.getContentDescription());
                    viewShopsList.putExtra("location","500044");
                    startActivity(viewShopsList);
                }
            });
        }

        @Override
        public int getItemCount() {
            return repairItemList.size();
        }
    }


    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

}