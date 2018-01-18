package com.example.repair;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
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
import com.example.repair.pojo.ServiceShopItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class ShopServicesList extends AppCompatActivity {


    private RecyclerView recyclerView;
    private ArrayList < ServiceShopItem > serviceShopItemsListGlobal;
    private ShopServicesList.ShopsListAdapter mAdapter;
    private HashMap < String, String > favorite;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_services_list);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        Intent intent = getIntent();
        String name = intent.getStringExtra("product_name");
        String location = intent.getStringExtra("location");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(name);

        favorite = new HashMap < String, String > ();

        serviceShopItemsListGlobal = new ArrayList < > ();

        recyclerView = findViewById(R.id.repair_shop_services_list);

        mAdapter = new ShopsListAdapter(this, serviceShopItemsListGlobal);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(mAdapter);
        fetchFavoriteList(name, location);
    }


    private void fetchFavoriteList(final String name, final String location) {

        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog_style);


        String URL = "https://repair-c8047.firebaseio.com/favorite/" + location + ".json";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener < JSONObject > () {

            @Override
            public void onResponse(JSONObject response) {
                Iterator < String > iterator = response.keys();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    try {
                        favorite.put(key, response.getString(key));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                fetchRepairServiceShopsItems(name, location);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getBaseContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        AppController.getInstance().addToRequestQueue(request);
    }

    public void fetchRepairServiceShopsItems(String name, String location) {


        String URL = "https://repair-c8047.firebaseio.com/repair_shops/" + name.toUpperCase() + "/" + location + ".json";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener < JSONObject > () {

            @Override
            public void onResponse(JSONObject response) {

                final ArrayList < ServiceShopItem > shopsItems = new ArrayList < > ();
                Iterator < String > iterator = response.keys();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    try {
                        JSONObject object = response.getJSONObject(key);

                        ServiceShopItem item = new ServiceShopItem();
                        item.setShopId(key);
                        item.setCall(object.getString("call"));
                        item.setFavoriteCount(favorite.get(item.getShopId()));
                        item.setPersonImage(object.getString("personImage"));
                        item.setShopLocation(object.getString("shopLocation"));
                        item.setShopName(object.getString("shopName"));

                        ArrayList<String> services = new ArrayList<>();
                        JSONArray array = object.getJSONArray("supportedServices");
                        for(int i = 0; i < array.length(); i++) {
                            services.add(array.getString(i));
                        }
                        item.setSupportedServices(services);

                        shopsItems.add(item);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                serviceShopItemsListGlobal.clear();
                serviceShopItemsListGlobal.addAll(shopsItems);
                mAdapter.notifyDataSetChanged();
                progressDialog.cancel();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getBaseContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        AppController.getInstance().addToRequestQueue(request);
    }

    public class ShopsListAdapter extends RecyclerView.Adapter < ShopsListAdapter.MyShopViewHolder > {

        private Context context;
        private ArrayList < ServiceShopItem > serviceShopItemsList;

        public class MyShopViewHolder extends RecyclerView.ViewHolder {

            public ImageView person_image, services, call, next;
            public TextView shop_name, shop_location, favorite_count;
            public CardView shopCard;
            public ListView servicesList;
            public ConstraintLayout lowerLayout;
            public LinearLayout lower;


            public MyShopViewHolder(final View itemView) {
                super(itemView);

                lowerLayout = itemView.findViewById(R.id.lower_layout);
                //imageviews

                person_image = itemView.findViewById(R.id.person_image);
                services = itemView.findViewById(R.id.services);
                call = itemView.findViewById(R.id.contacts_);
                next = itemView.findViewById(R.id.next);
                //textviews
                shop_name = itemView.findViewById(R.id.shop_name);
                shop_location = itemView.findViewById(R.id.shop_location);
                favorite_count = itemView.findViewById(R.id.favorite_count);

                shopCard = itemView.findViewById(R.id.shop_card);
                servicesList = itemView.findViewById(R.id.services_list);

                lower = itemView.findViewById(R.id.lower);
            }
        }

        public ShopsListAdapter(Context context, ArrayList < ServiceShopItem > serviceShopsList) {
            this.context = context;
            this.serviceShopItemsList = serviceShopsList;
        }

        @Override
        public MyShopViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_repair_shop_list_item, parent, false);
            return new MyShopViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyShopViewHolder holder, final int position) {
            final ServiceShopItem serviceShopItem = serviceShopItemsList.get(position);
            holder.shop_name.setText(serviceShopItem.getShopName());
            holder.favorite_count.setText(serviceShopItem.getFavoriteCount());
            holder.shop_location.setText(serviceShopItem.getShopLocation());

            Glide.with(context).load(serviceShopItem.getPersonImage()).into(holder.person_image);
            //handle services image rotation
            holder.call.setContentDescription(serviceShopItem.getCall());




            holder.next.setTag(serviceShopItem.getShopId());
            holder.next.setContentDescription(serviceShopItem.getFavoriteCount());



            holder.shopCard.setOnClickListener((view) -> {

                //TransitionManager.beginDelayedTransition(holder.lower);
                if(holder.lowerLayout.getVisibility() == View.VISIBLE) {
                    holder.lowerLayout.setVisibility(View.GONE);
                    holder.services.setRotation(0);
                }
                else {
                    holder.lowerLayout.setVisibility(View.VISIBLE);
                    holder.services.setRotation(180);
                }
                });


            holder.servicesList.setAdapter(new ArrayAdapter<String>(context,R.layout.services_list_view_item,serviceShopItem.getSupportedServices()));

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
                intent.putExtra("SHOP_ID", (String) view.getTag());
                intent.putExtra("FAVORITE",(String) view.getContentDescription());

                startActivity(intent);
            });

        }

        @Override
        public int getItemCount() {
            return serviceShopItemsList.size();
        }
    }

}