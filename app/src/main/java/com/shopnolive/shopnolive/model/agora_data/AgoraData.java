package com.shopnolive.shopnolive.model.agora_data;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AgoraData {

    @SerializedName("agora_token")
    @Expose
    private String agoraToken;

    public String getAgoraToken() {
        return agoraToken;
    }

    public void setAgoraToken(String agoraToken) {
        this.agoraToken = agoraToken;
    }

}