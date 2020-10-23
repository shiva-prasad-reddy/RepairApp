package com.example.repair.seller;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.repair.R;
import com.example.repair.pojo.Complaint;
import com.example.repair.pojo.STATUS;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class Inbox extends Fragment {


    private SellerObject seller;
    private ComplaintBoxAdapter mAdapter;
    private ArrayList<Complaint> complaintArrayList;
    ProgressDialog progressDialog;

    public Inbox() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        seller = SellerObject.getInstance();
        complaintArrayList = new ArrayList<>();
        progressDialog  = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

    }

    public void statusToggle() {

            if(!statusSwitch.isChecked()) {
                statusSwitch.setChecked(true);
                seller.databaseReference.child("STATUS").setValue("ONLINE");
                Toast.makeText(getContext(), "You are ONLINE", Toast.LENGTH_SHORT).show();
            } else {
                statusSwitch.setChecked(false);
                seller.databaseReference.child("STATUS").setValue("OFFLINE");
                Toast.makeText(getContext(), "You are OFFLINE", Toast.LENGTH_SHORT).show();
            }
    }
    Switch statusSwitch;
    CardView statusSwitchCard;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.seller_inbox, container, false);


        statusSwitchCard = view.findViewById(R.id.status_switch_card);
        statusSwitch = view.findViewById(R.id.status_switch);


        statusSwitchCard.setOnClickListener(v -> {
            statusToggle();
        });

        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog_style);
        //place address and description in a drop down

        seller.databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {



                ((TextView) view.findViewById(R.id.CONTACT_NUMBER)).setText(dataSnapshot.child("CONTACT_NUMBER").getValue().toString());
                ((TextView) view.findViewById(R.id.SHOP_ADDRESS)).setText(dataSnapshot.child("SHOP_ADDRESS").getValue().toString());
                ((TextView) view.findViewById(R.id.MAIL)).setText(dataSnapshot.child("MAIL").getValue().toString());
                ((TextView) view.findViewById(R.id.WEBSITE)).setText(dataSnapshot.child("WEBSITE").getValue().toString());

                if(dataSnapshot.child("STATUS").getValue().toString().equals("ONLINE")) {
                    statusSwitch.setChecked(true);
                }


                progressDialog.dismiss();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //error
            }
        });






        RecyclerView complaintBox = view.findViewById(R.id.complaint_box);
        mAdapter = new ComplaintBoxAdapter(getContext(),complaintArrayList);
        //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        //linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        complaintBox.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,true));


        complaintBox.setAdapter(mAdapter);
        complaintBox.setNestedScrollingEnabled(false);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("COMPLAINT");

        databaseReference.child(seller.SHOP_ID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Complaint complaint = dataSnapshot.getValue(Complaint.class);
                //Toast.makeText(getContext(), complaint.CONTACT_NUMBER, Toast.LENGTH_SHORT).show();
                complaint.Complaint_ID = dataSnapshot.getKey();
                complaintArrayList.add(complaint);
                mAdapter.notifyDataSetChanged();
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




    public class ComplaintBoxAdapter extends RecyclerView.Adapter <ComplaintBoxAdapter.ComplaintViewHolder> {

        private Context context;
        private ArrayList<Complaint> complaintsItemsList;

        @Override
        public ComplaintViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.complaint_box_item, parent, false);
            return new ComplaintViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ComplaintViewHolder holder, int position) {

            Complaint complaint = complaintsItemsList.get(position);
            holder.productName.setText(complaint.PRODUCT_NAME);
            holder.address.setText(complaint.ADDRESS);
            holder.date.setText(complaint.DATE);
            holder.type.setText(complaint.TYPE);
            holder.contactNumber.setText(complaint.CONTACT_NUMBER);
            holder.status_text.setText(complaint.STATUS.toString());
            holder.complaintCard.setContentDescription(complaint.Complaint_ID);

            /*
            if(complaint.PROBLEM_DESCRIPTION != null && !complaint.PROBLEM_DESCRIPTION.equals("UNKNOWN")) {
                holder.description.setText(complaint.PROBLEM_DESCRIPTION);
            } else {
                holder.description.setVisibility(View.GONE);
            }
            */
            STATUS status = STATUS.valueOf(complaint.STATUS.trim());
            //change card colors based on status
            switch(status) {
                case PENDING:
                    holder.status.setImageResource(R.drawable.pending);

                    holder.solved.setOnClickListener(v -> {
                        holder.status.setImageResource(R.drawable.resolved);
                        holder.cancel.setVisibility(View.GONE);
                        holder.solved.setVisibility(View.GONE);
                        holder.status_text.setText("RESOLVED");

                        FirebaseDatabase.getInstance().getReference("COMPLAINT").child(seller.SHOP_ID).
                                child(holder.complaintCard.getContentDescription().toString()).
                                child("STATUS").setValue("RESOLVED");

                        //update in firebase in complaint
                    });

                    holder.cancel.setOnClickListener( v -> {
                        holder.status.setImageResource(R.drawable.cancelled);
                        holder.cancel.setVisibility(View.GONE);
                        holder.solved.setVisibility(View.GONE);
                        holder.status_text.setText("CANCELLED");
                        //update in firebase in complaint
                        FirebaseDatabase.getInstance().getReference("COMPLAINT").child(seller.SHOP_ID).
                                child(holder.complaintCard.getContentDescription().toString()).
                                child("STATUS").setValue("CANCELLED");
                    });


                    break;
                case CANCELLED:
                    holder.status.setImageResource(R.drawable.cancelled);
                    holder.cancel.setVisibility(View.GONE);
                    holder.solved.setVisibility(View.GONE);
                    break;
                case RESOLVED:
                    holder.status.setImageResource(R.drawable.resolved);
                    holder.cancel.setVisibility(View.GONE);
                    holder.solved.setVisibility(View.GONE);


                    break;
            }


            holder.call.setContentDescription(complaint.CONTACT_NUMBER);
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


        }

        @Override
        public int getItemCount() {
            return complaintsItemsList.size();
        }

        public class ComplaintViewHolder extends RecyclerView.ViewHolder {

            public ImageView call, status;
            public TextView productName, date, type, contactNumber, status_text, address;
            public CardView complaintCard;
            public Button cancel, solved;


            public ComplaintViewHolder(View view) {
                super(view);
                call = view.findViewById(R.id.callbutton);
                status = view.findViewById(R.id.product_image);
                productName = view.findViewById(R.id.product_name);
                address = view.findViewById(R.id.address);
               // description = view.findViewById(R.id.description);
                date = view.findViewById(R.id.date);
                type = view.findViewById(R.id.type);
                status_text = view.findViewById(R.id.price_icon);
                contactNumber = view.findViewById(R.id.contact_number);
                complaintCard = view.findViewById(R.id.complaint_card);

                cancel = view.findViewById(R.id.cancel_button);
                solved = view.findViewById(R.id.solved_button);

            }
        }

        public ComplaintBoxAdapter(Context context, ArrayList <Complaint> list) {
            this.context = context;
            this.complaintsItemsList = list;

        }

    }


}
