package com.example.repair;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.repair.pojo.Order;
import com.example.repair.pojo.OrderStatus;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

public class UserOrders extends AppCompatActivity {

    private UserordersBoxAdapter mAdapter;
    private ArrayList<Order> ordersArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_orders);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Orders");

        RecyclerView ordersBox = findViewById(R.id.user_orders_box);
        ordersArrayList = new ArrayList<>();
        mAdapter = new UserordersBoxAdapter(this,ordersArrayList);
        //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        //linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        ordersBox.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        ordersBox.setAdapter(mAdapter);
        ordersBox.setNestedScrollingEnabled(false);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("USER_DATA");

        databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child("ORDERS").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //query those and populate list


                Iterator<DataSnapshot> dataSnapshotIterator = dataSnapshot.getChildren().iterator();
                while(dataSnapshotIterator.hasNext()) {
                    DataSnapshot snapshot = dataSnapshotIterator.next();

                    //Toast.makeText(Userorderss.this, snapshot.getValue().toString(), Toast.LENGTH_SHORT).show();
                    FirebaseDatabase.getInstance().getReference("ORDERS").child(snapshot.getValue().toString()).child(snapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot ds) {
                            //methods goes here
                            if(ds.getValue() != null) {
                                Order orders = ds.getValue(Order.class);
                                ordersArrayList.add(orders);
                                mAdapter.notifyDataSetChanged();
                            }

                            //user orderss
                            //Toast.makeText(UserOrders.this, ds.getValue().toString(), Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });




                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }



    class UserordersBoxAdapter extends RecyclerView.Adapter <UserordersBoxAdapter.ordersViewHolder> {

        private Context context;
        private ArrayList<Order> ordersItemsList;

        @Override
        public ordersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_orders_box_item, parent, false);
            return new ordersViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ordersViewHolder holder, int position) {

            Order order = ordersItemsList.get(position);
           holder.productName.setText(order.DETAILS);
            holder.date.setText(order.DATE);
            holder.price.setText(order.PRICE);
            holder.status.setText(order.STATUS.replace("_"," "));

            Glide.with(UserOrders.this).load(order.IMAGE).into(holder.productImage);

            OrderStatus status = OrderStatus.valueOf(order.STATUS.trim());
            //change card colors based on status
            switch(status) {
                case DELIVERED:
                    holder.status.setTextColor(Color.GREEN);
                    break;
                case ORDER_PLACED:
                    holder.status.setTextColor(Color.BLUE);
                    break;
                case CANCELLED:
                    holder.status.setTextColor(Color.RED);
                    break;
            }




        }

        @Override
        public int getItemCount() {
            return ordersItemsList.size();
        }

        public class ordersViewHolder extends RecyclerView.ViewHolder {

            public ImageView productImage;
            public TextView productName, date, price, status;


            public ordersViewHolder(View view) {
                super(view);

                productImage = view.findViewById(R.id.product_image);
                productName = view.findViewById(R.id.product_name);
                // description = view.findViewById(R.id.description);
                date = view.findViewById(R.id.date);
                price = view.findViewById(R.id.price);
                status = view.findViewById(R.id.status_text);


            }
        }

        public UserordersBoxAdapter(Context context, ArrayList <Order> list) {
            this.context = context;
            this.ordersItemsList = list;

        }

    }

}
