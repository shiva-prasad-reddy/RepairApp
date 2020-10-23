package com.example.repair;

import android.content.Context;
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

import com.example.repair.pojo.Complaint;
import com.example.repair.pojo.STATUS;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

public class UserComplaints extends AppCompatActivity {


    private UserComplaintBoxAdapter mAdapter;
    private ArrayList<Complaint> complaintArrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_complaints);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Complaints");
        RecyclerView complaintBox = findViewById(R.id.user_complaint_box);
        complaintArrayList = new ArrayList<>();
        mAdapter = new UserComplaintBoxAdapter(complaintArrayList);
        //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        //linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        complaintBox.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        complaintBox.setAdapter(mAdapter);
        complaintBox.setNestedScrollingEnabled(false);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("USER_DATA");

        databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child("COMPLAINT").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //query those and populate list


                Iterator<DataSnapshot> dataSnapshotIterator = dataSnapshot.getChildren().iterator();
                while(dataSnapshotIterator.hasNext()) {
                    DataSnapshot snapshot = dataSnapshotIterator.next();

                    //Toast.makeText(UserComplaints.this, snapshot.getValue().toString(), Toast.LENGTH_SHORT).show();
                    FirebaseDatabase.getInstance().getReference("COMPLAINT").child(snapshot.getValue().toString()).child(snapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot ds) {
                            //methods goes here

                            if(ds.getValue() != null ) {
                                Complaint complaint = ds.getValue(Complaint.class);
                                complaintArrayList.add(complaint);
                                mAdapter.notifyDataSetChanged();
                            }
                            //user complaints
                            //Toast.makeText(UserComplaints.this, String.valueOf(complaintArrayList.size()), Toast.LENGTH_SHORT).show();

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



    class UserComplaintBoxAdapter extends RecyclerView.Adapter <UserComplaintBoxAdapter.ComplaintViewHolder> {

        private ArrayList<Complaint> complaintsItemsList;

        @Override
        public ComplaintViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_complaint_box_item, parent, false);
            return new ComplaintViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ComplaintViewHolder holder, int position) {

            Complaint complaint = complaintsItemsList.get(position);
            holder.productName.setText(complaint.PRODUCT_NAME);
            holder.date.setText(complaint.DATE);

            holder.status_text.setText(complaint.STATUS.toString());

            STATUS status = STATUS.valueOf(complaint.STATUS.trim());
            //change card colors based on status
            switch(status) {
                case PENDING:
                    holder.status.setImageResource(R.drawable.pending);
                    break;
                case CANCELLED:
                    holder.status.setImageResource(R.drawable.cancelled);
                    break;
                case RESOLVED:
                    holder.status.setImageResource(R.drawable.resolved);
                    break;
            }




        }

        @Override
        public int getItemCount() {
            return complaintsItemsList.size();
        }

        public class ComplaintViewHolder extends RecyclerView.ViewHolder {

            public ImageView status;
            public TextView productName, date, status_text;


            public ComplaintViewHolder(View view) {
                super(view);

                status = view.findViewById(R.id.product_image);
                productName = view.findViewById(R.id.product_name);
                // description = view.findViewById(R.id.description);
                date = view.findViewById(R.id.date);

                status_text = view.findViewById(R.id.price_icon);


            }
        }

        public UserComplaintBoxAdapter(ArrayList <Complaint> list) {
            this.complaintsItemsList = list;

        }

    }

}
