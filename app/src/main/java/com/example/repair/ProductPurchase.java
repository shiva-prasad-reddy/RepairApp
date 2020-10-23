package com.example.repair;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.repair.pojo.Order;
import com.example.repair.pojo.OrderStatus;
import com.example.repair.pojo.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tmall.ultraviewpager.UltraViewPager;

import java.text.SimpleDateFormat;
import java.util.Date;


public class ProductPurchase extends AppCompatActivity {

    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_purchase);

        product = (Product)getIntent().getSerializableExtra("PRODUCT");


        ((TextView)findViewById(R.id.price_icon)).setText(product.PRICE);
        ((TextView)findViewById(R.id.name)).setText(product.NAME);
        ((TextView)findViewById(R.id.brand)).setText(product.BRAND);

        if(product.DESCRIPTION.equals("UNKNOWN")) {
            ((TextView)findViewById(R.id.description)).setVisibility(View.GONE);
        } else {
            ((TextView)findViewById(R.id.description)).setText(product.DESCRIPTION);
        }
        ((ImageView)findViewById(R.id.back_button)).setOnClickListener( v -> {
            this.onBackPressed();
        });

        UltraViewPager ultraViewPager = (UltraViewPager)findViewById(R.id.ultra_viewpager);
        ultraViewPager.setScrollMode(UltraViewPager.ScrollMode.HORIZONTAL);
        //initialize UltraPagerAdapter，and add child view to UltraViewPager
        PagerAdapter adapter = new UltraPagerAdapter(false);
        ultraViewPager.setAdapter(adapter);

        //initialize built-in indicator
        ultraViewPager.initIndicator();
        //set style of indicators
        ultraViewPager.getIndicator()
                .setOrientation(UltraViewPager.Orientation.HORIZONTAL)
                .setFocusColor(Color.GREEN)
                .setNormalColor(Color.WHITE)
                .setRadius((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, getResources().getDisplayMetrics()));
        //set the alignment
        ultraViewPager.getIndicator().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        //construct built-in indicator, and add it to  UltraViewPager
        ultraViewPager.getIndicator().build();

        //set an infinite loop
        ultraViewPager.setInfiniteLoop(true);


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ORDERS");
        final String id = databaseReference.push().getKey();


        ((Button) findViewById(R.id.BUY)).setOnClickListener( v -> {

            Order order = new Order();
            order.PRODUCT_ID = product.PRODUCT_ID;
            order.DATE = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

            order.DETAILS = product.NAME;
            order.PRICE = product.PRICE;
            order.IMAGE = product.IMAGE_ONE;

            order.CONTACT_NUMBER = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
            order.PINCODE = getApplicationContext().getSharedPreferences("REPAIRAPP",0).getString("PINCODE","000000");
            order.ADDRESS = getApplicationContext().getSharedPreferences("REPAIRAPP",0).getString("ADDRESS","NOT AVAILABLE");

            order.STATUS = OrderStatus.ORDER_PLACED.toString();

            databaseReference.child(product.SHOP_ID).child(id).setValue(order);
            FirebaseDatabase.getInstance().getReference("USER_DATA").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child("ORDERS").child(id).setValue(product.SHOP_ID);

            Snackbar.make(v,"Order Placed.",Snackbar.LENGTH_LONG).show();


        });

    }

    class UltraPagerAdapter extends PagerAdapter {
        private boolean isMultiScr;
        public UltraPagerAdapter(boolean isMultiScr) {
            this.isMultiScr = isMultiScr;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(container.getContext()).inflate(R.layout.layout_child_product, null);
            //new LinearLayout(container.getContext());
            ImageView imageView = linearLayout.findViewById(R.id.product_image);
            container.addView(linearLayout);
            switch(position) {
                case 0:
                    Glide.with(ProductPurchase.this).load(product.IMAGE_ONE).into(imageView);
                    break;
                case 1:
                    Glide.with(ProductPurchase.this).load(product.IMAGE_TWO).into(imageView);
                    break;
            }

            return linearLayout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            LinearLayout view = (LinearLayout) object;
            container.removeView(view);
        }
    }
}
