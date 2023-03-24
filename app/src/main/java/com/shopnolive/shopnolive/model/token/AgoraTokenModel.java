package com.shopnolive.shopnolive.model.token;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.shopnolive.shopnolive.model.agora_data.AgoraData;

public class AgoraTokenModel {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private AgoraData data;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public AgoraData getData() {
        return data;
    }

    public void setData(AgoraData data) {
        this.data = data;
    }

}
