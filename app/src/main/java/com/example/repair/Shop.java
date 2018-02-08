package com.example.repair;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.repair.pojo.Complaint;
import com.example.repair.pojo.STATUS;
import com.example.repair.pojo.ShopDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Shop extends AppCompatActivity {


    private TextView shopName, shopAddress, pincode, hoursOfOperation, mail, website, ownerName, contactNumber;
    private ImageView shopImage, ownerImage;
    private Button serviceBrands, devices, call, book;
    private ShopDetails shopDetails;
    private String PRODUCT_NAME, TYPE;
    private BottomSheetDialog dialog;

    private TextInputEditText problemDescription;
    private Button done;
    private TextView product_name, date, service_type, number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        Intent intent = getIntent();
        shopDetails = (ShopDetails)intent.getSerializableExtra("SHOP");
        PRODUCT_NAME = intent.getStringExtra("PRODUCT_NAME");
        TYPE = intent.getStringExtra("TYPE");


        call = findViewById(R.id.CALL);
        book = findViewById(R.id.BOOK);

        shopImage = findViewById(R.id.SHOP_IMAGE);
        ownerImage = findViewById(R.id.OWNER_IMAGE);
        Glide.with(this).load(shopDetails.SHOP_IMAGE).into(shopImage);
        Glide.with(this).load(shopDetails.OWNER_IMAGE).into(ownerImage);

        serviceBrands = findViewById(R.id.SERVICE_BRANDS);
        devices = findViewById(R.id.DEVICES);
        shopName = findViewById(R.id.SHOP_NAME);
        ownerName = findViewById(R.id.OWNER_NAME);
        contactNumber = findViewById(R.id.CONTACT_NUMBER);
        shopAddress = findViewById(R.id.SHOP_ADDRESS);
        website = findViewById(R.id.WEBSITE);
        mail = findViewById(R.id.MAIL);
        hoursOfOperation = findViewById(R.id.HOURS_OF_OPERATION);
        pincode = findViewById(R.id.PINCODE);

        shopName.setText(shopDetails.SHOP_NAME);
        ownerName.setText(shopDetails.OWNER_NAME);
        contactNumber.setText(shopDetails.CONTACT_NUMBER);
        shopAddress.setText(shopDetails.SHOP_ADDRESS);
        website.setText(shopDetails.WEBSITE);
        mail.setText(shopDetails.MAIL);
        hoursOfOperation.setText(shopDetails.HOURS_OF_OPERATION.replace(" ",""));
        pincode.setText(shopDetails.PINCODE);


        if(shopDetails.SERVICE_BRANDS.size() > 0) {
            serviceBrands.setOnClickListener( v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Services");
                String t[] = new String[shopDetails.SERVICE_BRANDS.size()];
                shopDetails.SERVICE_BRANDS.toArray(t);
                builder.setItems(t,null);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.show();
            });
        } else {
            serviceBrands.setVisibility(View.GONE);
        }

        devices.setOnClickListener( v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Repairs");
            String t[] = new String[shopDetails.DEVICES.size()];
            shopDetails.DEVICES.toArray(t);
            builder.setItems(t,null);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.show();
        });

        call.setOnClickListener(v -> {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + shopDetails.CONTACT_NUMBER));
            if (ActivityCompat.checkSelfPermission(Shop.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                Snackbar.make(v, "GRANT CALL PERMISSION IN SETTINGS.", Snackbar.LENGTH_SHORT).show();
                return;
            }
            startActivity(callIntent);
        });


        View bottomSheetView = getLayoutInflater().inflate(R.layout.location_book_bottom_sheet, null);
        dialog = new BottomSheetDialog(this);
        dialog.setContentView(bottomSheetView);



        product_name = bottomSheetView.findViewById(R.id.product_name);
        date = bottomSheetView.findViewById(R.id.date);
        service_type = bottomSheetView.findViewById(R.id.service_type);
        number = bottomSheetView.findViewById(R.id.number);
        problemDescription = bottomSheetView.findViewById(R.id.problem_description);

        product_name.setText(PRODUCT_NAME);
        service_type.setText(TYPE);
        number.setText(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
        date.setText(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("COMPLAINT");
        final String id = databaseReference.push().getKey();

        bottomSheetView.findViewById(R.id.done).setOnClickListener( v -> {
            String problem = TextUtils.isEmpty(problemDescription.getText().toString()) ? "UNKNOWN" : problemDescription.getText().toString();

            Complaint complaint = new Complaint();
            complaint.PRODUCT_NAME = PRODUCT_NAME;
            complaint.PROBLEM_DESCRIPTION = problem;
            complaint.DATE = date.getText().toString();
            complaint.TYPE = TYPE;
            complaint.STATUS = STATUS.PENDING.toString();
            complaint.CONTACT_NUMBER = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
            complaint.PINCODE = getApplicationContext().getSharedPreferences("REPAIRAPP",0).getString("PINCODE","000000");
            databaseReference.child(shopDetails.SHOP_ID).child(id).setValue(complaint);
            problemDescription.setText("");
            dialog.dismiss();
            Snackbar.make(book,"Service Booked.",Snackbar.LENGTH_LONG).show();
        });
        book.setOnClickListener(v -> dialog.show());


    }

}
