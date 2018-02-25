package com.example.repair;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.example.repair.app.AppController;
import com.example.repair.pojo.ShopDetails;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.github.florent37.expansionpanel.ExpansionHeader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class ShopServicesList extends AppCompatActivity {


    private RecyclerView recyclerView;
    private static final ArrayList<ShopDetails> shopsList = new ArrayList<>();
    private ShopServicesList.ShopsListAdapter mAdapter;

    private ShimmerFrameLayout mShimmerViewContainer;
    private String PINCODE, TYPE, PRODUCT_NAME;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_services_list);
        mShimmerViewContainer = findViewById(R.id.shimmer_view_container);




        Intent intent = getIntent();
        PINCODE = intent.getStringExtra("PINCODE");
        TYPE = intent.getStringExtra("TYPE");
        PRODUCT_NAME = intent.getStringExtra("PRODUCT_NAME");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(PRODUCT_NAME);
        actionBar.setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.repair_shop_services_list);
        mAdapter = new ShopsListAdapter(this, shopsList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(mAdapter);

        fetchRepairServiceShopsItems();
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

    public void fetchRepairServiceShopsItems() {


        String URL = "https://repair-c8047.firebaseio.com/SHOPS/" + PINCODE + "/" + TYPE + ".json";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener < JSONObject > () {

            @Override
            public void onResponse(JSONObject response) {

                ArrayList<ShopDetails> list = new ArrayList<>();
                Iterator < String > iterator = response.keys();
                while (iterator.hasNext()) {

                    String key = iterator.next();
                    try {
                        JSONObject object = response.getJSONObject(key);
                        ArrayList<String> DEVICES = new ArrayList<>();
                        JSONArray array = object.getJSONArray("DEVICES");
                        for(int i = 0; i < array.length(); i++) {
                            DEVICES.add(array.getString(i));
                        }
                        if(DEVICES.contains(PRODUCT_NAME)) {
                            ShopDetails item = new ShopDetails();
                            item.SHOP_ID = key;
                            item.SHOP_NAME = object.getString("SHOP_NAME");
                            item.OWNER_NAME = object.getString("OWNER_NAME");
                            item.OWNER_IMAGE = object.getString("OWNER_IMAGE");
                            item.CONTACT_NUMBER = object.getString("CONTACT_NUMBER");
                            item.STATUS = object.getString("STATUS");

                            item.AUTHORISED = object.getString("AUTHORISED").equals("YES") ? true : false;
                            if(item.AUTHORISED) {
                                ArrayList<String> services = new ArrayList<>();
                                JSONArray ar = object.getJSONArray("SERVICE_BRANDS");
                                for(int i = 0; i < ar.length(); i++) {
                                    services.add(ar.getString(i));
                                }
                                item.SERVICE_BRANDS.addAll(services);
                            }


                            //SHOP_ADDRESS, PINCODE, HOURS_OF_OPERATION, MAIL, WEBSITE, SHOP_IMAGE, DEVICES
                            item.SHOP_ADDRESS = object.getString("SHOP_ADDRESS");
                            item.PINCODE = object.getString("PINCODE");
                            item.HOURS_OF_OPERATION = object.getString("HOURS_OF_OPERATION");
                            item.MAIL = object.getString("MAIL");
                            item.WEBSITE = object.getString("WEBSITE");
                            item.SHOP_IMAGE = object.getString("SHOP_IMAGE");
                            item.DEVICES.addAll(DEVICES);




                            list.add(item);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                shopsList.clear();
                shopsList.addAll(list);
                mAdapter.notifyDataSetChanged();
                // stop animating Shimmer and hide the layout
                mShimmerViewContainer.stopShimmerAnimation();
                mShimmerViewContainer.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ShopServicesList.this, "No Services Points found in your location", Toast.LENGTH_SHORT).show();
            }
        });

        AppController.getInstance().addToRequestQueue(request);
    }

    public class ShopsListAdapter extends RecyclerView.Adapter < ShopsListAdapter.MyShopViewHolder > {

        private Context context;
        private ArrayList <ShopDetails> shopItemsList;

        public class MyShopViewHolder extends RecyclerView.ViewHolder {

            public ImageView owner_image, call, next, authorised;
            public TextView shop_name, owner_name, contact_number;
            public ListView service_brands;
            public LinearLayout service_brands_layout;
            public CardView ShopCard;
            public ExpansionHeader expansionHeader;

            public MyShopViewHolder(View itemView) {
                super(itemView);
                owner_image = itemView.findViewById(R.id.OWNER_IMAGE);
                authorised = itemView.findViewById(R.id.authorised);
                call = itemView.findViewById(R.id.BUY);
                next = itemView.findViewById(R.id.next);
                shop_name = itemView.findViewById(R.id.SHOP_NAME);
                owner_name = itemView.findViewById(R.id.OWNER_NAME);
                contact_number = itemView.findViewById(R.id.product_name);
                service_brands = itemView.findViewById(R.id.service_brands);
                service_brands_layout = itemView.findViewById(R.id.service_brands_layout);
                ShopCard = itemView.findViewById(R.id.shop_card);
                expansionHeader = itemView.findViewById(R.id.expansion_header);
            }
        }

        public ShopsListAdapter(Context context, ArrayList <ShopDetails> shopsList) {
            this.context = context;
            this.shopItemsList = shopsList;

        }

        @Override
        public MyShopViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_repair_shop_list_item, parent, false);
            return new MyShopViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyShopViewHolder holder, final int position) {
            final ShopDetails shopDetails = shopItemsList.get(position);

            Glide.with(context).load(shopDetails.OWNER_IMAGE).into(holder.owner_image);
            holder.call.setContentDescription(shopDetails.CONTACT_NUMBER);
            holder.next.setTag(shopDetails);

            if(shopDetails.STATUS.equals("OFFLINE")) {
                holder.ShopCard.setAlpha((float)0.4);
                holder.expansionHeader.setEnabled(false);
            }

            holder.shop_name.setText(shopDetails.SHOP_NAME);
            holder.owner_name.setText(shopDetails.OWNER_NAME);
            holder.contact_number.setText(shopDetails.CONTACT_NUMBER);

            if(!shopDetails.AUTHORISED) {
                holder.authorised.setVisibility(View.GONE);
            }

            if(shopDetails.SERVICE_BRANDS.size() > 0) {
                holder.service_brands.setAdapter(new ArrayAdapter<>(context,R.layout.services_list_view_item, shopDetails.SERVICE_BRANDS));
            } else {
                holder.service_brands_layout.setVisibility(View.GONE);
            }


            holder.call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + v.getContentDescription()));
                    if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        Snackbar.make(v, "GRANT CALL PERMISSION IN SETTINGS.", Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                    startActivity(callIntent);
                }
            });

            holder.next.setOnClickListener((view) -> {
                Intent intent = new Intent(context, Shop.class);
                intent.putExtra("SHOP", (ShopDetails)view.getTag());
                intent.putExtra("PRODUCT_NAME", PRODUCT_NAME);
                intent.putExtra("TYPE", TYPE);
                startActivity(intent);
            });


        }

        @Override
        public int getItemCount() {
            return shopItemsList.size();
        }
    }

}