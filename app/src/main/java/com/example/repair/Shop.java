package com.example.repair;

import android.app.ProgressDialog;
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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.example.repair.app.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Shop extends AppCompatActivity {


    private TextView time, shopName, ownerName, landmark, pincode, website, fullAddress, alternateContact, favoriteCount;
    private ImageView shopImage, personImage, call, directions, message;
    private ListView supports;
    private ActionBar actionBar;
    private ProgressDialog progressDialog;
    private ConstraintLayout shop;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        actionBar = getSupportActionBar();
        actionBar.setTitle("");

        shopImage = findViewById(R.id.shop_image);
        personImage = findViewById(R.id.owner_image);

        call = findViewById(R.id.call);
        directions = findViewById(R.id.directions);
        message = findViewById(R.id.Message);

        supports = findViewById(R.id.supports);

        shop = findViewById(R.id.SHOP);


        shopName = findViewById(R.id.shop_name);
        ownerName = findViewById(R.id.owner_name);
        favoriteCount = findViewById(R.id.favorite_count);

        alternateContact = findViewById(R.id.alternate_contact);
        fullAddress = findViewById(R.id.location);
        website = findViewById(R.id.website);


        time = findViewById(R.id.hours_of_operation);


        landmark = findViewById(R.id.landmark);
        pincode = findViewById(R.id.pincode);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));



        Intent intent = getIntent();
        String id = intent.getStringExtra("SHOP_ID");
        String favorite = intent.getStringExtra("FAVORITE");


        fetchShopDetails(id);


        favoriteCount.setText(favorite);

        call.setOnClickListener(v -> {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + v.getContentDescription()));
            if (ActivityCompat.checkSelfPermission(Shop.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                Snackbar.make(v, "GRANT CALL PERMISSION IN SETTINGS.", Snackbar.LENGTH_SHORT).show();
                return;
            }
            startActivity(callIntent);
        });

    }


    private void fetchShopDetails(String shoId) {

       progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog_style);

        String URL = "https://repair-c8047.firebaseio.com/shops/"+ shoId +".json";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener <JSONObject> () {

            @Override
            public void onResponse(JSONObject response) {

                try {

                    actionBar.setTitle(response.getString("shopName"));
                    shopName.setText(response.getString("shopName"));
                    ownerName.setText(response.getString("ownerName"));
                    landmark.setText(response.getString("landmark"));
                    pincode.setText(response.getString("pincode"));

                    alternateContact.setText(response.getString("contactNumber") + " , " + response.getString("alternateContact"));
                    fullAddress.setText(response.getString("fullAddress"));
                    website.setText(response.getString("website"));
                    time.setText(response.getString("open") + " - " + response.getString("close"));


                    ArrayList<String> services = new ArrayList<>();
                    JSONArray array = response.getJSONArray("supportedServices");
                    for(int i = 0; i < array.length(); i++) {
                        services.add(array.getString(i));
                    }

                    supports.setAdapter(new ArrayAdapter<String>(Shop.this,R.layout.services_list_view_item,services));



                    call.setContentDescription(response.getString("contactNumber"));



                    //favoriteCount.setText(response.getString());

                    Glide.with(Shop.this).load(response.getString("shopImage")).into(shopImage);
                    Glide.with(Shop.this).load(response.getString("personImage")).into(personImage);

                    shop.setVisibility(View.VISIBLE);
                    progressDialog.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }


        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Shop.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        AppController.getInstance().addToRequestQueue(request);
    }

}
