package com.example.repair.pojo;

import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by doom on 2/1/18.
 */

public class ServiceShopItem {

    private String shopId, shopName, shopLocation, favoriteCount, personImage, call;

    public ArrayList<String> supportedServices;
    public ServiceShopItem() {}

    public ServiceShopItem(String shopName, String shopLocation, String favoriteCount,
                           String personImage, String call) {
        this.shopName = shopName;
        this.shopLocation = shopLocation;
        this.favoriteCount = favoriteCount;
        this.personImage = personImage;
        this.call = call;

    }

    //setters


    public void setSupportedServices(ArrayList<String> supportedServices) {
        this.supportedServices = supportedServices;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public void setCall(String call) {
        this.call = call;
    }

    public void setFavoriteCount(String favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public void setPersonImage(String personImage) {
        this.personImage = personImage;
    }

    public void setShopLocation(String shopLocation) {
        this.shopLocation = shopLocation;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    //getters


    public ArrayList<String> getSupportedServices() {
        return supportedServices;
    }

    public String getShopId() {
        return shopId;
    }

    public String getCall() {
        return call;
    }

    public String getFavoriteCount() {
        return favoriteCount;
    }

    public String getPersonImage() {
        return personImage;
    }

    public String getShopLocation() {
        return shopLocation;
    }

    public String getShopName() {
        return shopName;
    }




}
