package com.example.repair.seller;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.repair.R;
import com.example.repair.pojo.Product;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewProduct extends Fragment {



    private Uri filePathImageOne, filePathImageTwo;
    private final int PICK_IMAGE_REQUEST_ONE = 9999;
    private final int PICK_IMAGE_REQUEST_TWO = 8888;
    private ProgressDialog progressDialog;

    private ImageView imageOne, imageTwo;
    private TextInputEditText name, price, brand, description;
    private Spinner type;

    private Button upload;


    public NewProduct() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog  = new ProgressDialog(getContext());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Uploading...");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.new_product,container, false);

        imageOne = view.findViewById(R.id.img_one);
        imageTwo = view.findViewById(R.id.img_two);

        name = view.findViewById(R.id.product_name);
        price = view.findViewById(R.id.price_icon);
        brand = view.findViewById(R.id.brand);
        description = view.findViewById(R.id.description);
        type = view.findViewById(R.id.type);
        upload = view.findViewById(R.id.upload);

       setClickListeners();

        return view;
    }

    private void setClickListeners() {

        imageOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage(PICK_IMAGE_REQUEST_ONE);
            }
        });
        imageTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage(PICK_IMAGE_REQUEST_TWO);
            }
        });


        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkAll()) {
                    uploadImageOne();
                } else {
                    Toast.makeText(getContext(),"Fill All Details",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private boolean checkAll() {

        boolean a = filePathImageOne != null;
        boolean b = filePathImageTwo != null;
        boolean c = !(TextUtils.isEmpty(name.getText().toString()));
        boolean d = !(TextUtils.isEmpty(price.getText().toString()));
        boolean e = !(TextUtils.isEmpty(brand.getText().toString()));

        return a && b && c && d && e;

    }

    private void uploadImageOne() {

        if(filePathImageOne != null && filePathImageTwo!= null)
        {

            progressDialog.show();


            Product product = new Product();

            StorageReference ref = FirebaseStorage.getInstance().getReference().child(UUID.randomUUID().toString()+".jpg");
            ref.putFile(filePathImageOne)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            product.IMAGE_ONE = taskSnapshot.getDownloadUrl().toString();
                            uploadImageTwoAndData(product);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(),"Image Upload Failed",Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void uploadImageTwoAndData(Product product) {

        StorageReference ref = FirebaseStorage.getInstance().getReference().child(UUID.randomUUID().toString()+".jpg");
        ref.putFile(filePathImageTwo)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        product.IMAGE_TWO = taskSnapshot.getDownloadUrl().toString();
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("PRODUCT");
                        String id = databaseReference.push().getKey();

                        product.SHOP_TYPE = SellerObject.getInstance().TYPE;
                        product.SHOP_PINCODE = SellerObject.getInstance().PINCODE;

                        product.NAME = name.getText().toString();
                        product.BRAND = brand.getText().toString();
                        product.PRICE = price.getText().toString();
                        product.TYPE = String.valueOf(type.getSelectedItem());
                        product.DESCRIPTION = TextUtils.isEmpty(description.getText().toString()) ? "UNKNOWN" : description.getText().toString();

                        databaseReference.child(SellerObject.getInstance().SHOP_ID).child(id).setValue(product);

                        Toast.makeText(getContext(),"Upload Done",Toast.LENGTH_SHORT).show();
                        clearAll();
                        progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(),"Image Upload Failed",Toast.LENGTH_SHORT).show();
                    }
                });
    }






    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == PICK_IMAGE_REQUEST_ONE && resultCode == RESULT_OK && data != null && data.getData() != null )
        {
            filePathImageOne = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), filePathImageOne);
                imageOne.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        if(requestCode == PICK_IMAGE_REQUEST_TWO && resultCode == RESULT_OK && data != null && data.getData() != null )
        {
            filePathImageTwo = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), filePathImageTwo);
                imageTwo.setImageBitmap(bitmap);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }





    public void clearAll() {

        name.getText().clear();
        price.getText().clear();
        brand.getText().clear();
        description.getText().clear();

        imageOne.setImageResource(R.drawable.ic_camera_black_24dp);
        imageTwo.setImageResource(R.drawable.ic_camera_black_24dp);

        filePathImageOne = filePathImageTwo = null;
    }


    public void chooseImage(int request) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), request);
    }




}
