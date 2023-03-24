package com.shopnolive.shopnolive.statusCheck;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StatusModel {
    @SerializedName("switch_status")
    @Expose
    private boolean isOk;

    public boolean isOk() {
        return isOk;
    }
}
