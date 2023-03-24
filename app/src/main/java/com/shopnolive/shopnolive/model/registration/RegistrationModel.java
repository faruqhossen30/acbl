package com.shopnolive.shopnolive.model.registration;

import com.google.gson.annotations.SerializedName;
import com.shopnolive.shopnolive.model.profile.ProfileData;

public class RegistrationModel {
    public boolean success = false;
    public String message;
    @SerializedName("data")
    public ProfileData data;

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

    public ProfileData getData() {
        return data;
    }

    public void setData(ProfileData data) {
        this.data = data;
    }
}