package com.shopnolive.shopnolive.model;

import com.google.gson.annotations.SerializedName;
import com.shopnolive.shopnolive.model.profile.ProfileData;

import java.util.List;

public class UserListResponse {
    public boolean success = false;
    public String message;
    @SerializedName("data")
    public List<ProfileData> data;

    public UserListResponse(boolean success, String message, List<ProfileData> data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

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

    public List<ProfileData> getData() {
        return data;
    }

    public void setData(List<ProfileData> data) {
        this.data = data;
    }
}
