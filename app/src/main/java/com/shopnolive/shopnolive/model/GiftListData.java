package com.shopnolive.shopnolive.model;

import com.google.gson.annotations.SerializedName;

public class GiftListData {

    @SerializedName("diamond")
    private int diamond;

    @SerializedName("comission")
    private int comission;

    @SerializedName("image")
    private String imageUrl;

    public int getDiamond() {
        return diamond;
    }

    public void setDiamond(int diamond) {
        this.diamond = diamond;
    }

    public int getComission() {
        return comission;
    }

    public void setComission(int comission) {
        this.comission = comission;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
