package com.example.repair.homescreen_fragments;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.repair.R;
import com.example.repair.ShopServicesList;
import com.example.repair.pojo.RepairItem;

import java.util.List;

/**
 * Created by doom on 31/1/18.
 */
class RepairItemAdapter extends RecyclerView.Adapter < RepairItemAdapter.MyViewHolder > {
    private Context context;
    private List< RepairItem > repairItemList;
    private RepairFragment.Pincode PINCODE;
    private String TYPE;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageView thumbnail;
        public CardView cardView;

        public MyViewHolder(final View view) {
            super(view);
            name = view.findViewById(R.id.title);
            thumbnail = view.findViewById(R.id.thumbnail);
            cardView = view.findViewById(R.id.card_view);

        }
    }


    public RepairItemAdapter(Context context, List < RepairItem > repairItemList, RepairFragment.Pincode PINCODE, String type) {
        this.context = context;
        this.repairItemList = repairItemList;
        this.PINCODE = PINCODE;
        this.TYPE = type;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_repair_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final RepairItem repairItem = repairItemList.get(position);
        holder.name.setText(repairItem.getTitle());
        Glide.with(context).load(repairItem.getImage()).into(holder.thumbnail);
        holder.cardView.setContentDescription(repairItem.getTitle());
        holder.thumbnail.setContentDescription(repairItem.getTitle());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewShopsList = new Intent(context, ShopServicesList.class);
                viewShopsList.putExtra("PINCODE",PINCODE.getPINCODE());
                viewShopsList.putExtra("TYPE", RepairItemAdapter.this.TYPE);
                viewShopsList.putExtra("PRODUCT_NAME",v.getContentDescription());
                context.startActivity(viewShopsList);
            }
        });

    }

    @Override
    public int getItemCount() {
        return repairItemList.size();
    }
}
