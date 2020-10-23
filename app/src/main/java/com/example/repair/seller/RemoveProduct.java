package com.example.repair.seller;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.repair.R;
import com.example.repair.homescreen_fragments.GridSpacingItemDecoration;
import com.example.repair.pojo.Product;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class RemoveProduct extends Fragment {


    public RemoveProduct() {
        // Required empty public constructor
    }




    private RecyclerView remove_products_recyclerView;
    private List<Product> products_ItemList;
    private RemoveProduct.ProductItemAdapter mProductItemAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.remove_product,container, false);

        remove_products_recyclerView = view.findViewById(R.id.remove_products_recycler_view);
        products_ItemList = new ArrayList< >();
        mProductItemAdapter = new RemoveProduct.ProductItemAdapter(getContext(), products_ItemList);
        remove_products_recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        remove_products_recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, 2, true));
        remove_products_recyclerView.setItemAnimator(new DefaultItemAnimator());
        remove_products_recyclerView.setAdapter(mProductItemAdapter);
        remove_products_recyclerView.setNestedScrollingEnabled(false);


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("PRODUCT");

        databaseReference.child(SellerObject.getInstance().SHOP_ID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Product product = dataSnapshot.getValue(Product.class);
                //Toast.makeText(getContext(), complaint.CONTACT_NUMBER, Toast.LENGTH_SHORT).show();

                product.PRODUCT_ID = dataSnapshot.getKey();
                products_ItemList.add(product);
                mProductItemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }



    class ProductItemAdapter extends RecyclerView.Adapter <ProductItemAdapter.MyViewHolder > {
        private Context context;
        private List<Product> productsItemList;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView name, price;
            public ImageView image;
            public Button remove;

            public MyViewHolder(final View view) {
                super(view);
                name = view.findViewById(R.id.product_name);
                image = view.findViewById(R.id.product_image);
                price = view.findViewById(R.id.product_price);
                remove = view.findViewById(R.id.remove);
            }
        }


        public ProductItemAdapter(Context context, List <Product> productsItemList) {
            this.context = context;
            this.productsItemList = productsItemList;
        }

        @Override
        public ProductItemAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.remove_product_single_item, parent, false);

            return new ProductItemAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ProductItemAdapter.MyViewHolder holder, final int position) {
            final Product product = productsItemList.get(position);
            holder.name.setText(product.NAME);
            Glide.with(context).load(product.IMAGE_ONE).into(holder.image);
            holder.price.setText(product.PRICE);


            holder.remove.setContentDescription(product.PRODUCT_ID);
            holder.remove.setTag(position);
            holder.remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseDatabase.getInstance().getReference("PRODUCT").child(SellerObject.getInstance().SHOP_ID).child(v.getContentDescription().toString()).setValue(null);
                    productsItemList.remove((int)v.getTag());
                    notifyDataSetChanged();
                }
            });

        }

        @Override
        public int getItemCount() {
            return productsItemList.size();
        }
    }


}
