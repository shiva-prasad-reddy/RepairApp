package com.example.repair.seller;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.repair.R;
import com.example.repair.UserOrders;
import com.example.repair.pojo.Order;
import com.example.repair.pojo.OrderStatus;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrdersReceived extends Fragment {



    private SellerObject seller;

    public OrdersReceived() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        seller = SellerObject.getInstance();
        ordersArrayList = new ArrayList<>();

    }

    private OrdersBoxAdapter mAdapter;
    private ArrayList<Order> ordersArrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_orders_received, container, false);

        RecyclerView ordersBox = view.findViewById(R.id.seller_orders_received);
        mAdapter = new OrdersBoxAdapter(ordersArrayList);
        ordersBox.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,true));
        ordersBox.setAdapter(mAdapter);
        ordersBox.setNestedScrollingEnabled(false);

        FirebaseDatabase.getInstance().getReference("ORDERS").child(seller.SHOP_ID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.getValue() != null) {
                    Order order = dataSnapshot.getValue(Order.class);

                    order.ORDER_ID = dataSnapshot.getKey();

                    ordersArrayList.add(order);
                    mAdapter.notifyDataSetChanged();
                }
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


    class OrdersBoxAdapter extends RecyclerView.Adapter <OrdersBoxAdapter.ordersViewHolder> {

        private ArrayList<Order> ordersItemsList;

        @Override
        public ordersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.seller_orders_box_item, parent, false);
            return new ordersViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ordersViewHolder holder, int position) {

            Order order = ordersItemsList.get(position);
            holder.productName.setText(order.DETAILS);
            holder.date.setText(order.DATE);
            holder.price.setText(order.PRICE);
            holder.status.setText(order.STATUS.replace("_"," "));

            Glide.with(getContext()).load(order.IMAGE).into(holder.productImage);



            holder.contact_number.setText(order.CONTACT_NUMBER);
            holder.address.setText(order.ADDRESS);

            holder.call.setContentDescription(order.CONTACT_NUMBER);
            holder.call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + v.getContentDescription()));
                    if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        Snackbar.make(v, "GRANT CALL PERMISSION IN SETTINGS.", Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                    startActivity(callIntent);
                }
            });


            OrderStatus status = OrderStatus.valueOf(order.STATUS.trim());
            //change card colors based on status
            switch(status) {
                case ORDER_PLACED:
                    holder.delivered.setOnClickListener(v -> {
                        holder.cancelled.setVisibility(View.GONE);
                        holder.delivered.setVisibility(View.GONE);
                        holder.status.setText(OrderStatus.DELIVERED.toString());

                        FirebaseDatabase.getInstance().getReference("ORDERS").child(seller.SHOP_ID).
                                child(order.ORDER_ID).
                                child("STATUS").setValue(OrderStatus.DELIVERED.toString());

                        //update in firebase in complaint
                    });

                    holder.cancelled.setOnClickListener( v -> {
                        holder.cancelled.setVisibility(View.GONE);
                        holder.delivered.setVisibility(View.GONE);
                        holder.status.setText(OrderStatus.CANCELLED.toString());

                        FirebaseDatabase.getInstance().getReference("ORDERS").child(seller.SHOP_ID).
                                child(order.ORDER_ID).
                                child("STATUS").setValue(OrderStatus.CANCELLED.toString());
                    });


                    break;
                default:
                    holder.cancelled.setVisibility(View.GONE);
                    holder.delivered.setVisibility(View.GONE);
                    break;
            }




        }

        @Override
        public int getItemCount() {
            return ordersItemsList.size();
        }

        public class ordersViewHolder extends RecyclerView.ViewHolder {

            public ImageView productImage, call;
            public TextView productName, date, price, status, contact_number, address;
            public Button delivered, cancelled;


            public ordersViewHolder(View view) {
                super(view);

                productImage = view.findViewById(R.id.product_image);
                productName = view.findViewById(R.id.product_name);
                // description = view.findViewById(R.id.description);
                date = view.findViewById(R.id.date);
                price = view.findViewById(R.id.price);
                status = view.findViewById(R.id.status_text);

                call = view.findViewById(R.id.call);
                contact_number = view.findViewById(R.id.contact_number);
                address = view.findViewById(R.id.address);
                delivered = view.findViewById(R.id.delivered);
                cancelled = view.findViewById(R.id.cancel_button);


            }
        }

        public OrdersBoxAdapter(ArrayList <Order> list) {
            this.ordersItemsList = list;

        }

    }

}
