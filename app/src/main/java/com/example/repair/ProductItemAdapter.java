package com.example.repair;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.example.repair.pojo.Product;

import java.util.List;

/**
 * Created by doom on 10/2/18.
 */

public class ProductItemAdapter extends RecyclerView.Adapter <ProductItemAdapter.MyViewHolder > {
    private Activity context;
    private List<Product> productsItemList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, price;
        public ImageView image;
        public CardView card;

        public MyViewHolder(final View view) {
            super(view);
            name = view.findViewById(R.id.product_name);
            image = view.findViewById(R.id.product_image);
            price = view.findViewById(R.id.product_price);
            card = view.findViewById(R.id.card_purchase);
        }
    }


    public ProductItemAdapter(Activity context, List <Product> productsItemList) {
        this.context = context;
        this.productsItemList = productsItemList;
    }

    @Override
    public ProductItemAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_single_item, parent, false);

        return new ProductItemAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ProductItemAdapter.MyViewHolder holder, final int position) {
        final Product product = productsItemList.get(position);
        holder.name.setText(product.NAME);
        Glide.with(context).load(product.IMAGE_ONE).into(holder.image);
        holder.price.setText(product.PRICE);
        holder.card.setTag(product);
        holder.card.setOnClickListener(v -> {
            //Launch Purchase Product Intent
            Intent intent = new Intent(context,ProductPurchase.class);
            intent.putExtra("PRODUCT",(Product)v.getTag());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return productsItemList.size();
    }
}
