package com.shopnolive.shopnolive.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class GiftListResponse {
    public boolean success = false;
    public String message;
    @SerializedName("data")
    public List<GiftListData> data;

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

    public List<GiftListData> getData() {
        return data;
    }

    public void setData(List<GiftListData> data) {
        this.data = data;
    }
}
