package com.example.repair.pojo;

import java.util.List;

/**
 * Created by doom on 9/1/18.
 */

public class ShopItem {
    public  String shopImage, personImage, open, close,
            shopName, ownerName, landmark, pincode,
            contactNumber, website, fullAddress, alternateContact;
    public List<String> supportedDevices, supportedServices;

    public ShopItem() {
    }
}
