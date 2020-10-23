package com.example.repair.seller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.repair.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Seller extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private SellerObject seller;
    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        seller = SellerObject.getInstance();
        DatabaseReference  databaseReference = FirebaseDatabase.getInstance().getReference("SHOPS");
        seller.databaseReference = databaseReference.child(seller.PINCODE).child(seller.TYPE).child(seller.SHOP_ID);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        fragment = new Inbox();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_seller, fragment).commit();
        getData();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fragment.onActivityResult(requestCode,resultCode,data);
    }

    public void getData() {

        seller.databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setDetails(dataSnapshot.child("SHOP_IMAGE").getValue().toString(), dataSnapshot.child("OWNER_IMAGE").getValue().toString(),dataSnapshot.child("SHOP_NAME").getValue().toString(),dataSnapshot.child("OWNER_NAME").getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //error
            }
        });
    }


    public void setDetails(String shopImage, String ownerImage, String shopName, String ownerName) {
        try {
            Glide.with(this).load(ownerImage).into((ImageView) findViewById(R.id.owner_image));
            Glide.with(this).load(shopImage).into((ImageView) findViewById(R.id.shop_image));
            ((TextView) findViewById(R.id.shop_name)).setText(shopName);
            ((TextView) findViewById(R.id.owner_name)).setText(ownerName);
        } catch(IllegalArgumentException | NullPointerException e) {
            getData();
            //Toast.makeText(Seller.this, "Error Occured", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void setToolbarTitle(String msg) {
        toolbar.setTitle(msg);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if(id == R.id.inbox && !(fragment instanceof Inbox)) {
            fragment = new Inbox();
            setToolbarTitle("RepairApp");
        } else if (id == R.id.add_product && !(fragment instanceof NewProduct)) {
            fragment = new NewProduct();
            setToolbarTitle("New Product");
        } else if (id == R.id.remove_product && !(fragment instanceof RemoveProduct)) {
            fragment = new RemoveProduct();
            setToolbarTitle("Remove Product");
        } else if (id == R.id.view_products && !(fragment instanceof ViewProducts)) {
            fragment = new ViewProducts();
            setToolbarTitle("Products");
        }else if (id == R.id.orders && !(fragment instanceof OrdersReceived)) {
            fragment = new OrdersReceived();
            setToolbarTitle("Orders");
        } else if (id == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("REPAIRAPP",0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.commit();
            finish();
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_seller, fragment).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
