package com.shopnolive.shopnolive.model.coin;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UpdateCoin {

    @SerializedName("coin")
    @Expose
    private String coin;

    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

}
