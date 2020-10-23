package com.example.repair.seller;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

/**
 * Created by doom on 3/2/18.
 */

public class SellerObject {
    private static final SellerObject ourInstance = new SellerObject();

    public static SellerObject getInstance() {
        return ourInstance;
    }

    public String SHOP_ID, PINCODE,TYPE;

    public DatabaseReference databaseReference;

    private SellerObject() {
    }
}
