package com.shopnolive.shopnolive.model.coin;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ShareCoin {

    @SerializedName("shareId")
    @Expose
    private String shareId;

    @SerializedName("coinAmount")
    @Expose
    private String coinAmount;

    public ShareCoin(String shareId, String coinAmount) {
        this.shareId = shareId;
        this.coinAmount = coinAmount;
    }

    public String getShareId() {
        return shareId;
    }

    public void setShareId(String shareId) {
        this.shareId = shareId;
    }

    public String getCoinAmount() {
        return coinAmount;
    }

    public void setCoinAmount(String coinAmount) {
        this.coinAmount = coinAmount;
    }

}
