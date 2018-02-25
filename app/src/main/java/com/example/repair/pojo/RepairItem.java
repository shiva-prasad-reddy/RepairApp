package com.example.repair.pojo;

/**
 * Created by doom on 31/12/17.
 */

public class RepairItem {

    String title;
    int image;

    public String getTitle() {
        return title;
    }

   // public void setTitle(String title) {
    //    this.title = title;
    //}

    public int getImage() {
        return image;
    }


    public RepairItem() {}
    public RepairItem(String title, int image) {
        this.title = title;
        this.image =  image;
    }


}
