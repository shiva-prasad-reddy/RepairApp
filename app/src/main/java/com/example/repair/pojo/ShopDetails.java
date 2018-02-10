package com.example.repair.pojo;

import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by doom on 2/1/18.
 */

public class ShopDetails implements Serializable {

    public String SHOP_ID, SHOP_NAME, OWNER_NAME, OWNER_IMAGE, CONTACT_NUMBER;
    public String STATUS;
    public boolean AUTHORISED;
    public ArrayList<String> SERVICE_BRANDS, DEVICES;
    public ShopDetails() {
        SERVICE_BRANDS = new ArrayList<>();
        DEVICES = new ArrayList<>();
    }

    public String SHOP_ADDRESS, PINCODE, HOURS_OF_OPERATION, MAIL, WEBSITE, SHOP_IMAGE;



}
