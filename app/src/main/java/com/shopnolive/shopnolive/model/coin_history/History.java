
package com.shopnolive.shopnolive.model.coin_history;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class History {

    @SerializedName("senderId")
    @Expose
    private String senderId;
    @SerializedName("SUM(coin)")
    @Expose
    private String sUMCoin;

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSUMCoin() {
        return sUMCoin;
    }

    public void setSUMCoin(String sUMCoin) {
        this.sUMCoin = sUMCoin;
    }

}
