package com.shopnolive.shopnolive.model.gift;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GiftHistoryResponse {
    public boolean success = false;
    public String message;
    @SerializedName("data")
    @Expose
    private List<GiftHistoryItem> data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<GiftHistoryItem> getData() {
        return data;
    }

    public void setData(List<GiftHistoryItem> data) {
        this.data = data;
    }
}
