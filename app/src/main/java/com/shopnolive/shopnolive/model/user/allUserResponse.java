package com.shopnolive.shopnolive.model.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.shopnolive.shopnolive.model.profile.ProfileData;

import java.util.List;

public class allUserResponse {
    @SerializedName("response")
    @Expose
    private List<ProfileData> response = null;

    public List<ProfileData> getResponse() {
        return response;
    }

    public void setResponse(List<ProfileData> response) {
        this.response = response;
    }
}
