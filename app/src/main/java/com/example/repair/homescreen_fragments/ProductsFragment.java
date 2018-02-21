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

import com.example.repair.ProductItemAdapter;
import com.example.repair.R;
import com.example.repair.Shop;
import com.example.repair.pojo.Product;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProductsFragment extends Fragment {


    public ProductsFragment() {
        // Required empty public constructor
    }

    private RecyclerView view_products_recyclerView;
    private List<Product> products_ItemList;
    private ProductItemAdapter mProductItemAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.products_fragment, container, false);

        view_products_recyclerView = view.findViewById(R.id.fragment_view_products_recycler_view);
        products_ItemList = new ArrayList< >();
        mProductItemAdapter = new ProductItemAdapter(getActivity(), products_ItemList);
        view_products_recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        view_products_recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, 2, true));
        view_products_recyclerView.setItemAnimator(new DefaultItemAnimator());
        view_products_recyclerView.setAdapter(mProductItemAdapter);
        view_products_recyclerView.setNestedScrollingEnabled(false);



        FirebaseDatabase.getInstance().getReference("PRODUCT").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


               Iterator<DataSnapshot> itr =  dataSnapshot.getChildren().iterator();
               while(itr.hasNext()) {
                   DataSnapshot snapshot = itr.next();
                   String shop_id = snapshot.getKey();

                   Iterator<DataSnapshot> productIterator = snapshot.getChildren().iterator();
                   while(productIterator.hasNext()) {
                       DataSnapshot snap = productIterator.next();
                       Product product = snap.getValue(Product.class);
                       product.SHOP_ID = shop_id;
                       product.PRODUCT_ID = snap.getKey();
                       products_ItemList.add(product);

                   }
                   mProductItemAdapter.notifyDataSetChanged();
               }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return view;

    }

}
