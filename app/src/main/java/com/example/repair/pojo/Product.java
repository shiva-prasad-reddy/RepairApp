package com.example.repair.pojo;

import java.io.Serializable;

/**
 * Created by doom on 20/1/18.
 */

public class Product implements Serializable {

    public String SHOP_TYPE, SHOP_PINCODE,
                  NAME, IMAGE_ONE, IMAGE_TWO, PRICE, TYPE, BRAND, DESCRIPTION;

    public Product() {}

    public String PRODUCT_ID, SHOP_ID;

}
